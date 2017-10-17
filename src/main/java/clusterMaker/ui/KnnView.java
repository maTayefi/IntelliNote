/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions, and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions, and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 3. Redistributions must acknowledge that this software was
 * originally developed by the UCSF Computer Graphics Laboratory
 * under support by the NIH National Center for Research Resources,
 * grant P41-RR01081.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package clusterMaker.ui;

// System imports

// Cytoscape imports

import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.ClusterResults;
import clusterMaker.treeview.KnnViewFrame;
import clusterMaker.treeview.PropertyConfig;
import clusterMaker.treeview.model.KnnViewModel;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;
import cytoscape.view.CytoscapeDesktop;

// Giny imports
// ClusterMaker imports
// TreeView imports

/**
 * The ClusterViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class KnnView extends TreeView {
    private CyLogger myLogger;

    private static String appName = "ClusterMaker KnnView";

    public KnnView() {
        super();
        myLogger = CyLogger.getLogger(KnnView.class);
    }

    public KnnView(PropertyConfig propConfig) {
        super(propConfig);
        myLogger = CyLogger.getLogger(KnnView.class);
    }

    public String getAppName() {
        return appName;
    }

    // ClusterViz methods
    public String getShortName() {
        return "knnview";
    }

    public String getName() {
        return "Eisen KnnView";
    }

    public ClusterResults getResults() {
        return null;
    }

    public void initializeProperties() {
    }

    public boolean isAvailable() {
        CyNetwork network = Cytoscape.getCurrentNetwork();
        CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
        String netId = network.getIdentifier();
        if (networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE)) {
            String type = networkAttributes.getStringAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE);
            // TODO why is KnnView deciding which algorithms are visualizable, rather than having each algorithm decide whether itself is visualizable?
            if (!type.equals("kmeans") &&
                    !type.equals("kmedoid") &&
                    !type.equals("autosome_heatmap") &&
                    !type.equals("PAM") &&
                    !type.equals("HOPACH")
                    )
                return false;
        }

        if (networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_NODE_ATTRIBUTE) ||
                networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_ATTR_ATTRIBUTE)) {
            return true;
        }
        return false;
    }

    public CyCommandResult startViz() throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        if (isAvailable()) {
            KnnView kv = new KnnView();  // Clone ourselves
            kv.startup();
            result.addMessage("Knn View displayed");
        } else {
            throw new CyCommandException("No K-Means compatible cluster available");
        }
        return result;
    }

    protected void startup() {
        // Get our data model
        dataModel = new KnnViewModel(myLogger);

        // Set up the global config
        setConfigDefaults(new PropertyConfig(globalConfigName(), "ProgramConfig"));

        // Set up our configuration
        PropertyConfig documentConfig = new PropertyConfig(getShortName(), "DocumentConfig");
        dataModel.setDocumentConfig(documentConfig);

        // Create our view frame
        KnnViewFrame frame = new KnnViewFrame(this, appName);

        // Set the data model
        frame.setDataModel(dataModel);
        frame.setLoaded(true);
        frame.addWindowListener(this);
        frame.setVisible(true);
        geneSelection = frame.getGeneSelection();
        geneSelection.addObserver(this);
        arraySelection = frame.getArraySelection();
        arraySelection.addObserver(this);

        // Now set up to receive selection events
        myView = Cytoscape.getCurrentNetworkView();
        myNetwork = Cytoscape.getCurrentNetwork();
        myView.addGraphViewChangeListener(this);

        // Set up to listen for changes in the network view
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().
                addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUS, this);
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().
                addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
    }
}

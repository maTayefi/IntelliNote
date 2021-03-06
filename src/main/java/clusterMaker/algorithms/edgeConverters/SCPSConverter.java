/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
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
package clusterMaker.algorithms.edgeConverters;

public class SCPSConverter implements EdgeWeightConverter {

    /**
     * Get the short name of this converter
     *
     * @return short-hand name for converter
     */
    public String getShortName() {
        return "SCPS";
    }

    public String toString() {
        return "SCPS";
    }

    /**
     * Get the name of this converter
     *
     * @return name for converter
     */
    public String getName() {
        return "SCPS convertion (assumes edge weights are BLAST e-values)";
    }

    /**
     * Convert an edge weight
     *
     * @param weight the edge weight to convert
     * @param minValue the minimum value over all edge weights
     * @param maxValue the maximum value over all edge weights
     * @return the converted edge weight
     */
    public double convert(double weight, double minValue, double maxValue) {

        double w = 6.1302;
        double b = 1.2112;

        weight = 1 / (1 + Math.exp(w * Math.log10(weight) + b));
        return weight;
    }
}

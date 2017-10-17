/**
 *
 */
package clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.layout.parameter_training;

import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.datastructure.ConnectedComponent;
import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.layout.IParameters;
import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.layout.LayoutFactory;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * @author sita
 */
public interface IParameterTraining {


    public void initialise(LayoutFactory.EnumLayouterClass enumType, int generationsSize,
                           int noOfGenerations);

    public IParameters run(ConnectedComponent cc);

    public void setMaxThreadSemaphoreAndThreadsList(Semaphore semaphore, ArrayList<Thread> allThreads);

}

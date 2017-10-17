/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusterMaker.algorithms.networkClusterers.glay;

/**
 * @author Gang Su
 */
public interface GAlgorithm {

    public abstract double getModularity();

    public int[] getMembership();

}

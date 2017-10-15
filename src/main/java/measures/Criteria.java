package measures;

import utilities.Utility;

public class Criteria {
    double purity;
    double entropy;
    double v_meas;
    double rand;
    double f_meas;

    public static void evaluateClustering() {
        Double[][] resultMatrix = Utility.readMems();
        ClusterEvaluator clusterEvaluator = new ClusterEvaluator();
        ContingencyTable contingencyTable = new ContingencyTable(459, 6);
        contingencyTable.setData(resultMatrix);
        clusterEvaluator.setData(contingencyTable);
        System.out.println("Purity: " + clusterEvaluator.getPurity());
        System.out.println("FMeasure: " + clusterEvaluator.getFMeasure());
        System.out.println("VMeasure: " + clusterEvaluator.getVMeasure(0.01));
        System.out.println("RandIndex: " + clusterEvaluator.getRandIndex());

    }

}

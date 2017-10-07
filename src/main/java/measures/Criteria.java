package measures;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Criteria {
    double purity;
    double entropy;
    double v_meas;
    double rand;
    double f_meas;

    public static void evaluateClustering() {
        Double[][] resultMatrix = new Double[459][6];
        try {
            Scanner s = new Scanner(new File("answerForRueterswithN6.txt"));
            int column = 0;
            int row = 0;
            while (s.hasNextDouble()) {
                //System.out.println(str+"-"+column+"-"+row);
                resultMatrix[row][column] = s.nextDouble();
                column++;
                if (column == 6) {
                    column = 0;
                    row++;
                }
            }
            //System.out.println(Arrays.deepToString(simMatrix));
            s.close();
            // At this point all dead cells are 0 and all live are 1
        } catch (IOException i) {
            System.out.println("Problems..");

        }
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

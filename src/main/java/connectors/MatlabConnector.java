package connectors;

import matlabcontrol.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by maTayefi on 2/28/2017.
 */
public class MatlabConnector {

    public static void runClustering() throws MatlabConnectionException, MatlabInvocationException {

        //read simmatrix
        double simMatrix[][] = new double[459][459];
        try {
            Scanner s = new Scanner(new File("SimMatrix.txt"));
            int row = 0;
            int column = 0;
            while (s.hasNext()) {
                String str = s.next();
                //if(str.contains("[")){
                // column++;
                //row=0;
                //}
                str = str.replaceAll("[,\\[\\]]", "");
                //System.out.println(str+"-"+column+"-"+row);
                simMatrix[row][column] = Double.valueOf(str);
                column++;
                if (column == 459) {
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

        MatlabProxyFactoryOptions options =
                new MatlabProxyFactoryOptions.Builder()
                        .setUsePreviouslyControlledSession(true)
                        .build();


        MatlabProxyFactory factory = new MatlabProxyFactory(options);
        MatlabProxy proxy = factory.getProxy();

        // call user-defined function (must be on the path)

        String matrixForMatlab = "";
        for (int i = 0; i < 459; i++) {
            for (int j = 0; j < 459; j++) {
                if (j == 0) matrixForMatlab = matrixForMatlab.concat(" ");
                matrixForMatlab = matrixForMatlab.concat(String.valueOf(simMatrix[i][j]));
                if (j != 458) matrixForMatlab = matrixForMatlab.concat(" ");
                else matrixForMatlab = matrixForMatlab.concat(";");
            }
        }


        proxy.eval("addpath('D:\\Thesis\\IntelliNote\\')");
        proxy.eval("n_clusters=6;");
        proxy.eval("similarities=[" + matrixForMatlab + "];");
        //proxy.feval("frecca(similarities,n_clusters)");
        proxy.eval("frecca(similarities,n_clusters)");
        proxy.eval("rmpath('D:\\Thesis\\IntelliNote\\')");
        //proxy.setVariable("similarities",simMatrix);
        //proxy.setVariable("n_clusters",5);
        //proxy.feval("D:\\Thesis\\IntelliNote\\frecca.m");

        System.out.println("ans " + proxy.getVariable("ans").toString());
        double[][] mems = (double[][]) proxy.getVariable("mems");
        double[][] pj = (double[][]) proxy.getVariable("pj");
        double[] cij = (double[]) proxy.getVariable("cij");
        // close connection
        proxy.disconnect();

        //Future<MatlabEngine> eng = MatlabEngine.startMatlabAsync();
        //MatlabEngine ml = eng.get();
        //ml.putVariable("SIMILARITIES",simMatrix);
        //ml.putVariable("N_CLUSTERS",5);
        //Scanner scanner=new Scanner(new File("frecca.m"));
        //ml.feval(scanner.toString());
        //String function="";
        //while(scanner.hasNextLine()){
        //  function=function.concat(scanner.nextLine());
        //ml.feval(scanner.nextLine());
        //}
        //ml.feval(function);
        //double[][] mems=ml.getVariable("MEMS");
        //double[][] pj=ml.getVariable("PJ");
        //double[] cij=ml.getVariable("CIJ");
        System.out.println("mems " + Arrays.deepToString(mems));
        System.out.println("pj " + Arrays.deepToString(pj));
        System.out.println("cij " + Arrays.toString(cij));
        // Disconnect from the MATLAB session
        //ml.disconnect();
    }
}

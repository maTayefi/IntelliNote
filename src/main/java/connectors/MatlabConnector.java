package connectors;

import com.mathworks.engine.MatlabEngine;
import matlabcontrol.*;
import utilities.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static utilities.Utility.fLogger;

/**
 * Created by maTayefi on 2/28/2017.
 */
public class MatlabConnector {

    public static void runClustering() throws MatlabConnectionException, MatlabInvocationException, ExecutionException, InterruptedException {

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

        String matrixForMatlab = "";
        for (int i = 0; i < 459; i++) {
            for (int j = 0; j < 459; j++) {
                if (j == 0) matrixForMatlab = matrixForMatlab.concat(" ");
                matrixForMatlab = matrixForMatlab.concat(String.valueOf(simMatrix[i][j]));
                if (j != 458) matrixForMatlab = matrixForMatlab.concat(" ");
                else matrixForMatlab = matrixForMatlab.concat(";");
            }
        }

        if (Utility.matlabConnectionMethod == 0) {
            MatlabProxyFactoryOptions options =
                    new MatlabProxyFactoryOptions.Builder()
                            .setUsePreviouslyControlledSession(true)
                            .build();

            MatlabProxyFactory factory = new MatlabProxyFactory(options);
            MatlabProxy proxy = factory.getProxy();


            proxy.eval("addpath('D:\\Thesis\\IntelliNote\\src\\main\\resources\\')");
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

            System.out.println("mems " + Arrays.deepToString(mems));
            System.out.println("pj " + Arrays.deepToString(pj));
            System.out.println("cij " + Arrays.toString(cij));
            proxy.disconnect();
        } else if (Utility.matlabConnectionMethod == 1) {
            Future<MatlabEngine> eng = MatlabEngine.startMatlabAsync();
            MatlabEngine ml = eng.get();
            ml.putVariableAsync("similarities", simMatrix);
            ml.putVariableAsync("n_clusters", Utility.no_of_clusters);

            ml.eval("addpath('D:\\Thesis\\IntelliNote\\src\\main\\resources\\')");
            //khat be khat behesh bedeh ba ml.eval (for benevis vasash)
            //String fevalOut=ml.feval("frecca",(Object) simMatrix,6);

            ml.eval("[mems, pj, cij] =frecca(similarities,n_clusters);");
            //ml.eval("rmpath('D:\\Thesis\\IntelliNote\\src\\main\\resources\\')");

            double[][] cij = ml.getVariable("cij");
            //1*n
            try (
                    OutputStreamWriter writer = new OutputStreamWriter(
                            new FileOutputStream("cij.txt"), "UTF-8")
            ) {
                writer.write(Arrays.deepToString(cij));
            } catch (IOException ex) {
                fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            }

            double[][] mems = ml.getVariable("mems");
            try (
                    OutputStreamWriter writer = new OutputStreamWriter(
                            new FileOutputStream("mems.txt"), "UTF-8")
            ) {
                writer.write(Arrays.deepToString(mems));
            } catch (IOException ex) {
                fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            }
            double[] pj = ml.getVariable("pj");
            try (
                    OutputStreamWriter writer = new OutputStreamWriter(
                            new FileOutputStream("pj.txt"), "UTF-8")
            ) {
                writer.write(Arrays.toString(pj));
            } catch (IOException ex) {
                fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            }
            ml.disconnectAsync();
        }
    }
}

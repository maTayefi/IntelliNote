package utilities;

import model.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Logger;

public class Utility {
    public Vector<Vector<Boolean>> d_matrix;
    //0:Local - Reuters
    //1:Evernote Account - Reuters
    //2:Evernote Account - Sample Dataset
    public static int input = 0;

    public static int no_of_levels = 4;
    public static int no_of_clusters = 6;

    public static boolean[] levelsInClustering = new boolean[4];

    public static int current_stage = 0;
    public static int current_subStage = 0;
    //int current_subStage=0;

    public static final int getting_data = 0;
    public static final int process_data = 1;
    //////
    public static final int createVectorOfVectors = 0;
    public static final int createSimilarityMatrix = 1;
    public static final int runClustering = 2;
    //////
    public static final int showing_result = 2;
    public static final int getting_user_changes = 3;
    public static final int apply_changes = 4;


    public static ArrayList<Document> documents = new ArrayList<>();


    public static final Logger fLogger = Logger.getLogger("My Logger");

    //0:matlabcontrol
    //1:matlab engine
    //2: vnc, server ,...
    public static int matlabConnectionMethod = 1;
    public static int currentSize;

    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static Double[][] readMems() {
        Double[][] resultMatrix = new Double[459][6];
        try {
            Scanner s = new Scanner(new File("mems.txt"));
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
        return resultMatrix;
    }
}

package utilities;

import model.Document;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Utility {
    public int[][] d_matrix;
    //0:Local - Reuters
    //1:Evernote Account - Reuters
    //2:Evernote Account - Sample Dataset
    public static int input = 0;

    public static int no_of_levels = 4;

    public static boolean[] levelsInClustering = new boolean[4];

    public static int current_stage = 1;
    public static int current_subStage = 2;
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
}

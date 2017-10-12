package data_process;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import model.Sentence;
import model.Token;
import utilities.Utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.logging.Level;

import static utilities.Utility.cosineSimilarity;
import static utilities.Utility.fLogger;

public class PrepareClustering {

    public static ILexicalDatabase db = new NictWordNet();
    public static RelatednessCalculator rc = new JiangConrath(db);
    public static Vector<Vector<Double>> vector = new Vector<>();

    public static void createVectorOfVectors() {
        vector.setSize(Utility.documents.size());
        Set<String> distinctNonStpWrdsInTwoSntncs = new HashSet<>();
        System.out.println("docs size" + Utility.documents.size());
        for (int i = 0; i < Utility.documents.size(); i++) {
            //d-matrix ro inja besaaz
            Vector<Double> vIn = new Vector<>();
            vIn.setSize(Utility.documents.size());
            vector.add(i, vIn);
            for (int j = 0; j < Utility.documents.size(); j++) {
                //System.out.println(vector.toString());
                if (i != j) {
                    for (Sentence sentence : Utility.documents.get(i).getSentences()) {
                        for (Token token : sentence.getTokens()) distinctNonStpWrdsInTwoSntncs.add(token.getWord());
                    }
                    for (Sentence sentence : Utility.documents.get(j).getSentences()) {
                        for (Token token : sentence.getTokens()) distinctNonStpWrdsInTwoSntncs.add(token.getWord());
                    }
                    //n=distinctNonStpWrdsInTwoSntncs.size();
                    List<String> distinctNonStpWrdsInTwoSntncsLST = new ArrayList<>(distinctNonStpWrdsInTwoSntncs);
                    boolean appears = false;
                    OUTERMOST:
                    for (Sentence sentence : Utility.documents.get(i).getSentences()) {
                        for (Token token : sentence.getTokens()) {
                            if (distinctNonStpWrdsInTwoSntncsLST.get(j).equals(token.getWord())) {
                                vector.get(i).add(j, (double) 1);
                                appears = true;
                                break OUTERMOST;
                            }
                        }
                    }
                    if (!appears) {
                        double maxScore = 0;
                        for (Sentence sentence : Utility.documents.get(i).getSentences()) {
                            for (Token token : sentence.getTokens()) {
                                char tag = token.getPOSTag().toLowerCase().charAt(0);
                                if (token.getPOSTag().charAt(0) == 'J') tag = 'a';
                                else if (token.getPOSTag().equals("WRB")) tag = 'r';
                                else if (tag != 'n' && tag != 'v' && tag != 'a' && tag != 'r') tag = 'n';
                                double score = rc.calcRelatednessOfWords(token.getWord() + "#" + tag, distinctNonStpWrdsInTwoSntncsLST.get(j) + "#n");
                                //if(score==0.0)System.out.println("zero "+token.getWord()+" "+token.getPOSTag()+" "+distinctNonStpWrdsInTwoSntncsLST.get(j)+" "+tag);
                                if (score > maxScore) maxScore = score;
                            }
                        }
                        vector.get(i).add(j, maxScore);
                    }
                }
                //baraye har vector ba khodesh
                else vector.get(i).add(j, 1.0);
            }
        }
/*        for(int i=0;i<subTexts.size()-1;i++){
            System.out.println("begin "+i);
            System.out.println(vector.get(i).size()+" "+vector.get(i).toString());
        }*/
        try (
                OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream("vector.txt"), "UTF-8")
                //OutputStream buffer = new BufferedOutputStream(writer);
                //ObjectOutputStream output = new ObjectOutputStream(writer);
        ) {
            writer.write(String.valueOf(vector));
        } catch (IOException ex) {
            fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
        }
    }

    public static void createSimilarityMatrix() {
        //alan bayad cosine similarity to be dast biyarim o berizim too ye matrix jadid o bedimesh be Fuzzy Algorithm
        Double[][] similarities = new Double[Utility.currentSize - 1][Utility.currentSize - 1];
        for (int i = 0; i < Utility.currentSize - 1; i++) {
            for (int j = 0; j < Utility.currentSize - 1; j++) {
                double[] s1 = new double[Utility.currentSize];
                double[] s2 = new double[Utility.currentSize];
                for (int k = 0; k < Utility.currentSize - 1; k++) {
                    if (vector.get(i).get(k) != null) {
                        s1[k] = vector.get(i).get(k);
                    } else {
                        System.out.println("null i " + i + " " + k);
                        s1[k] = 0.0;
                    }
                    if (vector.get(j).get(k) != null) {
                        s2[k] = vector.get(j).get(k);
                    } else {
                        System.out.println("null j " + j + " " + k);
                        s2[k] = 0.0;
                    }

                }
                //System.out.println(Arrays.toString(s1));
                //System.out.println(Arrays.toString(s2));
                similarities[i][j] = cosineSimilarity(s1, s2);
            }
        }
/*        for(int i=0;i<currentSize-1;i++){
            for(int j=0;j<currentSize-1;j++){
                System.out.println("Sim "+i+" "+j+" "+similarities[i][j]);
            }
        }*/
        try (
                OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream("SimMatrix.txt"), "UTF-8")
                //OutputStream buffer = new BufferedOutputStream(writer);
                //ObjectOutputStream output = new ObjectOutputStream(writer);
        ) {
            writer.write(Arrays.deepToString(similarities));
        } catch (IOException ex) {
            fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
        }
    }
}

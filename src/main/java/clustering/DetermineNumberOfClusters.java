package clustering;

import connectors.MatlabConnector;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import model.Document;
import model.Sentence;
import model.Token;
import utilities.Utility;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DetermineNumberOfClusters {
    //ba'dan ontology sh kon
    //khooneha faghat 0 yaa 1 bashan, vase kaare ma kaafiye
    private Set<String> allTermsInDocuments = new HashSet<>();
    private Set<String> currentDocumentTerms = new HashSet<>();
    private int t = 0;

    public int calcNumOfClusters() throws InterruptedException, ExecutionException, MatlabConnectionException, MatlabInvocationException {
        Utility.no_of_clusters = dMatrixMethod();
        MatlabConnector.runClustering();
        Double[][] mems = Utility.readMems();
        return 5;
    }
    public int dMatrixMethod() {
        for (Document doc : Utility.documents) {
            for (Sentence sentence : doc.getSentences()) {
                for (Token token : sentence.getTokens()) {
                    allTermsInDocuments.add(token.getWord());
                    currentDocumentTerms.add(token.getWord());
                }
            }
            t = t + currentDocumentTerms.size();
            currentDocumentTerms = new HashSet<>();
        }
        int m = Utility.documents.size();
        int n = allTermsInDocuments.size();
        System.out.println("m:n:t " + m + " " + n + " " + t);
        return (m * n) / t;
    }
}

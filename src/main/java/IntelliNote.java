import Model.Document;
//import org.apache.jena.sparql.algebra.Op;
import Model.Sentence;
import Model.Token;
import com.mathworks.engine.MatlabEngine;
import com.uttesh.exude.ExudeData;
import com.uttesh.exude.exception.InvalidDataException;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import matlabcontrol.*;
import measures.ClusterEvaluator;
import measures.ContingencyTable;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Version;
import reuters21578.ExtractReuters;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by maTayefi on 3/5/2017.
 */
public class IntelliNote{

    private static RelatednessCalculator rc;

    private static final Logger fLogger = Logger.getLogger("My Logger");

    public static void main(String[] args) throws IOException, InvalidDataException, MatlabInvocationException, MatlabConnectionException {
        Double[][] resultMatrix=new Double[459][5];
        try {
            Scanner s = new Scanner(new File("answerForRueterswithN5.txt"));
            int column=0;
            int row=0;
            while (s.hasNextDouble()) {
                //System.out.println(str+"-"+column+"-"+row);
                resultMatrix[row][column] = s.nextDouble();
                column++;
                if(column==5){
                    column=0;
                    row++;
                }
            }
            //System.out.println(Arrays.deepToString(simMatrix));
            s.close();
            // At this point all dead cells are 0 and all live are 1
        } catch (IOException i) {
            System.out.println("Problems..");

        }
        ClusterEvaluator clusterEvaluator=new ClusterEvaluator();
        ContingencyTable contingencyTable=new ContingencyTable(459,5);
        contingencyTable.setData(resultMatrix);
        clusterEvaluator.setData(contingencyTable);
        System.out.println(clusterEvaluator.getPurity());

        System.exit(2);


        //read simmatrix
        double simMatrix[][] = new double[459][459];
        try {
            Scanner s = new Scanner(new File("SimMatrix.txt"));
            int row=0;
            int column=0;
            while (s.hasNext()) {
                String str=s.next();
                //if(str.contains("[")){
                   // column++;
                    //row=0;
                //}
                str = str.replaceAll("[,\\[\\]]","");
                //System.out.println(str+"-"+column+"-"+row);
                simMatrix[row][column] = Double.valueOf(str);
                column++;
                if(column==459){
                    column=0;
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

        String matrixForMatlab="";
        for(int i=0;i<459;i++){
            for(int j=0;j<459;j++){
                if(j==0)matrixForMatlab=matrixForMatlab.concat(" ");
                matrixForMatlab=matrixForMatlab.concat(String.valueOf(simMatrix[i][j]));
                if(j!=458)matrixForMatlab=matrixForMatlab.concat(" ");
                else matrixForMatlab=matrixForMatlab.concat(";");
            }
        }


        proxy.eval("addpath('D:\\Thesis\\IntelliNote\\')");
        proxy.eval("n_clusters=5;");
        proxy.eval("similarities=["+ matrixForMatlab +"];");
        //proxy.feval("frecca(similarities,n_clusters)");
        proxy.eval("frecca(similarities,n_clusters)");
        proxy.eval("rmpath('D:\\Thesis\\IntelliNote\\')");
        //proxy.setVariable("similarities",simMatrix);
        //proxy.setVariable("n_clusters",5);
        //proxy.feval("D:\\Thesis\\IntelliNote\\frecca.m");

        System.out.println("ans "+ proxy.getVariable("ans").toString());
        double[][] mems=(double[][])proxy.getVariable("mems");
        double[][] pj=(double[][])proxy.getVariable("pj");
        double[] cij=(double[])proxy.getVariable("cij");
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
        System.out.println("mems "+ Arrays.deepToString(mems));
        System.out.println("pj "+ Arrays.deepToString(pj));
        System.out.println("cij "+ Arrays.toString(cij));
        // Disconnect from the MATLAB session
        //ml.disconnect();

        System.exit(1);
        File reutersDir = new File("D:\\Thesis\\IntelliNote\\src\\main\\resources\\reuters-21578\\data");

		/*
         * // First, extract to a tmp directory and only if everything succeeds,
		 * // rename // to output directory. File outputDir = new File(args[1]);
		 * outputDir = new File(outputDir.getAbsolutePath() + "-tmp");
		 * outputDir.mkdirs();
		 */
        ExtractReuters extractor = new ExtractReuters(reutersDir);
        extractor.extract();
        // Now rename to requested output dir
        // outputDir.renameTo(new File(args[1]));
        ArrayList<String> texts = extractor.getBodies();
        //System.out.println(texts.toString());
        OpenNLPUtil openNLPUtil = new OpenNLPUtil();


        ILexicalDatabase db = new NictWordNet();
        rc = new JiangConrath(db);

        POSModel model = new POSModelLoader()
                .load(new File("D:\\Thesis\\IntelliNote\\src\\main\\resources\\opennlp\\en-pos-maxent.bin"));
        POSTaggerME tagger = new POSTaggerME(model);


        InputStream inputStream = new FileInputStream("D:\\Thesis\\IntelliNote\\src\\main\\resources\\opennlp\\en-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(inputStream);
        TokenizerME tokenizer = new TokenizerME(tokenModel);

        ArrayList<Document> documents=new ArrayList<>();
        int currentSize=texts.size();
        ArrayList<String> subTexts= new ArrayList<>(texts.subList(0,currentSize-1));
        for (String doc : subTexts) {
            Document document = new Document();
            document.setSimpleText(doc);
            ArrayList<Sentence> sentences=new ArrayList<>();
            int sNo=0;
            for(String sentenceT:openNLPUtil.SentenceDetect(doc)){
                ArrayList<Token> tokens=new ArrayList<>();
                String tokensOriginal[] = tokenizer.tokenize(sentenceT);
                String[] tags = tagger.tag(tokensOriginal);
                //System.out.println("tags: "+ Arrays.toString(tags));
                POSSample sample = new POSSample(tokensOriginal, tags);
                //System.out.println("POS Tags:"+sample.toString());

                String withoutStopWordsSntnc = ExudeData.getInstance().filterStoppingsKeepDuplicates(sentenceT);
                //System.out.println("||||||||||||||||||||||||||||||| After StopWord Removal:" + "\n" + output);

                String tokensClean[] = tokenizer.tokenize(withoutStopWordsSntnc);

                int indexInOriginalTokens=0;
                int indexInCleanTokens=0;
                //&lt age lt too clean ha bood igonresh kone va bere soraghe ba'di
                //System.out.println("tokensOriginal"+Arrays.toString(tokensOriginal));
                //System.out.println("tokensClean"+Arrays.toString(tokensClean));
                for(String tokenS:tokensClean){
                    String toCheck=tokensOriginal[indexInOriginalTokens];
                    if(tokenS.equals(toCheck.toLowerCase())){
                        Token token=new Token(tokenS,sNo,indexInCleanTokens,tags[indexInOriginalTokens]);

                        indexInOriginalTokens++;


                        tokens.add(token);
                    }
                    else{
                        boolean exists=false;
                        for(int i=indexInOriginalTokens;i<tokensOriginal.length;i++){
                            if(tokensOriginal[i].toLowerCase().equals(tokenS)){
                                exists=true;
                                break;
                            }
                        }
                        if(exists){
                            while((!tokenS.equals(toCheck.toLowerCase()))){
                                indexInOriginalTokens++;
                                toCheck=tokensOriginal[indexInOriginalTokens];
                            }
                            Token token=new Token(tokenS,sNo,indexInCleanTokens,tags[indexInOriginalTokens]);
                            tokens.add(token);
                        }
                        else if(tokenS.equals("lt")){
                            //System.out.println("&lt");
                            indexInOriginalTokens++;
                        }
                        else System.out.println("not handled tokenS "+tokenS);
                    }
                    indexInCleanTokens++;
                }
                Sentence sentenceM=new Sentence(tokens,sNo);
                sentences.add(sentenceM);
                sNo++;
            }
            document.setSentences(sentences);
            documents.add(document);
        }

        //int n;
        Vector<Vector<Double>> vector=new Vector<>();
        vector.setSize(documents.size());
        Set<String> distinctNonStpWrdsInTwoSntncs=new HashSet<>();
        System.out.println("docs size"+documents.size());
        for(int i=0;i<documents.size();i++){
            Vector<Double> vIn=new Vector<>();
            vIn.setSize(documents.size());
            vector.add(i,vIn);
            for(int j=0;j<documents.size();j++){
                //System.out.println(vector.toString());
                if(i!=j){
                    for(Sentence sentence:documents.get(i).getSentences()){
                        for(Token token:sentence.getTokens())distinctNonStpWrdsInTwoSntncs.add(token.getWord());
                    }
                    for(Sentence sentence:documents.get(j).getSentences()){
                        for(Token token:sentence.getTokens())distinctNonStpWrdsInTwoSntncs.add(token.getWord());
                    }
                    //n=distinctNonStpWrdsInTwoSntncs.size();
                    List<String> distinctNonStpWrdsInTwoSntncsLST=new ArrayList<>(distinctNonStpWrdsInTwoSntncs);
                    boolean appears=false;
                    OUTERMOST:for(Sentence sentence:documents.get(i).getSentences()){
                        for(Token token:sentence.getTokens()){
                            if(distinctNonStpWrdsInTwoSntncsLST.get(j).equals(token.getWord())){
                                vector.get(i).add(j, (double) 1);
                                appears=true;
                                break OUTERMOST;
                            }
                        }
                    }
                    if(!appears){
                        double maxScore=0;
                        for(Sentence sentence:documents.get(i).getSentences()){
                            for(Token token:sentence.getTokens()){
                                char tag=token.getPOSTag().toLowerCase().charAt(0);
                                if(token.getPOSTag().charAt(0)=='J')tag='a';
                                else if(token.getPOSTag().equals("WRB"))tag='r';
                                else if(tag!='n' && tag!='v' && tag!='a' && tag!='r') tag='n';
                                double score=rc.calcRelatednessOfWords(token.getWord()+"#"+tag, distinctNonStpWrdsInTwoSntncsLST.get(j)+"#n");
                                //if(score==0.0)System.out.println("zero "+token.getWord()+" "+token.getPOSTag()+" "+distinctNonStpWrdsInTwoSntncsLST.get(j)+" "+tag);
                                if(score>maxScore)maxScore=score;
                            }
                        }
                        vector.get(i).add(j, maxScore);
                    }
                }
                //baraye har vector ba khodesh
                else vector.get(i).add(j,1.0);
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
        ){
            writer.write(String.valueOf(vector));
        }
        catch(IOException ex){
            fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
        }
        //alan bayad cosine similarity to be dast biyarim o berizim too ye matrix jadid o bedimesh be Fuzzy Algorithm
        Double[][] similarities=new Double[currentSize-1][currentSize-1];
        for(int i=0;i<currentSize-1;i++){
            for(int j=0;j<currentSize-1;j++){
                double[] s1=new double[currentSize];
                double[] s2=new double[currentSize];
                for(int k = 0; k < currentSize-1; k++) {
                    if(vector.get(i).get(k)!=null) {
                        s1[k] = vector.get(i).get(k);
                    } else {
                        System.out.println("null i "+i+" "+k);
                        s1[k] = 0.0;
                    }
                    if(vector.get(j).get(k)!=null) {
                        s2[k] = vector.get(j).get(k);
                    } else {
                        System.out.println("null j "+j+" "+k);
                        s2[k] = 0.0;
                    }

                }
                //System.out.println(Arrays.toString(s1));
                //System.out.println(Arrays.toString(s2));
                similarities[i][j]=cosineSimilarity(s1,s2);
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
        ){
            writer.write(Arrays.deepToString(similarities));
        }
        catch(IOException ex){
            fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
        }
 /*       final String test = "This is a test. How about that?! Huh?";
        StringReader reader = new StringReader(test);
        StandardTokenizer tokenizer = new StandardTokenizer(Version.LUCENE_36, reader);

        CharTermAttribute charTermAttrib = tokenizer.getAttribute(CharTermAttribute.class);
        TypeAttribute typeAtt = tokenizer.getAttribute(TypeAttribute.class);
        OffsetAttribute offset = tokenizer.getAttribute(OffsetAttribute.class);

        List<String> tokens = new ArrayList<String>();
        tokenizer.reset();
        while (tokenizer.incrementToken()) {
            tokens.add(charTermAttrib.toString());
            System.out.println(typeAtt.toString() + " " + offset.toString() + ": " + charTermAttrib.toString());
        }
        tokenizer.end();
        tokenizer.close();*/

    }
    private static double cosineSimilarity(double[] vectorA, double[] vectorB) {
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

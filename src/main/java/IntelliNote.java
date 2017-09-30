import Model.Document;
//import org.apache.jena.sparql.algebra.Op;
import Model.Sentence;
import Model.Token;
import com.uttesh.exude.ExudeData;
import com.uttesh.exude.exception.InvalidDataException;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.JiangConrath;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Created by maTayefi on 3/5/2017.
 */
public class IntelliNote {

    private static RelatednessCalculator rc;

    public static void main(String[] args) throws IOException, InvalidDataException {
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

        for (String doc : texts) {
            Document document = new Document();
            document.setSimpleText(doc);
            ArrayList<Sentence> sentences=new ArrayList<>();
            int sNo=0;
            for(String sentenceT:openNLPUtil.SentenceDetect(doc)){
                ArrayList<Token> tokens=new ArrayList<>();
                String tokensOriginal[] = tokenizer.tokenize(sentenceT);
                String[] tags = tagger.tag(tokensOriginal);
                System.out.println("tags: "+ Arrays.toString(tags));
                POSSample sample = new POSSample(tokensOriginal, tags);
                System.out.println("POS Tags:"+sample.toString());
                //save tags

                String withoutStopWordsSntnc = ExudeData.getInstance().filterStoppingsKeepDuplicates(sentenceT);
                //System.out.println("||||||||||||||||||||||||||||||| After StopWord Removal:" + "\n" + output);

                String tokensClean[] = tokenizer.tokenize(withoutStopWordsSntnc);

                int indexInOriginalTokens=0;
                int indexInCleanTokens=0;
                System.out.println("tokensOriginal"+Arrays.toString(tokensOriginal));
                System.out.println("tokensClean"+Arrays.toString(tokensClean));
                for(String tokenS:tokensClean){
                    String toCheck=tokensOriginal[indexInOriginalTokens];
                    if(tokenS.equals(toCheck.toLowerCase())){
                        Token token=new Token(tokenS,sNo,indexInCleanTokens,tags[indexInOriginalTokens]);
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
                        else System.out.println("not handled tokenS "+tokenS);
                    }
                    indexInCleanTokens++;
                }
                Sentence sentenceM=new Sentence(tokens,sNo);
                sentences.add(sentenceM);
                sNo++;
            }
            document.setSentences(sentences);
        }
        //double loop for matrix and inside that cosine of similarities

        //rc.calcRelatednessOfWords(nv1+"#n", nv2+"#n");

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

}

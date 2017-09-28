import Model.Document;
//import org.apache.jena.sparql.algebra.Op;
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
import opennlp.tools.tokenize.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Version;
import reuters21578.ExtractReuters;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


        for (String doc : texts) {
            Document document = new Document();
            document.setSimpleText(doc);
            document.setSentences(openNLPUtil.SentenceDetect(doc));
            System.out.println("++++++++++++++++++++++++++++++++++DOC");
            System.out.println(doc);
            System.out.println("----------------------------------SENTENCES");
            System.out.println(Arrays.toString(document.getSentences()));
            System.out.println("***********************END");

            //hazfe stop word ha
            String output = ExudeData.getInstance().filterStoppingsKeepDuplicates(doc);
            System.out.println("||||||||||||||||||||||||||||||| After StopWord Removal:" + "\n" + output);
            document.setAfterStopWordRemoval(output);
            //POS Tagging ham mikhaym
            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                    .tokenize(output);
            String[] tags = tagger.tag(whitespaceTokenizerLine);

            POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
            System.out.println("POS Tags:"+sample.toString());
            //save tags
            //rc.calcRelatednessOfWords(nv1+"#n", nv2+"#n");
        }
        //double loop for matrix and inside that cosine of similarities


        final String test = "This is a test. How about that?! Huh?";
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
        tokenizer.close();

    }

}

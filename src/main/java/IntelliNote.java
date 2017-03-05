import Model.Document;
import org.apache.jena.sparql.algebra.Op;
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
    public static void main(String[] args) throws IOException {
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
        ArrayList<String> texts=extractor.getBodies();
        System.out.println(texts.toString());
        OpenNLPUtil openNLPUtil=new OpenNLPUtil();

        for(String doc:texts){
            Document document=new Document();
            document.setSimpleText(doc);
            document.setSentences(openNLPUtil.SentenceDetect(doc));
            System.out.println("++++++++++++++++++++++++++++++++++");
            System.out.println(doc);
            System.out.println("---------------");
            System.out.println(Arrays.toString(document.getSentences()));
            System.out.println("++++++++++++++++++++++++++++++++++");
        }



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
            System.out.println(typeAtt.toString() + " " + offset.toString() +      ": " + charTermAttrib.toString());
        }
        tokenizer.end();
        tokenizer.close();




    }

}

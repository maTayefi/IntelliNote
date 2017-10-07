package utilities;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by maTayefi on 3/5/2017.
 */
public class OpenNLPUtil {
    public OpenNLPUtil(){

    }

    public String[] SentenceDetect(String text) throws
            IOException {

        // always start with a model, a model is learned from training data
        InputStream is = new FileInputStream("src/main/resources/opennlp/en-sent.bin");
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sdetector = new SentenceDetectorME(model);

        String sentences[] = sdetector.sentDetect(text);

        is.close();
        return sentences;
    }
}

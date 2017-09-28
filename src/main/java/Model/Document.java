package Model;

/**
 * Created by maTayefi on 3/5/2017.
 */
public class Document {
    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }

    public String[] getSentences() {
        return sentences;
    }

    public void setSentences(String[] sentences) {
        this.sentences = sentences;
    }

    String simpleText;
    String afterStopWordRemoval;

    public String getAfterStopWordRemoval() {
        return afterStopWordRemoval;
    }

    public void setAfterStopWordRemoval(String afterStopWordRemoval) {
        this.afterStopWordRemoval = afterStopWordRemoval;
    }

    String[] sentences;
    public Document(){
        simpleText="";
        sentences=null;
    }
 }

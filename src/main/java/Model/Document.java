package Model;

import java.util.ArrayList;

/**
 * Created by maTayefi on 3/5/2017.
 */
public class Document {
    private String simpleText;
    private ArrayList sentences = null;

    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }


    public Document() {
        simpleText = "";
        sentences = null;
    }


    public Document(ArrayList sentences) {
        this.sentences = sentences;
    }


    public ArrayList<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(ArrayList<Sentence> sentences) {
        this.sentences = sentences;
    }
}

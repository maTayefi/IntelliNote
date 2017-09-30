package Model;

import edu.mit.jwi.item.IWord;


public class Token {
    //vase dbPedia ham ezaf kon
    //private ArrayList<IWord> synSet;
    private IWord iword;
    private String dbPediaResourceURI;
    private int sNo = 0;//Number of Sentence in Context
    private int tNo = 0;//Number of token location in sentence
    private String word;
    private String POSTag;


    public Token(String word, int sNo, int tNo,String POSTag) {
        this.word = word;
        this.sNo = sNo;
        this.tNo = tNo;
        this.POSTag=POSTag;
    }

    public String getDbPediaResourceURI() {
        return dbPediaResourceURI;
    }

    public void setDbPediaResourceURI(String dbPediaResourceURI) {
        this.dbPediaResourceURI = dbPediaResourceURI;
    }


    public String toString() {
        return word;
    }

    public IWord getIword() {
        return iword;
    }

    public void setIword(IWord iword) {
        this.iword = iword;
    }

    public int getsNo() {
        return sNo;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    public int gettNo() {
        return tNo;
    }

    public void settNo(int tNo) {
        this.tNo = tNo;
    }

    public String getPOSTag() {
        return POSTag;
    }

    public void setPOSTag(String POSTag) {
        this.POSTag = POSTag;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }


}

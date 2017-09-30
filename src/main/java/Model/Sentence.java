package Model;

import java.util.ArrayList;


public class Sentence {

    private ArrayList<Token> tokens = null;
    private int sNo;

    public int getsNo() {
        return sNo;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }


    public void setTokens(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public Sentence(ArrayList<Token> tokens, int sNo) {
        this.tokens = tokens;
        this.sNo = sNo;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

}

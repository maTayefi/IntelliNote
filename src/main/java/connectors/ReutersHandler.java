package connectors;

import com.uttesh.exude.ExudeData;
import com.uttesh.exude.exception.InvalidDataException;
import connectors.reuters21578.ExtractReuters;
import model.Document;
import model.Sentence;
import model.Token;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import utilities.OpenNLPUtil;
import utilities.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ReutersHandler {
    public static void createModel() throws IOException, InvalidDataException {
        File reutersDir = new File("D:\\Thesis\\IntelliNote\\src\\main\\resources\\reuters-21578\\data");
        ExtractReuters extractor = new ExtractReuters(reutersDir);
        extractor.extract();
        ArrayList<String> texts = extractor.getBodies();
        OpenNLPUtil openNLPUtil = new OpenNLPUtil();


        POSModel model = new POSModelLoader()
                .load(new File("D:\\Thesis\\IntelliNote\\src\\main\\resources\\opennlp\\en-pos-maxent.bin"));
        POSTaggerME tagger = new POSTaggerME(model);


        InputStream inputStream = new FileInputStream("D:\\Thesis\\IntelliNote\\src\\main\\resources\\opennlp\\en-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(inputStream);
        TokenizerME tokenizer = new TokenizerME(tokenModel);

        Utility.currentSize = texts.size();
        ArrayList<String> subTexts = new ArrayList<>(texts.subList(0, Utility.currentSize - 1));
        for (String doc : subTexts) {
            Document document = new Document();
            document.setSimpleText(doc);
            ArrayList<Sentence> sentences = new ArrayList<>();
            int sNo = 0;
            for (String sentenceT : openNLPUtil.SentenceDetect(doc)) {
                ArrayList<Token> tokens = new ArrayList<>();
                String tokensOriginal[] = tokenizer.tokenize(sentenceT);
                String[] tags = tagger.tag(tokensOriginal);
                //System.out.println("tags: "+ Arrays.toString(tags));
                //POSSample sample = new POSSample(tokensOriginal, tags);
                //System.out.println("POS Tags:"+sample.toString());

                String withoutStopWordsSntnc = ExudeData.getInstance().filterStoppingsKeepDuplicates(sentenceT);
                //System.out.println("||||||||||||||||||||||||||||||| After StopWord Removal:" + "\n" + output);

                String tokensClean[] = tokenizer.tokenize(withoutStopWordsSntnc);

                int indexInOriginalTokens = 0;
                int indexInCleanTokens = 0;
                //&lt age lt too clean ha bood igonresh kone va bere soraghe ba'di
                //System.out.println("tokensOriginal"+Arrays.toString(tokensOriginal));
                //System.out.println("tokensClean"+Arrays.toString(tokensClean));
                for (String tokenS : tokensClean) {
                    String toCheck = tokensOriginal[indexInOriginalTokens];
                    if (tokenS.equals(toCheck.toLowerCase())) {
                        Token token = new Token(tokenS, sNo, indexInCleanTokens, tags[indexInOriginalTokens]);

                        indexInOriginalTokens++;


                        tokens.add(token);
                    } else {
                        boolean exists = false;
                        for (int i = indexInOriginalTokens; i < tokensOriginal.length; i++) {
                            if (tokensOriginal[i].toLowerCase().equals(tokenS)) {
                                exists = true;
                                break;
                            }
                        }
                        if (exists) {
                            while ((!tokenS.equals(toCheck.toLowerCase()))) {
                                indexInOriginalTokens++;
                                toCheck = tokensOriginal[indexInOriginalTokens];
                            }
                            Token token = new Token(tokenS, sNo, indexInCleanTokens, tags[indexInOriginalTokens]);
                            tokens.add(token);
                        } else if (tokenS.equals("lt")) {
                            //System.out.println("&lt");
                            indexInOriginalTokens++;
                        } else System.out.println("not handled tokenS " + tokenS);
                    }
                    indexInCleanTokens++;
                }
                Sentence sentenceM = new Sentence(tokens, sNo);
                sentences.add(sentenceM);
                sNo++;
            }
            document.setSentences(sentences);
            Utility.documents.add(document);
        }
    }
}

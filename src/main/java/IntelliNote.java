import reuters21578.ExtractReuters;

import java.io.File;
import java.util.Collection;

/**
 * Created by maTayefi on 3/5/2017.
 */
public class IntelliNote {
    public static void main(String[] args){
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
        Collection<String> texts=extractor.getBodies();
        System.out.println(texts.toString());
    }
}

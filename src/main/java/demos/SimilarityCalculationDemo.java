package demos;

import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.*;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class SimilarityCalculationDemo {
	
	private static NictWordNet db = new NictWordNet();
	private static RelatednessCalculator[] rcs = {
			new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db), 
			new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
			};
	
	private static void run( String word1, String word2 ) {
		WS4JConfiguration.getInstance().setMFS(true);
		for ( RelatednessCalculator rc : rcs ) {
			double s = rc.calcRelatednessOfWords(word1, word2);
			System.out.println( rc.getClass().getName()+"\t"+s );
		}
	}
	public static void main(String[] args) {
		//long t0 = System.currentTimeMillis();
		System.out.println("act ---- moderate");
		run( "act","moderate" );
		//long t1 = System.currentTimeMillis();
		//System.out.println( "Done in "+(t1-t0)+" msec." );
		System.out.println("good ---- nice");
		run( "good","nice" );
		System.out.println("good ---- bad");
		run( "good","bad" );
		System.out.println("table ---- God");
		run( "table","God" );
		System.out.println("house ---- room");
		run( "house","room" );

	}
}
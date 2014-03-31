package com.util;

import java.util.HashSet;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class Constants {
	public static LexicalizedParser lp;
	
	public static HashSet<String> puncs;
	
	public static void init() {
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	    lp.setOptionFlags(new String[]{"-maxLength", "200", "-retainTmpSubcategories"});
	    
	    puncs = new HashSet<String>();
	    
	    puncs.add(".");
	    puncs.add(",");
	    puncs.add(";");
	}
}

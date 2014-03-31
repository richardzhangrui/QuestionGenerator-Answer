package com.util;

import java.util.List;

import com.common.Sent;

import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class Util {
	
	public static List<TypedDependency> get_tdls(Tree parse) {
		
	    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
	    
	    return tdl;
	}
	
	public static TypedDependency get_root(Sent sent) {
		TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
	    tp.printTree(sent.parse);
	    
	    TypedDependency root = null;

	    for (int i = 0; i < sent.tdl.size(); i++) {
	    	if (sent.tdl.get(i).reln().toString().equals("root")) {
	    		root = sent.tdl.get(i);
	    		break;
	    	}
	    }
	    
	    return root;
	}
}

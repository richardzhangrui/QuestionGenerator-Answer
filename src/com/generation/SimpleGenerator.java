package com.generation;

import java.util.HashSet;

import com.common.Sent;
import com.util.Constants;
import com.util.Reorderer;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.WordLemmaTag;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.trees.TypedDependency;

public class SimpleGenerator extends QuestionGenerator{
	
	@Override
	public String generateSent(Sent sent) {
	    
		Reorderer.reorder(sent);
		
	    HashSet<String> relations = new HashSet<String>();
	    /* dependencies include is/are, were/was, can/have, etc. */
	    relations.add("cop");
	    relations.add("auxpass");
	    relations.add("aux");
	    
	    int index = -1;
	    
	    for (int i = 0; i < sent.tdl.size(); i++) {
	    	TypedDependency tmp = sent.tdl.get(i);
	    	if (tmp.gov().equals(sent.root.dep())) {
	    		String type = tmp.reln().toString();		
	    		
	    		if (relations.contains(type)) {
	    			index = tmp.dep().index() - 1;
	    			if (type == "aux") {
	    				break;
	    			}
	    		} else if (type.startsWith("prep")) {
	    			String tag = sent.parse.taggedLabeledYield().get(tmp.gov().index()-1).tag();
	    			if (!tag.equals("VBZ") && !tag.equals("VBP") && !tag.equals("VBD") && !tag.equals("MD"))
	    				index = tmp.gov().index() - 1;
	    		}
	    	}
	    }
	    
	    StringBuffer sb = new StringBuffer();
	    
	    if (index == -1) {
	    	CoreLabel lable = sent.parse.taggedLabeledYield().get(sent.root_index);
	        /* get the tag */
	    	String tag = lable.tag();
	    
	    	WordTag wt = new WordTag(sent.sent.get(sent.root_index).word(),tag);
	    	
	        /* get the original word */
	    	WordLemmaTag ori_word = Morphology.lemmatizeStatic(wt);
	    	
	    	sent.sent.get(sent.root_index).setWord(ori_word.lemma());
	    	
	        /* handle simple declarative sentences: add helper verbs according to verb tense */
	    	if (tag.equals("VBD") || tag.equals("MD")) {
	    		sb.append("Did ");
	    	} else if (tag.equals("VBP")) {
	    		sb.append("Do ");
	    	} else if (tag.equals("VBZ")) {
	    		sb.append("Does ");
	    	}
	    } else if (index != -1) {
	    	String word = sent.sent.get(index).word();
	    	if (index < sent.tdl.size() && sent.tdl.get(index).reln().toString().equals("aux") && word.toLowerCase().equals("ca")) {
	    		word += "n";
	    	}
			sb.append(Character.toUpperCase(word.charAt(0)));
		    sb.append(word.substring(1) + " ");
	    }

	    for (int i = 0; i < sent.sent.size(); i++) {
	    	if (i == index || sent.reorder_indexes.contains(i)) {
	    		continue;
	    	}
	    	
	    	/* Need to check whether it is a Proper Noun */
	    	if (i == 0 && !sent.parse.taggedLabeledYield().get(i).tag().equals("NNP") && !sent.sent.get(i).word().equals("I")) {
	    		sb.append(Character.toLowerCase((sent.sent.get(i).word().charAt(0))));
	    		sb.append(sent.sent.get(i).word().substring(1) + " ");
	    		continue;
	    	}
	    	
	    	if (Constants.puncs.contains(sent.sent.get(i).word())) {
	    		continue;
	    	}
	    	
	    	if (i < sent.tdl.size() && sent.tdl.get(i).reln().toString().equals("neg")) {
	    		continue;
	    	}
	    	
	    	String word = sent.sent.get(i).word();
	    	
	    	sb.append(word+" ");
	    }    
	    
	    for (int i = 0; i < sent.reorder_range.size(); i += 2) {
	    	for (int j = sent.reorder_range.get(i); j <= sent.reorder_range.get(i + 1); j++) {
	    		if (!sent.parse.taggedLabeledYield().get(j).tag().equals("NNP") && !sent.sent.get(j).word().equals("I")) {
		    		sb.append(sent.sent.get(j).word().toLowerCase() + " ");
		    	} else {
		    		sb.append(sent.sent.get(j).word() + " ");
		    	}
	    	}
	    }
	    
	    sb.append("?");
	    
	    return sb.toString();
	}

}

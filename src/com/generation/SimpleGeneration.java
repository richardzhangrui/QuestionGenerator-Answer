package com.generation;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.process.PTBTokenizer;


/**  
 * TODO: Create classes (more OOP) 
 * 		 Transform to 'NO' questions
 * 		 Negation sentence
 **/
class SimpleGeneration {
  public static void main(String[] args) {
    LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    lp.setOptionFlags(new String[]{"-maxLength", "200", "-retainTmpSubcategories"});
    
    System.out.println("Please input the sentence: ");
    
    Scanner sc = new Scanner(System.in);
    
    String str = sc.nextLine();
    
    Reader in = new StringReader(str);
    
    PTBTokenizer<Word> tokenizer =  PTBTokenizer.newPTBTokenizer(in);
    
    List<Word> sent = tokenizer.tokenize();
        
    //String str = "Bills on ports and immigration were submitted by Senator Brownback , Republican of Kansas";
    //String str = "He sleeps on the bed.";
    //String[] sent = str.split(" ");
    //String[] sent = { "This", "is", "an", "easy", "sentence", "." };
    List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
    Tree parse = lp.apply(rawWords);
    //parse.pennPrint();
    //System.out.println();

    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
    //System.out.println(tdl);
    //System.out.println();

    //TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
    //tp.printTree(parse);
    
    TypedDependency root = null;
    int index = -1;
    int root_index = -1;
    
    for (int i = 0; i < tdl.size(); i++) {
    	if (tdl.get(i).reln().toString().equals("root")) {
    		root = tdl.get(i);
    		root_index = i;
    	}
    }
    
      
    HashSet<String> relations = new HashSet<String>();
    /* dependencies include is/are, were/was, can/have, etc. */
    relations.add("cop");
    relations.add("auxpass");
    relations.add("aux");
    
    for (int i = 0; i < tdl.size(); i++) {
    	TypedDependency tmp = tdl.get(i);
    	if (tmp.gov().equals(root.dep())) {
    		String type = tmp.reln().toString();
    		
    		if (relations.contains(type)) {
    			index = tmp.dep().index() - 1;
    		}
    	}
    }
    
    StringBuffer sb = new StringBuffer();
    
    if (index == -1) {
    	CoreLabel lable = parse.taggedLabeledYield().get(root_index);
        /* get the tag */
    	String tag = lable.tag();
    
    	WordTag wt = new WordTag(sent.get(root_index).word(),tag);
    	
        /* get the original word */
    	WordLemmaTag ori_word = Morphology.lemmatizeStatic(wt);
    	
    	sent.get(root_index).setWord(ori_word.lemma());
    	
        /* handle simple declarative sentences: add helper verbs according to verb tense */
    	if (tag.equals("VBD")) {
    		sb.append("Did ");
    	} else if (tag.equals("VBP")) {
    		sb.append("Do ");
    	} else if (tag.equals("VBZ")) {
    		sb.append("Does ");
    	}
    } else if (index != -1) {
    	String word = sent.get(index).word();
    	if (tdl.get(index).reln().toString().equals("aux") && word.toLowerCase().equals("ca")) {
    		word += "n";
    	}
		sb.append(Character.toUpperCase(word.charAt(0)));
	    sb.append(word.substring(1) + " ");
    }
    boolean isPeriod = false;
    for (int i = 0; i < sent.size(); i++) {
    	if (i == index) {
    		continue;
    	}
    	
    	/* Need to check whether it is a Proper Noun */
    	if (i == 0 && !parse.taggedLabeledYield().get(i).tag().equals("NNP") && !sent.get(i).word().equals("I")) {
    		sb.append(Character.toLowerCase((sent.get(i).word().charAt(0))));
    		sb.append(sent.get(i).word().substring(1) + " ");
    		continue;
    	}
    	
    	if (sent.get(i).word().equals(".")) {
    		sb.append("?");
    		isPeriod = true;
    		continue;
    	}
    	
    	if (tdl.get(i).reln().toString().equals("neg")) {
    		continue;
    	}
    	
    	String word = sent.get(i).word();
    	
    	sb.append(word+" ");
    }    
    
    if (!isPeriod) {
    	sb.append("?");
    }
    
    System.out.println(sb.toString());
    
  }
}

package com.generation;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

class Pair {
	public int start;
	public int end;
}

/**
 * TODO: 1. OOP
 * 		 2. Support questions other than in the first position.(with tags and denpendencies)
 * 	     3. Question generation factory
 * 	     4. More name entity (use other classifier)
 * @author richard
 *
 */

public class MiddleGeneration {
	
	
	public static void main(String[] args) throws IOException {
		
		LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	    lp.setOptionFlags(new String[]{"-maxLength", "200", "-retainTmpSubcategories"});
	    
	    System.out.println("Please input the sentence: ");
	    
	    Scanner sc = new Scanner(System.in);
	    
	    String str = sc.nextLine();
	    
	    Reader in = new StringReader(str);
	    
	    PTBTokenizer<Word> tokenizer =  PTBTokenizer.newPTBTokenizer(in);
	    
	    List<Word> sent = tokenizer.tokenize();
		
	    List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
	    Tree parse = lp.apply(rawWords);
	    
	    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
	    
	    //TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
	    //tp.printTree(parse);
	    	    
		String serializedClassifier = "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz";


		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

		/* For either a file to annotate or for the hardcoded text example,
		this demo file shows two ways to process the output, for teaching
	    purposes.  For the file, it shows both how to run NER on a String
	    and how to run it on a whole file.  For the hard-coded String,
	    it shows how to run it on a single sentence, and how to do this
	    and produce an inline XML output format.
		*/

        //String s2 = "Tom Cruise goes to school at Stanford University, which is located in California.";
                
		HashMap<String, ArrayList<Pair>> map = new HashMap<String, ArrayList<Pair>>();
		map.put("PERSON", new ArrayList<Pair>());
		map.put("ORGANIZATION", new ArrayList<Pair>());
		map.put("LOCATION", new ArrayList<Pair>());

        
	    for (List<CoreLabel> lcl : classifier.classify(str)) {
	    	
	    	for (int i = 0; i < lcl.size(); i++) {
	    		CoreLabel cl = lcl.get(i);
		        String ne = cl.get(CoreAnnotations.AnswerAnnotation.class); 
		        
		        String prev = "";
		        Pair pos = new Pair();
		        pos.start = 0;
		        pos.end = 0;
		        while (!ne.equals("0") && (prev.equals("") || ne.equals(prev))) {
		        	if (prev.equals("")) {
		        		pos.start = i;
		        	}
		        	//System.out.println(ne);
		        	i++;
		        	if (i >= lcl.size()) {
		        		break;
		        	}
		        	prev = ne;
		        	cl = lcl.get(i);
			        ne = cl.get(CoreAnnotations.AnswerAnnotation.class); 
		        }
		        
		       
		        if (!prev.equals("") && !prev.equals("0")) {
		        	i--;
		        	pos.end = i;
		        	//System.out.println(prev);
		        	if (map.get(prev) != null) {
		        		map.get(prev).add(pos);
		        	}
		        }
	    	}
	    }
	    
	    ArrayList<StringBuffer> sbs = new ArrayList<StringBuffer>();
	    
	    for (String key : map.keySet()) {
	    	for (Pair pos : map.get(key)) {
	    		if (pos.start == 0) {
	    			StringBuffer sb = new StringBuffer();
	    			if (key == "PERSON") {
	    				sb.append("Who ");	
	    			} else if (key == "ORGANIZATION") {
	    				sb.append("What ");
	    			} else if (key == "LOCATION") {
	    				sb.append("Where ");
	    			}
	    			
	    			for (int i = pos.end + 1; i < sent.size(); i++) {
    					String word = sent.get(i).word();
    					if (word.equals(".")) {
    						sb.append("?");
    						continue;
    					}
    					sb.append(word + " ");
    				}
    				if (sb.charAt(sb.length()-1) != '?') {
    					sb.append("?");
    				}
    				sbs.add(sb);
	    		} 
	    	}
	    }
	    
	    for (StringBuffer sb : sbs) {
	    	System.out.println(sb.toString());
	    }
	    
	}
	
}


package com.common;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.util.Constants;
import com.util.Util;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

public class Sent {
	public List<Word> sent;
	public Tree parse;
	public List<TypedDependency> tdl;
	
	public int root_index;
	public TypedDependency root;
	
	public HashMap<Integer, Tree> index_to_nodes;
	
	public ArrayList<Integer> reorder_range;
	public HashSet<Integer> reorder_indexes;
	
	public Sent() {
		index_to_nodes = new HashMap<Integer, Tree>();
		reorder_range = new ArrayList<Integer>();
		reorder_indexes = new HashSet<Integer>();
	}
	
	private void push_to_map(Tree node, List<Label> labels) {
		if (labels.contains(node.label())) {
			index_to_nodes.put(labels.indexOf(node.label()), node);
		}
		
		for (Tree n : node.children()) {
			push_to_map(n, labels);
		}
	}
	
	public void init(String input) {
		Reader in = new StringReader(input);
	    
	    PTBTokenizer<Word> tokenizer =  PTBTokenizer.newPTBTokenizer(in);
	    
	    sent = tokenizer.tokenize();
	    
	    List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
	    
		parse = Constants.lp.apply(rawWords);	    
		
		push_to_map(parse, parse.yield());
		
	    tdl = Util.get_tdls(parse);
	    
	    root = Util.get_root(this);
	    
		root_index = root.dep().index() - 1;

	}
}

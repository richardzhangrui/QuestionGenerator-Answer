package com.util;

import com.common.Sent;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;


public class Reorderer {
	public static void reorder(Sent input) {
		
		int len = input.tdl.size();
		int root_index = input.root_index;
		
		
		for (int i = 0; i < len; i++) {
			TypedDependency rel = input.tdl.get(i);
			int d_index = rel.dep().index() - 1;
			
			if (rel.reln().toString().startsWith("prep") && d_index < root_index 
					&& rel.gov().index() == root_index + 1) {
				
				Tree node = input.index_to_nodes.get(d_index);
				
				while(node.parent(input.parse) != null && node.parent(input.parse).parent(input.parse) != null 
						&& !node.parent(input.parse).parent(input.parse).label().toString().equals("PP")) {
					node = node.parent(input.parse);
				}
	
				if (node.parent(input.parse) != null && node.parent(input.parse).parent(input.parse) != null) {
					
					node = node.parent(input.parse).parent(input.parse).firstChild().firstChild();
					
					int p_index = input.parse.yield().indexOf(node.label());
					
					input.reorder_range.add(p_index);
					input.reorder_range.add(d_index);
					
					for (int j = p_index; j <= d_index; j++) {
						input.reorder_indexes.add(j);
					}
				}
			}
		}
		
	}
}

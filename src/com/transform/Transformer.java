package com.transform;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;


public class Transformer {
	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("annotators", "tokenize,ssplit,pos,lemma,parse");
		StanfordCoreNLP snlp = new StanfordCoreNLP(props);
		
		String text = "the quick brown fox jumps over the lazy dog";
		Annotation document = new Annotation(text);
		snlp.annotate(document);
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap cm : sentences) {
			for (CoreMap token : cm.get(CoreAnnotations.TokensAnnotation.class)) {
				ArrayCoreMap aToken = (ArrayCoreMap) token;
				System.out.println(aToken.toShorterString());
	        }
		}
	}
}

package edu.cmu.ark;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

public class RelatedWords {
	private RelatedWords(){
		if (dict == null) {
			URL url;
			try {
				url = new URL("file" ,null ,"/Users/richard/HW/11611/project/WordNet-3.0/dict");
				dict = new Dictionary(url);
				dict.open();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	public static RelatedWords getInstance() {
		return new RelatedWords();
	}
	
	public List<String> getSynonyms(String word, POS pos) {
		IIndexWord idxWord = dict.getIndexWord(word, pos); 
		List<String> ret = new ArrayList<String>();
		if (idxWord == null)
			return ret;
		IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning IWord word = dict.getWord(wordID);
		IWord tmp = dict.getWord(wordID);
		ISynset synset = tmp.getSynset();
		// iterate over words associated with the synset
		
		for(IWord w : synset.getWords()) {
			//System.out.println(w.getLemma());
			ret.add(w.getLemma());
		}
		
		return ret;
	}
	
	public List<String> getAntonyms(String word, POS pos) {
		IIndexWord idxWord = dict.getIndexWord(word, pos); 
		List<String> ret = new ArrayList<String>();
		if (idxWord == null)
			return ret;
		
		IWordID wordID = idxWord.getWordIDs().get(0); 
		IWord tmp = dict.getWord(wordID);
			
		for (IWordID id : tmp.getRelatedWords(Pointer.HYPERNYM_INSTANCE)) {
			//System.out.println(dict.getWord(id));
			ret.add(dict.getWord(id).toString());
		}
		
		return ret;
	}
	
	private static IDictionary dict = null;
	
}

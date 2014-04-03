import java.util.HashSet;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class SearchAnswer {
	String question;
	HttpSolrServer server;
	HashSet<String> words;

	public SearchAnswer(String url){
		this.server = new HttpSolrServer(url);
		this.server.setRequestWriter(new BinaryRequestWriter());
		
		this.words = new HashSet<String>();
		words.add("Could");
		words.add("Can");

		words.add("Does");
		words.add("Do");
		words.add("Did");
		words.add("Have");

		words.add("Is");
		words.add("Are");
		words.add("Am");

		words.add("Was");
		words.add("Were");
		//
		// words.add("How");
		// words.add("When");
		// words.add("Where");
		// words.add("What");
		// words.add("Which");
		// words.add("Who");

	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String doQuery(String text) {
		SolrQuery query = new SolrQuery();
		//query.setQuery("content:" + text);
		 query.setQuery(text);
		System.out.println("Query is : " + text);
		try {
			QueryResponse rsp = this.server.query(query);
			SolrDocumentList docs = rsp.getResults();
			// now only return the first sentence
			return docs.get(0).getFieldValue("content").toString();

		} catch (SolrServerException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String analyzeQuestion(String question) {
		/*
		 * LexicalizedParser lp = LexicalizedParser.loadModel(
		 * "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		 * lp.setOptionFlags(new String[]{"-maxLength", "200",
		 * "-retainTmpSubcategories"});
		 * 
		 * PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(new
		 * StringReader(question)); List<Word> sent = tokenizer.tokenize();
		 */
		QuestionType qt;
		String[] tokens = question.split(" ");
		if (tokens.length <= 2) {
			return "I don't know!";
		}

		if (this.words.contains(tokens[0])) {
			// it is a Yes or No question.
			qt = QuestionType.YesOrNo;
		} else {
			qt = QuestionType.Other;
		}

		String recons = "";
		for (int i = 2; i < tokens.length; i++) {

			recons += tokens[i] + " ";
		}

		recons = recons.trim();
		recons = recons.substring(0, recons.length() - 1);
		return recons;
	}

	public static void main(String args[]) {
		SearchAnswer sa = new SearchAnswer("http://localhost:8983/solr");
		String query = sa.analyzeQuestion(args[0]);
		String response = sa.doQuery(query);
		System.out.println(response);
	}
}

package Index;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class IndexData {
	SolrServer server;
	public int paraID = 0;
	public int sentenceID = 0;
	public Collection<SolrInputDocument> docs;
	public StanfordCoreNLP pipeline;
	public void init() {
		this.server = new HttpSolrServer("http://localhost:8983/solr/nlp/");
		this.docs = new ArrayList<SolrInputDocument>();
		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit");
		this.pipeline = new StanfordCoreNLP(props);
	
	}

	public void geneDoc(String text) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.setField("paraID", paraID);
		doc.setField("id", sentenceID);
		doc.setField("content", text);
		this.sentenceID ++;
		this.docs.add(doc);
	}

	public void parseLine(String line) {
		this.paraID++;
		Annotation document = new Annotation(line);
		this.pipeline.annotate(document);
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			System.out.println(sentence);
			this.geneDoc(sentence.toString());
		}
	}

	public void update() {
		try {
			this.server.add(this.docs);
			this.server.commit();
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException {
		IndexData id = new IndexData();
		id.init();

		BufferedReader bi = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		String line;
		while ((line = bi.readLine()) != null) {
			if(line.equals("")){
				continue;
			}
			id.parseLine(line);
		}
		/*try {
			id.server.deleteByQuery("*:*");
			id.server.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		id.update();
	}
}
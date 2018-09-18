package iep;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import iep.lucene.IndexFiles;
import iep.lucene.SearchResult;

public class WriteIndex {
	private IndexFiles indexFiles;
	private Map<String, SearchResult> resultMap;
	private static final String homeUrl = "https://www.iep.utm.edu/";
	public static void main(String[] args) throws Exception {
		new WriteIndex().run();
	}
	
	private void run() throws Exception {
		indexFiles = new IndexFiles(); 
		resultMap = new HashMap<>();
		List<String> entries = Files.readAllLines(Paths.get("c:/users/karln/downloads/iep/index.csv"));
		for ( String entry: entries) {
		    String[] vals = entry.split("\\|");
		    String url = vals[0];
		    String name = url.replace(homeUrl, "").replace("/", "");
		    String[] ev = vals[1].split(",");
		    StringBuilder sb = new StringBuilder();
		    if ( ev.length > 1 ) {
		    	sb.append(ev[1].trim() + " ");
		    }
	    	sb.append(ev[0].trim());
			try ( InputStream in = Files.newInputStream(Paths.get("c:/users/karln/downloads/iep/" + name + ".html"))) {
				Document d = Jsoup.parse(in, StandardCharsets.UTF_8.name(), homeUrl);
				Element p = d.select(".entry-content").select("p").get(0);
				String text = p.text();
				if ( text != null && !text.isEmpty()) {
					resultMap.put(sb.toString(), new SearchResult(sb.toString(), url, text, 0.0f ));
				}
				in.close();
			}
		}
		for ( SearchResult sr: resultMap.values()) {
			indexFiles.indexEntry(sr.subject, sr.url, sr.preamble);
		}
		indexFiles.close();
	}

}

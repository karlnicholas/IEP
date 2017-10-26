package iep;

import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import iep.lucene.IndexFiles;
import iep.lucene.SearchResult;

public class WriteIndex {
	private IndexFiles indexFiles;
	private Map<String, SearchResult> resultMap;

	public static void main(String[] args) throws Exception {
		new WriteIndex().run();
	}
	private void run() throws Exception {
		indexFiles = new IndexFiles(); 
		resultMap = new HashMap<>();
		Path p = Paths.get("c://users/karln/downloads/iep/index.csv");
		try ( BufferedReader reader = Files.newBufferedReader(p, Charsets.UTF_8)) {
			CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT);
			for ( CSVRecord record: parser) {
				String name = record.get(1).replaceAll("http://www.iep.utm.edu/", "").replace("/", "");
				try ( InputStream is = Files.newInputStream(Paths.get("c://users/karln/downloads/iep/" + name )) ) {				
					parseDoc( Jsoup.parse(is, null, "http://www.iep.utm.edu/"), record );
				}				
			}
		}
		for ( SearchResult sr: resultMap.values()) {
			indexFiles.indexEntry(sr.subject, sr.url, sr.preamble);
		}
		//indexFiles.indexEntry(title, record.get(1), p.text());

		indexFiles.close();
	}
	private void parseDoc(Document doc, CSVRecord record) throws Exception {
		Elements els = doc.getElementsByClass("entry-content");
		Element p;
		int ppos = 0;
		int l;
		do { 
			p = els.get(0).getElementsByTag("p").get(ppos++);
			l = p.text().split(" ").length;
		} while ( l <= 1 );
		String title = getTitle(record.get(2));
		
		resultMap.put(title, new SearchResult( title, record.get(1), p.text(), 0.0f ));
		//indexFiles.indexEntry(title, record.get(1), p.text());
//		if ( title.contains("Ethics")) {
//			System.out.println(record.get(0)+":"+record.get(1)+":"+record.get(3)+title+":"+p.text());
//		}
	}
	private String getTitle(String title) {
		String nTitle = title;
		if ( title.contains(":") ) {
			nTitle = switchFunc(title, ":");
		}
		if ( nTitle.contains(",")) {
			nTitle = switchFunc(nTitle, ",");
		}
		return nTitle;
	}
	private String switchFunc( String s, String d) {
		String[] words = s.split(" ");
		int cl = 0;
		while ( !words[cl].contains(d) ) cl++;
		StringBuilder sb = new StringBuilder();
		for ( int i=cl+1; i < words.length; ++i ) {
			sb.append( words[i]);
			sb.append(' ');
		}
		for ( int i=0; i < cl+1; ++i ) {
			sb.append( words[i].replace(d, ""));
			sb.append(' ');
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
}
package sep;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReadIndex {
	
	public static void main(String[] args) throws IOException {
		new ReadIndex().run();
	}
	private void run() throws IOException {
		Document doc = Jsoup.parse(new File("c:/users/karln/downloads/view-source_https___plato.stanford.edu_archives_fall2016_contents.html"), null);
		doc.setBaseUri("https://plato.stanford.edu/archives/fall2016/");
		Element content = doc.getElementById("content");
		char c = 'a';
		char found = '0';
		ArrayList<IndexEntry> indexEntries = null;
		Map<Character, ArrayList<IndexEntry>> index = new HashMap<Character, ArrayList<IndexEntry>>(); 
		for ( Element element: content.select("a, li") ) {
			if ( element.id().equals(Character.toString(c))) {
				if ( indexEntries != null ) {
					index.put(found, indexEntries);
				}
				found = c;
				c++;
				indexEntries = new ArrayList<IndexEntry>();
			}
			if ( found != '0' && element.tag().getName().equals("li") && element.parent().parent() == content) {
				Elements link = element.select(":root > a");
				String name = null;
				ReferTo referTo = null;
				if ( link.size() == 0 ) {
					name = element.ownText();
					referTo = new ReferTo(name, null);
				} else {
					Element a = link.get(0);
					String url = a.absUrl("href");
					name = a.text();
					referTo = new ReferTo(name, new URL(url) );
				}
				String text = element.ownText();
				String author = null;
				if ( text.contains("(") ) {
					author = text.substring(text.indexOf("(")+1).replace(")", "");
				}
				if ( text.contains("— see")) {
					text = element.text();
					name = text.substring(0, text.indexOf("— see")).trim();
					IndexEntry indexEntry = new ReDirectIndexEntry(referTo, name);
					indexEntries.add(indexEntry);
					System.out.println(indexEntry);
					
				} else {
					// do subentries
					ArrayList<DirectIndexEntry> subEntries = new ArrayList<DirectIndexEntry>();
					for ( Element subEl: element.children().select("a, li") ) {
						if ( subEl.tag().getName().equals("li")) {
							Elements sublink = subEl.getElementsByTag("a");
							if ( sublink.size() > 0 ) {
								Element suba = sublink.get(0);
								String suburl = suba.absUrl("href");
								String subname = suba.text();
								ReferTo subreferTo = new ReferTo(subname, new URL(suburl) );
								String subtext = subEl.ownText();
								String subauthor = null;
								if ( subtext.contains("(") ) {
									subauthor = subtext.substring(subtext.indexOf("(")+1).replace(")", "");
								}
								DirectIndexEntry subEntry = new DirectIndexEntry(subreferTo, subauthor, null);
								subEntries.add(subEntry);
							}
						}
					}
					
					//
					IndexEntry indexEntry = new DirectIndexEntry(referTo, author, subEntries);
					indexEntries.add(indexEntry);
					System.out.println(indexEntry);
				}
			}
		}
		index.put(found, indexEntries);
		for ( Character key: index.keySet() ) {
			System.out.println(key + " = " + index.get(key).size());
		}
	}

}

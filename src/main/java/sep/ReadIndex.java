package sep;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReadIndex {
	
	Map<String, ReferTo> directs = new HashMap<String, ReferTo>();
	Map<String, ReferTo> reDirects = new HashMap<String, ReferTo>();
	int directDup = 0;
	int reDirectDup = 0;

	public static void main(String[] args) throws IOException {
		new ReadIndex().run();
	}
	private void run() throws IOException {
		Document doc = Jsoup.parse(new File("c:/users/karln/downloads/Table of Contents (Stanford Encyclopedia of Philosophy_Fall 2016 Edition).html"), null);
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
//					String url = a.absUrl("href");
					String url = a.attr("href");
					name = a.text();
//					referTo = new ReferTo(name, new URL(url) );
					referTo = new ReferTo(name, url );
				}
				String text = element.ownText();
				String author = null;
				if ( text.contains("(") ) {
					author = text.substring(text.indexOf("(")+1).replace(")", "");
				}
				// do subentries
				ArrayList<IndexEntry> subEntries = new ArrayList<IndexEntry>();
				for ( Element subEl: element.children().select("a, li") ) {
					if ( subEl.tag().getName().equals("li")) {
						Elements sublink = subEl.getElementsByTag("a");
						if ( sublink.size() > 0 ) {
							Element suba = sublink.get(0);
//							String suburl = suba.absUrl("href");
							String suburl = suba.attr("href");
							String subname = suba.text();
//							ReferTo subreferTo = new ReferTo(subname, new URL(suburl) );
							ReferTo subreferTo = new ReferTo(subname, suburl );
							String subtext = subEl.ownText();
							String subauthor = null;
							if ( subtext.contains("(") ) {
								subauthor = subtext.substring(subtext.indexOf("(")+1).replace(")", "");
							}
							if ( subtext.contains("— see")) {
								subtext = subEl.text();
								subname = subtext.substring(0, subtext.indexOf("— see")).trim();
								IndexEntry indexEntry = new ReDirectIndexEntry(subreferTo, subname, null);
								subEntries.add(indexEntry);
								// manage url's
								addReDirect(subreferTo);
							} else {
								IndexEntry subEntry = new DirectIndexEntry(subreferTo, subauthor, null);
								subEntries.add(subEntry);
								// manage url's
								addDirect(subreferTo);
							}
						}
					}
				}
				if ( text.contains("— see")) {
					text = element.text();
					name = text.substring(0, text.indexOf("— see")).trim();
					IndexEntry indexEntry = new ReDirectIndexEntry(referTo, name, subEntries);
					indexEntries.add(indexEntry);
					// manage url's
					addReDirect(referTo);
					System.out.println(indexEntry);
					
				} else {
					//
					IndexEntry indexEntry = new DirectIndexEntry(referTo, author, subEntries);
					indexEntries.add(indexEntry);
					// manage url's
					addDirect(referTo);
					System.out.println(indexEntry);
				}
			}
		}
		index.put(found, indexEntries);
		for ( Character key: index.keySet() ) {
			System.out.println(key + " = " + index.get(key).size());
		}
		System.out.println("directs = " + directs.size() + ", and " + directDup + " dups");
		System.out.println("reDirects = " + reDirects.size() + ", and " + reDirectDup + " dups");
/*		
		for ( String key: reDirects.keySet() ) {
			if ( !directs.containsKey(key)) {
				System.out.println("No direct for redirect: " + reDirects.get(key));
			}
		}
*/		
//		ripSite();
	}
	private void ripSite() throws ClientProtocolException, IOException {
		try ( CloseableHttpClient httpclient = HttpClients.createDefault() ) {
			int count = 0;
			for ( String url: directs.keySet() ) {
				if ( count++ % 100 == 0 ) System.out.println(count);
				HttpGet httpGet = new HttpGet("https://plato.stanford.edu/archives/fall2016/"+url);
				OutputStream fos = Files.newOutputStream(Paths.get("c:/users/karln/sep/" + url));
				CloseableHttpResponse response = httpclient.execute(httpGet);
				try {
				    HttpEntity entity = response.getEntity();
				    entity.writeTo(fos);
				} finally {
				    response.close();
				}
				fos.close();
			}
			httpclient.close();
		}
	}
	private void addDirect(ReferTo referTo) {
		if ( referTo.url == null ) return;
		if ( directs.get(referTo.url) != null ) {
			directDup++;			
		} else {
			directs.put(referTo.url, referTo);
		}
	}
	private void addReDirect(ReferTo referTo) {
		if ( referTo.url == null ) return;
		if ( reDirects.get(referTo.url) != null ) {
			reDirectDup++;			
		} else {
			reDirects.put(referTo.url, referTo);
		}
	}

}

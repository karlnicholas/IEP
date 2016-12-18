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

import lucene.IndexFiles;

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
//				ArrayList<IndexEntry> subEntries = new ArrayList<IndexEntry>();
				for ( Element subEl: element.children().select("a, li") ) {
					if ( subEl.tag().getName().equals("li")) {
						Elements sublink = subEl.getElementsByTag("a");
						if ( sublink.size() > 0 ) {
							Element suba = sublink.get(0);
//							String suburl = suba.absUrl("href");
							String suburl = suba.attr("href");
							String subname = suba.text();
							if ( !subname.contains(name)) {
								String parent = null;
								if ( subname.contains(":") ) {
									parent = subname.substring(subname.indexOf(':')+1);
								}
								if ( parent == null || !subname.contains(parent)) {									
									if ( name.contains(",")) {
										subname = subname + " of " + adjustName(name);
									} else {
										subname = adjustName(name) + " " + subname;
									}
								}
							}
//							ReferTo subreferTo = new ReferTo(subname, new URL(suburl) );
							ReferTo subreferTo = new ReferTo(subname, suburl );
							String subtext = subEl.ownText();
							String subauthor = null;
							if ( subtext.contains("(") ) {
								subauthor = subtext.substring(subtext.indexOf("(")+1).replace(")", "");
							}
							if ( subtext.contains("— see")) {
//								subtext = subEl.text();
//								subname = subtext.substring(0, subtext.indexOf("— see")).trim();
//								IndexEntry indexEntry = new ReDirectIndexEntry(subreferTo, subname, null);
//								subEntries.add(indexEntry);
								// manage url's
								addReDirect(subreferTo);
							} else {
//								IndexEntry subEntry = new DirectIndexEntry(subreferTo, subauthor, null);
//								subEntries.add(subEntry);
								// manage url's
								addDirect(subreferTo);
							}
						}
					}
				}

				if ( text.contains("— see")) {
					text = element.text();
					name = text.substring(0, text.indexOf("— see")).trim();
//					IndexEntry indexEntry = new ReDirectIndexEntry(referTo, name, subEntries);
//					indexEntries.add(indexEntry);
					// manage url's
					addReDirect(referTo);
//					System.out.println(indexEntry);
					
				} else {
					//
//					IndexEntry indexEntry = new DirectIndexEntry(referTo, author, subEntries);
//					indexEntries.add(indexEntry);
					// manage url's
					addDirect(referTo);
//					System.out.println(indexEntry);
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
		
		int count = 0;
		EntryParser entryParser = new EntryParser();
//		for ( String url: directs.keySet() ) {
//			if ( ++count%500 == 0 ) System.out.println(count);
//			String preamble = entryParser.parseEntry(url, Paths.get("c:/users/karln/sep/"+url));
//		}
/*
		IndexFiles indexFiles = new IndexFiles();
		Map<String, String> subjects = new HashMap<String, String>();
		for ( Character key: index.keySet() ) {
			for ( IndexEntry entry: index.get(key) ) {
				String name = adjustName(entry.referTo.name);
				if ( entry.referTo.url != null ) {
					if ( subjects.get(name) == null ) {
						String preamble = entryParser.parseEntry(entry.referTo.url, Paths.get("c:/users/karln/sep/"+entry.referTo.url));
						subjects.put( name, entry.referTo.url);
						indexFiles.indexEntry(name, entry.referTo.url, preamble);
					}
				}
				if ( entry.subEntries != null ) {
					for ( IndexEntry subEntry: entry.subEntries ) {
						String subName = adjustName(subEntry.referTo.name);
						if ( subEntry.referTo.url != null ) {
							if ( subjects.get(name) == null ) {
								String preamble = entryParser.parseEntry(subEntry.referTo.url, Paths.get("c:/users/karln/sep/"+subEntry.referTo.url));
								subjects.put( name+": "+subName, subEntry.referTo.url);
								indexFiles.indexEntry(name+": "+subName, subEntry.referTo.url, preamble);
							}
						}
					}
				}
			}
		}
		indexFiles.close();
*/		
/*				
		Map<String, String> subjects = new HashMap<String, String>();
		for ( Character key: index.keySet() ) {
			for ( IndexEntry entry: index.get(key) ) {
				String name = adjustName(entry.referTo.name);
				if ( entry.referTo.url != null ) {
					if ( subjects.get(name) == null ) {
						subjects.put( name, entry.referTo.url);
					}
				}
				if ( entry.subEntries != null ) {
					for ( IndexEntry subEntry: entry.subEntries ) {
						String subName = adjustName(subEntry.referTo.name);
						if ( subEntry.referTo.url != null ) {
							if ( subjects.get(name) == null ) {
//								subjects.put( name+": "+subName, subEntry.referTo.url);
								subjects.put( "parent: "+subName, subEntry.referTo.url);
							}
						}
					}
				}
			}
		}
*/				
		Map<String, String> subjects = new HashMap<String, String>();
		for ( ReferTo key: directs.values() ) {
			subjects.put(adjustName(key.name), key.url);
		}
		for ( ReferTo key: reDirects.values() ) {
			String rName = clipRedirect(key.name);
			if ( subjects.get(rName) == null ) {
				ReferTo rDirect = directs.get(key.url);
				if ( rDirect == null ) {
					System.out.println("subject not found: " + rName + ":" + key);
				} else {
					subjects.put(rName, key.url);
				}
			} else {
				if ( !subjects.get(rName).equals(key.url) ) {
					System.out.println("same subject, diff url: " + rName + "==" + subjects.get(rName) + "==" + key.url);
					subjects.put(rName, key.url);
				}
			}
		}
		System.out.println("Subjects.size = " + subjects.size());
		for ( String key: subjects.keySet() ) {
			System.out.println(key);
		}

		count = 0;
		IndexFiles indexFiles = new IndexFiles();
		for ( String key: subjects.keySet() ) {
			if ( ++count%100 == 0 ) System.out.println(count);
			String url = subjects.get(key);
			String preamble = entryParser.parseEntry(url, Paths.get("c:/users/karln/sep/"+url));
			indexFiles.indexEntry(key, url, preamble);
		}
		indexFiles.close();

	}
	private String clipRedirect(String name) {		String colon = null;
		if ( name.contains(":")) {
			int idx = name.indexOf(':');
			String first = name.substring(0, idx);
			colon = name.substring(idx+1).trim();
			name = first;
		}
		if ( name.contains(",")) {
			int idx = name.indexOf(',');
			String first = name.substring(0, idx);
			name = name.substring(idx + 1).trim() + ' ' + first.trim();
		}
		if ( colon != null ) {
			if ( colon.contains(" of") ) {
				name = colon + ' ' + name ;
			} else if ( colon.contains("and ")  ) {
				name = name + ' ' + colon;
			} else if ( !colon.contains(" ")  ){
				name = name+ ' ' + colon ;
			} else {
				name = colon + ' ' + name ;
			}
		}
		return name;
	}
	private String adjustName(String name) {
/*		
		String colon = null;
		if ( name.contains(":")) {
			int idx = name.indexOf(':');
			String first = name.substring(0, idx);
			colon = name.substring(idx+1).trim();
			name = first;
		}
*/		
		if ( name.contains(",")) {
			int idx = name.indexOf(',');
			String first = name.substring(0, idx);
			name = name.substring(idx + 1).trim() + ' ' + first.trim();
		}
//		if ( colon != null )
//			name = name + ": " + colon;
		return name;
	}
	private void ripSite() throws ClientProtocolException, IOException {
		try ( CloseableHttpClient httpclient = HttpClients.createDefault() ) {
			int count = 0;
			for ( String url: directs.keySet() ) {
				if ( ++count%100 == 0 ) System.out.println(count);
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

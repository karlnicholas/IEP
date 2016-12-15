package sep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseEntry {
	public static void main(String[] args) throws IOException {
		new ParseEntry().run("entries/abduction/", new File("c:/users/karln/downloads/Abduction (Stanford Encyclopedia of Philosophy_Fall 2016 Edition).html"));
	}
	private void run(String url, File file) throws IOException {
		Document doc = Jsoup.parse(file, null);
		doc.setBaseUri("https://plato.stanford.edu/archives/fall2016/"+url);

		Element elPreamble = doc.getElementById("preamble");
		Element elTocs = doc.getElementById("toc");
		Element elMainText = doc.getElementById("main-text");
//		Element elBibliography = doc.getElementById("bibliography");
		Element elRelatedEntries = doc.getElementById("related-entries");
//		Element elArticleCopyright = doc.getElementById("article-copyright");
		
		TOC toc = parseToc(elTocs);
		Map<String, List<String>> texts = splitMainText(elMainText);
		trimTocTexts(toc, texts);
		
	}
	private void trimTocTexts(TOC toc, Map<String, List<String>> texts) {
		for ( String key: toc.getUrls()) {
			if ( texts.get(key.replace("#",  "")) == null ) {
				toc.removeEntry(key);
			}
		}
	}
	private Map<String, List<String>> splitMainText(Element elMainText) {
		Map<String, List<String>> texts = new HashMap<String, List<String>>(); 
		List<String> text = null;
		String url = null;
		for(Element els: elMainText.select("p, h2, h3")) {
			String tag = els.tag().toString().toLowerCase();
			if ( tag.equals("h2") || tag.equals("h3") ) {
				if ( text != null ) texts.put(url, text);
				Element a = els.child(0);
				url = a.attr("name");
				text = new ArrayList<String>();
				continue;
			}
			if ( tag.equals("p") && text != null ) {
				text.add(els.html());
			}
		}
		if ( text != null ) texts.put(url, text);
		return texts;
	}
	
	private TOC parseToc(Element elTocs) {
		TOC toc = new TOC();
		for ( Element elEntry: elTocs.select("li")) {
			Elements elSubTocs = elEntry.children().select("li");
			List<TOCEntry> subEntries = null;
			if ( elSubTocs != null ) {
				subEntries = new ArrayList<TOCEntry>();
				for ( Element elSubEntry: elSubTocs) {
					Element as = elSubEntry.child(0);
					subEntries.add(new TOCEntry(as.attr("href"), as.ownText(), null));
				}
			}
			Element a = elEntry.child(0);
			toc.addEntry(new TOCEntry(a.attr("href"), a.ownText(), subEntries) );
		}
		return toc;
	}

}

package sep;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseEntry {
	int count = 0;
	public static void main(String[] args) throws IOException {
		new ParseEntry().run();
	}
	private void run() throws IOException {
		Files.list(Paths.get("c:/users/karln/sep/entries/")).forEach( (file) -> {
			try {
				parseEntry( "entries/" + file.getFileName().toString() + "/", file );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	private void parseEntry(String url, Path path) throws IOException {
		Document doc = Jsoup.parse(path.toFile(), null);
		if ( count++ % 100 == 0 ) System.out.println(count);
//		doc.setBaseUri("https://plato.stanford.edu/archives/fall2016/"+url);

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
				Elements a = els.select("a");
				if ( a.size() == 0 ) {
					url = els.attr("id");
				} else {
					url = a.get(0).attr("name");
				}
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
					Elements as = elSubEntry.select("a");
					if ( as.size() != 0 ) {
						subEntries.add(new TOCEntry(as.get(0).attr("href"), as.get(0).ownText(), null));
					}
				}
			}
			Elements as = elEntry.select("a");
			if ( as.size() != 0 ) {
				toc.addEntry(new TOCEntry(as.get(0).attr("href"), as.get(0).ownText(), subEntries) );
			}
		}
		return toc;
	}

}

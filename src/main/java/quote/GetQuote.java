package quote;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import iep.lucene.SearchResult;

public class GetQuote {
	protected static final String URL_PREFIX = "http://plato.stanford.edu/cgi-bin/encyclopedia/random";
	public SearchResult getEntryFromStanford() {
		String text = "";
		String subject = "";
		String url = "";
		Document doc;
		try {
			Response response = Jsoup.connect(URL_PREFIX)
	                .followRedirects(true) //to follow redirects
	                .execute();
			doc = response.parse();
			Elements preamble = doc.select("div[id=preamble] p");
			URL rUrl = response.url();
			if ( rUrl != null )
				url = rUrl.toString();
			
			Elements subjects = doc.select("div[id=aueditable] h1");
			if ( subjects != null ) 
				if ( subjects.first() != null )
					subject = subjects.first().ownText();
			
			if (preamble != null) {
				if (preamble.first() != null)
					text = preamble.first().text();
			}
		} catch (IOException e) {
//			log.error(e.getMessage());
			text = e.getLocalizedMessage();
		}
		return new SearchResult(subject, url, subject, text, (float)1.0);
	}

}

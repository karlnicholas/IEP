package sep;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import lucene.SearchFiles;
import lucene.SearchResult;

public class DoSearch {
	public static void main(String[] args) {
		new DoSearch().doSearch();
	}
	public void doSearch() {
		try {
			SearchFiles searchFiles = new SearchFiles();
			List<SearchResult> searchResults = searchFiles.query("Plato");
			if ( searchResults.size() > 0 ) {
				System.out.println("Found results: " + searchResults.size());
				for ( SearchResult searchResult: searchResults ) {
					System.out.println(searchResult);
				}
			}
			searchFiles.close();
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

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
			List<SearchResult> searchResults = searchFiles.query("ethics");
			if ( searchResults.size() > 0 ) {
				System.out.println("Found results: " + searchResults.size());
				for ( SearchResult searchResult: searchResults ) {
					System.out.println(searchResult);
				}
				System.out.println("Best result\n"+searchResults.get(0).preamble);
			}
			searchFiles.close();
		} catch (ParseException | IOException e) {
			//
			e.printStackTrace();
		}
	}

}

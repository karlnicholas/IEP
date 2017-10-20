package iep;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import iep.lucene.SearchFiles;
import iep.lucene.SearchResult;

public class DoSearch {
	public static void main(String[] args) throws ParseException, IOException {
		new DoSearch().doSearch();
	}
	public void doSearch() throws ParseException, IOException {
		SearchFiles searchFiles = new SearchFiles();
		List<SearchResult> searchResults = searchFiles.query("chomsky");
		if ( searchResults.size() > 0 ) {
			System.out.println("Found results: " + searchResults.size());
			for ( SearchResult searchResult: searchResults ) {
				System.out.println(searchResult);
			}
			System.out.println("Best result\n"+searchResults.get(0).preamble);
		}
		searchFiles.close();
	}

}

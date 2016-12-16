/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/** Simple command-line based search demo. */
public class SearchFiles {
	static final int hitsPerPage = 100;
	IndexReader reader;
	IndexSearcher searcher;
	Analyzer analyzer;

	public SearchFiles() {
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(IndexFiles.indexPath)));
			searcher = new IndexSearcher(reader);
			analyzer = new StandardAnalyzer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<SearchResult> query(String value) throws ParseException, IOException {

		QueryParser parser = new QueryParser("name", analyzer);
		Query query = parser.parse(value);

		return doPagingSearch(query);

	}

	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This demonstrates a typical paging search scenario, where the search
	 * engine presents pages of size n to the user. The user can then go to the
	 * next page if interested in the next hits.
	 * 
	 * When the query is executed for the first time, then only enough results
	 * are collected to fill 5 result pages. If the user wants to page beyond
	 * this limit, then the query is executed another time and all hits are
	 * collected.
	 * 
	 */
	private List<SearchResult> doPagingSearch(Query query) throws IOException {
		List<SearchResult> searchResults = new ArrayList<SearchResult>();
		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;

		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);
		if ( numTotalHits == 0 ) return searchResults; 

		hits = searcher.search(query, numTotalHits).scoreDocs;

		end = Math.min(hits.length, start + hitsPerPage);

		for (int i = start; i < end; i++) {

			Document doc = searcher.doc(hits[i].doc);
			String name = doc.get("name");
			if (name != null) {
				String url = doc.get("url");
				
				searchResults.add(new SearchResult(name, url, hits[i].score));
			}
		}
		return searchResults;
	}
}
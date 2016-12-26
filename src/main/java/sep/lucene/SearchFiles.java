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
package sep.lucene;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.FilterDirectoryReader.SubReaderWrapper;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

/** Simple command-line based search demo. */
public class SearchFiles {
	static final int hitsPerPage = 100;
	IndexReader reader;
	IndexSearcher searcher;
	Analyzer analyzer;

	public SearchFiles() {
		try {
			reader =  DirectoryReader.open(FSDirectory.open(Paths.get(SearchFiles.class.getResource("/index/").toURI())));
			searcher = new IndexSearcher(reader);
			analyzer = new EnglishAnalyzer();
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized List<SearchResult> query(String value) throws ParseException, IOException {

		return doPagingSearch(value);

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
	 * @throws ParseException 
	 * 
	 */
	private List<SearchResult> doPagingSearch(String value) throws IOException, ParseException {
		QueryParser parser = new QueryParser("name", analyzer);
		Query query = parser.parse(value);

		List<SearchResult> searchResults = new ArrayList<SearchResult>();
		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;

		// if nothing found?
		if ( numTotalHits == 0 ) {
			parser = new QueryParser("preamble", analyzer);
			query = parser.parse(value);
			results = searcher.search(query, hitsPerPage);
			hits = results.scoreDocs;

			numTotalHits = results.totalHits;
			if ( numTotalHits == 0 ) {
				return searchResults;
			}			
		}
		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		hits = searcher.search(query, numTotalHits).scoreDocs;

		end = Math.min(hits.length, start + hitsPerPage);

		float tScore = hits[0].score;
		int tCount = 0;
		for ( int i=1; i < end; ++i ) {
			if ( Float.compare(hits[i].score, tScore) == 0 ) {
				tCount = i;
			}
		}
		for (int i = start; i < end; i++) {

			Document doc = searcher.doc(hits[i].doc);
			String name = doc.get("name");
			if (name != null) {
				String url = doc.get("url");
				String subject = doc.get("subject");
				String preamble = null;
				if ( i <= tCount ) {
					preamble = doc.get("preamble");
				}
				searchResults.add(new SearchResult(name, url, subject, preamble, hits[i].score));
				if ( i == tCount ) {
					Collections.shuffle(searchResults);
				}
			}
		}

//		searchResults.get(0).preamble = searcher.doc(hits[0].doc);
		return searchResults;
	}

	public void listIndexes() {
		try {
			int docs = reader.getDocCount("name");
			for ( int docID = 0 ; docID < docs; ++docID ) {
				Fields terms = reader.getTermVectors(docID);
				int i = 0;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
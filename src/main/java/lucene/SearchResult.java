package lucene;

import sep.ReferTo;

public class SearchResult extends ReferTo {
	float score;
	public SearchResult(String name, String url, float score) {
		super(name, url);
		this.score = score;
	}
	@Override
	public String toString() {
		return super.toString()+":"+score;
	}
}

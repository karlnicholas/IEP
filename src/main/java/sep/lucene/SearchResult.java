package sep.lucene;

import sep.ReferTo;

public class SearchResult extends ReferTo {
	public float score;
	public String preamble;
	public SearchResult(String name, String url, String preamble, float score) {
		super(name, url);
		this.score = score;
		this.preamble = preamble;
	}
	@Override
	public String toString() {
		return super.toString()+":"+score;
	}
}

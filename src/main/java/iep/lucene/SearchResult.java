package iep.lucene;

import model.ReferTo;

public class SearchResult extends ReferTo {
	public float score;
	public String preamble;
	public String subject;
	public SearchResult(String name, String url, String subject, String preamble, float score) {
		super(name, url);
		this.subject = subject;
		this.score = score;
		this.preamble = preamble;
	}
	@Override
	public String toString() {
		return super.toString()+":"+score+":"+subject;
	}
}

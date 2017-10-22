package iep.lucene;

public class SearchResult {
	public String subject;
	public float score;
	public String preamble;
	public String url;
	public SearchResult(String subject, String url, String preamble, float score) {
		this.subject = subject;
		this.url = url;
		this.preamble = preamble;
		this.score = score;
	}
	@Override
	public String toString() {
		return subject+":"+score;
	}
}

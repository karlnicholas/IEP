package sep;

import java.util.List;

public class TOCEntry {
	String url;
	String title;
	List<TOCEntry> subEntries;
	public TOCEntry(String url, String title, List<TOCEntry> subEntries) {
		super();
		this.url = url;
		this.title = title;
		this.subEntries = subEntries;
	}

}

package sep;

import java.util.ArrayList;
import java.util.List;

public class TOC {
	List<TOCEntry> toc;
	public TOC() {
		 toc = new ArrayList<TOCEntry>();
	}
	public void addEntry(TOCEntry e) {
		toc.add(e);
	}
	public List<String> getUrls() {
		List<String> urls = new ArrayList<String>();
		for ( TOCEntry entry: toc) {
			urls.add(entry.url);
			if ( entry.subEntries != null ) {
				for ( TOCEntry subEntry: entry.subEntries) {
					urls.add(subEntry.url);
				}
			}
		}
		return urls;
	}
	public void removeEntry(String key) {
		for ( TOCEntry entry: toc) {
			if ( entry.url.equals(key)) {
				toc.remove(entry);
				return;
			}
			if ( entry.subEntries != null ) {
				for ( TOCEntry subEntry: entry.subEntries) {
					if ( subEntry.url.equals(key)) {
						toc.remove(subEntry);
						return;
					}
				}
			}
		}
		
	}
}

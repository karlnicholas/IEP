package sep;

import java.util.ArrayList;

public class DirectIndexEntry extends IndexEntry {
	String author;
	ArrayList<DirectIndexEntry> subEntries;
	public DirectIndexEntry(ReferTo referTo, String author, ArrayList<DirectIndexEntry> subEntries) {
		super(referTo);
		this.author = author;
		this.subEntries = subEntries;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DIRECT\t\t");
		sb.append(super.toString());
		sb.append("\t\t");
		sb.append(author==null?"****":author);
		if ( subEntries.size() > 0 ) {
			for ( DirectIndexEntry entry: subEntries) {
				sb.append("\n\tSUBENTRY\t");
				sb.append(entry.referTo.name);
				sb.append("\t\t");
				sb.append(entry.author==null?"****":entry.author);
			}
		}
		return sb.toString();
	}
}

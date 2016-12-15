package sep;

import java.util.ArrayList;

public class DirectIndexEntry extends IndexEntry {
	String author;
	ArrayList<IndexEntry> subEntries;
	public DirectIndexEntry(ReferTo referTo, String author, ArrayList<IndexEntry> subEntries) {
		super(referTo);
		this.author = author;
		this.subEntries = subEntries;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DIRECT\t");
		sb.append(super.toString());
		sb.append("\t\t");
		sb.append(author==null?"****":author);
		if ( subEntries != null && subEntries.size() > 0 ) {
			for ( IndexEntry entry: subEntries) {
				if ( entry instanceof DirectIndexEntry ) {
					sb.append("\n\tSUBDIRECT\t");
					sb.append(entry.referTo);
					sb.append("\t\t");
					sb.append(((DirectIndexEntry)entry).author==null?"****":((DirectIndexEntry)entry).author);
				} else if ( entry instanceof ReDirectIndexEntry ) {
					sb.append("\n\t+SUBREDIR\t");
					sb.append(((ReDirectIndexEntry)entry).name);
					sb.append(" -> ");
					sb.append(entry.referTo);
					
				}
			}
		}
		return sb.toString();
	}
}

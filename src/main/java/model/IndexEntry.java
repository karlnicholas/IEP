package model;

import java.util.ArrayList;

public abstract class IndexEntry {
	ReferTo referTo;
	ArrayList<IndexEntry> subEntries;
	public IndexEntry(ReferTo referTo, ArrayList<IndexEntry> subEntries) {
		super();
		this.referTo = referTo;
		this.subEntries = subEntries;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder(referTo.toString());
		if ( subEntries != null && subEntries.size() > 0 ) {
			for ( IndexEntry entry: subEntries) {
				if ( entry instanceof DirectIndexEntry ) {
					sb.append("\n\tSUBDIRECT\t");
					sb.append(entry.referTo);
//					sb.append("\t\t");
//					sb.append(((DirectIndexEntry)entry).author==null?"****":((DirectIndexEntry)entry).author);
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

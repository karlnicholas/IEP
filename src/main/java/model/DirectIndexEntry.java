package model;

import java.util.ArrayList;

public class DirectIndexEntry extends IndexEntry {
	String author;
	public DirectIndexEntry(ReferTo referTo, String author, ArrayList<IndexEntry> subEntries) {
		super(referTo, subEntries);
		this.author = author;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DIRECT\t");
		sb.append(super.toString());
		return sb.toString();
	}
}

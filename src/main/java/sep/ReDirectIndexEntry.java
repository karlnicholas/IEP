package sep;

import java.util.ArrayList;

public class ReDirectIndexEntry extends IndexEntry {
	String name;
	public ReDirectIndexEntry(ReferTo referTo, String name, ArrayList<IndexEntry> subEntries) {
		super(referTo, subEntries);
		this.name = name;
	}
	@Override
	public String toString() {
		return "+REDIR\t" + name + " -> " + super.toString();
	}
}

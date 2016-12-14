package sep;

public class ReDirectIndexEntry extends IndexEntry {
	String name;
	public ReDirectIndexEntry(ReferTo referTo, String name) {
		super(referTo);
		this.name = name;
	}
	@Override
	public String toString() {
		return "+REDIR\t" + name + " -> " + super.referTo.name;
	}
}

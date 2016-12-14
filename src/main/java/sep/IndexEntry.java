package sep;

public abstract class IndexEntry {
	ReferTo referTo;
	public IndexEntry(ReferTo referTo) {
		super();
		this.referTo = referTo;
	}
	public String toString() {
		return referTo.name;
		
	}
}

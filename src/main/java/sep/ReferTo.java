package sep;

import java.net.URL;

public class ReferTo {
	String name;
	URL url;
	public ReferTo(String name, URL url) {
		super();
		this.name = name;
		this.url = url;
	}

	public String toString() {
		return name + " = " + url;
	}
}

package qmap2;

public class EntField {
	public String name = "";
	public String value = "";
	
	public void parse(String str) {
		if(str == null) {
			name = "";
			value = "";
			return;
		}
		String[] s = str.split("\"", -1);
		if(s != null && s.length > 3) {
			name = s[1];
			value = s[3];
		}
	}
	
	public String toString() {
		return "\"" + name + "\" \"" + value + "\"";
	}
}

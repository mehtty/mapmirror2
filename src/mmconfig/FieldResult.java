package mmconfig;

import qmap2.EntField;

public class FieldResult {
	public static final char ACTION_NONE = ' ', ACTION_ADD = '+', ACTION_DELETE = '-';
	
	public EntField field = new EntField();
	public char action = ACTION_NONE;
	
	public void parse(String s) {
		reset();
		if(s == null) return;
		String[] ss = s.split("\"", -1);
		if(ss != null && ss.length > 0) {
			String a = ss[0].trim();
			if(a.length() > 0) {
				action = a.charAt(0);
			} else {
				action = ACTION_NONE;
			}
			field.parse(s);
		}

	}
	
	public String toString() {
		if(field == null) return "";
		return "" + (action==ACTION_NONE?"":action) + field.toString();
	}
	
	public void reset() {
		action = ACTION_NONE;
		if(field == null) field = new EntField();
		else field.parse(null);
	}
	
	public boolean valid() {
		return field != null && field.valid();
	}
}

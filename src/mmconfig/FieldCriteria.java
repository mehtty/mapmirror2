package mmconfig;

import qmap2.EntField;

public class FieldCriteria {
	public static final char MATCH_EXACT = '=', MATCH_ANY = '*', MATCH_NOT = '!', MATCH_GT = '>', MATCH_LT = '<';
	
	public EntField field = new EntField();
	public char action = MATCH_EXACT;
	
	public void parse(String s) {
		reset();
		if(s == null) return;
		String[] ss = s.split("\"", -1);
		if(ss != null && ss.length > 1) {
			field.name = ss[1];
			if(ss.length > 2) {
				String a = ss[2].trim();
				if(a.length() > 0) {
					action = a.charAt(0);
				} else {
					action = MATCH_EXACT;
				}
			} else {
				action = MATCH_ANY;
			}
			if(ss.length > 3) {
				field.value = ss[3];
			}
		}

	}
	
	public String toString() {
		if(field == null) return "";
		String retval = "\"" + field.name + "\"" + action;
		if(action != MATCH_ANY) {
			retval += "\"" + field.value + "\"";
		}
		return retval;
	}
	
	public void reset() {
		action = MATCH_EXACT;
		if(field == null) field = new EntField();
		else field.parse(null);
	}
	
	public boolean valid() {
		return field != null && field.valid();
	}
	
	public boolean matchesValue(EntField ef) {
		if(ef == null || !valid() || !ef.valid()) return false;
		//if(!field.name.equals(ef.name)) return false;
		if(action == MATCH_ANY) return true;
		if(action == MATCH_NOT) return !field.value.equals(ef.value);
		if(action == MATCH_GT) return field.value.compareTo(ef.value) > 0;
		if(action == MATCH_LT) return field.value.compareTo(ef.value) < 0;
		return field.value.equals(ef.value);
	}
}

package mmconfig;

import java.util.Vector;

public class FieldReplacement {
	//public static int ACTION_NOTHING = 0, ACTION_MATCH_EMPTY = 1, ACTION_ADD_MISSING = 2, ACTION_DELETE_ENT = 4, ACTION_DELETE_FIELD = 8;
	
	public final String div = "\"";
	public final String bigdiv = "\" \"";
	
//	public String classname = null;
//	public String fieldname = null;
//	public String oldValue = null;
//	public String newValue = null;
//	public int action = 0; 
	public boolean delete = false;

	public Vector<FieldCriteria> criteria = new Vector<FieldCriteria>();
	public Vector<FieldResult> results = new Vector<>();
	
	public boolean valid() {
		//return classname != null && fieldname != null && oldValue != null && newValue != null;
		return criteria != null && criteria.size() > 0 && (delete || (results != null && results.size() > 0));
	}
	
	public String toDebugString() {
		String retval = "";
		if(!valid()) retval += "Invalid Field Replacement...\n";
		retval += "Criteria:\n";
		for(int i = 0; i < criteria.size(); i++) {
			retval += "\t" + criteria.get(i) + "\n";
		}
		retval += "Resutls:\n";
		if(delete) retval += "\tDELETE\n";
		for(int i = 0; i < results.size(); i++) {
			retval += "\t" + results.get(i) + "\n";
		}
		return retval;
	}
	
	public String toString() {
		if(!valid()) return "#INVALID";
		return criteria.firstElement().toString() + (criteria.size() > 1?"...":"") + " -> " + (delete?"DELETE":(results.firstElement().toString() + (results.size()>1?"...":"")));
	}
	
	/*
	public boolean parse(String s) {
		if(s == null) return false;
		String[] ss = s.split(div, -1);
		if(ss.length < 9) return false;
		classname = ss[1];
		fieldname = ss[3];
		oldValue = ss[5];
		newValue = ss[7];
		try {
			action = Integer.parseInt(ss[8].trim());
		} catch (NumberFormatException nfe) {
			System.out.println("Failed to parse action");
			action = 0;
		}
		return true;
	}

	public boolean isAction(int a) {
		return (action & a) == a;
	}
	*/	
//	public String toString() {
		//return div + classname + bigdiv + fieldname + bigdiv + oldValue + bigdiv + newValue + div + " " + action;
//		String retval = 
//	}
}

package mmconfig;

public class FieldReplacement {
	public static int ACTION_NOTHING = 0, ACTION_MATCH_EMPTY = 1, ACTION_ADD_MISSING = 2, ACTION_DELETE_ENT = 4;
	
	public final String div = "\"";
	public final String bigdiv = "\" \"";
	
	public String classname = null;
	public String fieldname = null;
	public String oldValue = null;
	public String newValue = null;
	public int action = 0; 
	
	public boolean valid() {
		return classname != null && fieldname != null && oldValue != null && newValue != null;
	}
	
	public boolean parse(String s) {
		if(s == null) return false;
		String[] ss = s.split(div);
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

	
	public String toString() {
		return div + classname + bigdiv + fieldname + bigdiv + oldValue + bigdiv + newValue + div + " " + action;
	}
}

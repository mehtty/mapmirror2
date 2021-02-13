package qmap2;

public class QVector {
	public double x,y,z;
	
	public static String format(double d) {
		int i = (int)d;
		if(d == i) {
			return "" + i;
		}
		return "" + d;
	}
	
	public void parse(String str) {
		int stage = 0;
		String tmp = "";
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if(c == '-' || c == '.' || Character.isDigit(c)) {
				switch(stage) {
				case 0: 
				case 2: 
				case 4: 
					stage++;
					tmp = "" + c;
					break;
				case 1:
				case 3:
				case 5:
					tmp += c;
				}
			} else {
				switch(stage) {
				case 0:
				case 2:
				case 4:
					break;
				case 1:
					stage = 2;
					try {
						x = Double.parseDouble(tmp);
					} catch (NumberFormatException nfe) {}
					break;
				case 3:
					stage = 4;
					try {
						y = Double.parseDouble(tmp);
					} catch (NumberFormatException nfe) {}
					break;
				}
			}
		}
		try {
			z = Double.parseDouble(tmp);
		} catch (NumberFormatException nfe) {}
	}
	
	public String toString() {
		return "" + format(x) + " " + format(y) + " " + format(z);
	}
	
	public void transform(boolean flipx, boolean flipy, boolean flipz, double movex, double movey, double movez) {
		if(flipx) x = x * -1;
		if(flipy) y = y * -1;
		if(flipz) z = z * -1;
		x += movex;
		y += movey;
		z += movez;
	}
	
	public static void main(String[] args) {
		QVector v = new QVector();
		v.parse("\"1 2 3\"");
		System.out.println(v);
		v.parse("4 5.8 6");
		System.out.println(v);
		v.parse("aefas 7 asfeasef ase8dfa -9");
		System.out.println(v);
	}
}

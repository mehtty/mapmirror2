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
	
	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
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
	
	public static QVector diff(QVector v1, QVector v2) {
		if(v1 == null) return v2;
		if(v2 == null) return v1;
		QVector v = new QVector();
		v.x = v1.x - v2.x;
		v.y = v1.y - v2.y;
		v.z = v1.z - v2.z;
		return v;
	}
/*
    cx = aybz − azby
    cy = azbx − axbz
    cz = axby − aybx	
*/
	public static QVector crossProduct(QVector v1, QVector v2) {
		if(v1 == null) return v2;
		if(v2 == null) return v1;
		QVector v = new QVector();
		v.x = v1.y*v2.z - v1.z*v2.y;
		v.y = v1.z*v2.x - v1.x*v2.z;
		v.z = v1.x*v2.y - v1.y*v2.x;
		return v;
	}
	
	public static double length(QVector v) {
		if(v == null) return 0;
		return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2) + Math.pow(v.z, 2));
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

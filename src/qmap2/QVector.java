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
	
	public String toString() {
		return "" + format(x) + " " + format(y) + " " + format(z);
	}
}

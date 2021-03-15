package qmap2;

public class BrushFace {
	public QVector v1 = new QVector(), v2 = new QVector(), v3 = new QVector();
	public Texture texture = new Texture();
	public double xoffset, yoffset, rotation, xscale, yscale;
	public Double angle = null, hangle = null;
	public QVector normal = null;
	public double normalLength = 0;

	public void reset() {
		v1 = new QVector();
		v2 = new QVector();
		v3 = new QVector();
		texture.name = "";
		xoffset = 0;
		yoffset = 0;
		rotation = 0;
		xscale = 0;
		yscale = 0;
	}
	
	public void parse(String str) {
		reset();
		if(str == null) {
			return;
		}
		String[] s = str.split(" ", -1);
		if(s != null && s.length > 20) {
			try {
				v1.x = Double.parseDouble(s[1].trim());
				v1.y = Double.parseDouble(s[2].trim());
				v1.z = Double.parseDouble(s[3].trim());
				v2.x = Double.parseDouble(s[6].trim());
				v2.y = Double.parseDouble(s[7].trim());
				v2.z = Double.parseDouble(s[8].trim());
				v3.x = Double.parseDouble(s[11].trim());
				v3.y = Double.parseDouble(s[12].trim());
				v3.z = Double.parseDouble(s[13].trim());
				texture.name = s[15].trim();
				xoffset = Double.parseDouble(s[16].trim());
				yoffset = Double.parseDouble(s[17].trim());
				rotation = Double.parseDouble(s[18].trim());
				xscale = Double.parseDouble(s[19].trim());
				yscale = Double.parseDouble(s[20].trim());
			} catch (Exception ex) {
				System.out.println("Error parsing face: " + ex.getMessage());
			}
		}
	}
	
	public String toString() {
		return "( " + v1 + " ) ( " + v2 + " ) ( " + v3 + " ) " + texture + " " + QVector.format(xoffset) + " " + QVector.format(yoffset) + " " + QVector.format(rotation) + " " + QVector.format(xscale) + " " + QVector.format(yscale);
	}
	
	public void transform(boolean flipx, boolean flipy, boolean flipz, double movex, double movey, double movez, boolean fullRotate, boolean fullFlip) {
		System.out.print("Transforming: " + toString());
		if(v1 != null) v1.transform( flipx,  flipy,  flipz,  movex,  movey,  movez);
		if(v2 != null) v2.transform( flipx,  flipy,  flipz,  movex,  movey,  movez);
		if(v3 != null) v3.transform( flipx,  flipy,  flipz,  movex,  movey,  movez);
		if(fullRotate || fullFlip) {
			Double a = getAngle();
			System.out.print(" Angle: " + a);
			if(a == null) return;
			Double ha = getHAngle();
			System.out.print(" Hor.Angle: " + ha);
			if(ha == null) return;
			double aa = Math.abs(a);
			double haa = Math.abs(ha); //Also, exactly 45 behaves like Y-plane, at least in TB
			if(aa <= (Math.PI / 4) || (Math.PI - aa) >= (Math.PI * 0.75)) {
				// Face is vertical(ish)
				System.out.print(" -> vertical");
				if(fullRotate) {
					rotation = rotation * -1;
					//xoffset = xoffset * -1;
				}
				if(haa <= (Math.PI / 4) || (Math.PI - haa) >= (Math.PI * 0.75)) {
					xoffset += movey;
				} else {
					xoffset += movex;
				}
				yoffset += movez;
				if(fullFlip) {
					xscale = xscale * -1;
				}
			} else {
				// Face is horizontal(isH)
				System.out.print(" -> horizontal");
				if(fullRotate) {
					rotation = (rotation + 180) % 360;
					//xoffset = xoffset * -1;
					//yoffset = yoffset * -1;
				}
				xoffset += movex;
				yoffset += movey;
				if(fullFlip) {
					//xscale = xscale * -1;
					//yscale = yscale * -1;
				}
			}
		}
		System.out.println("\n\t-> " + toString());
	}
	
	public void calculateNormal() {
		QVector va = QVector.diff(v2, v1);
		QVector vb = QVector.diff(v3, v1);
		normal = QVector.crossProduct(va, vb);
		normalLength = QVector.length(normal); 
	}
	
	public Double calculateAngle() {
		if(normal == null) calculateNormal();
		double shadowLength = Math.sqrt(Math.pow(normal.x, 2) + Math.pow(normal.y, 2));
		if(normalLength == 0) {
			angle = null;
		} else {
			angle = Math.acos(shadowLength/normalLength);
		}
		return angle;
	}
	
	// Returns the angle between the horizontal plane and the normal vector to the face. 
	// 0/Pi = face is vertical, Pi/2 = horizontal
	public Double getAngle() {
		if(angle == null) return calculateAngle();
		return angle;
	}
	
	public Double calculateHAngle() {
		if(normal == null) calculateNormal();
		double shadowLength = Math.sqrt(Math.pow(normal.z, 2) + Math.pow(normal.y, 2));
		if(normalLength == 0) {
			hangle = null;
		} else {
			hangle = Math.acos(shadowLength/normalLength);
		}
		return hangle;
	}
	
	// Returns the angle between the horizontal plane and the normal vector to the face. 
	// 0/Pi = face is vertical, Pi/2 = horizontal
	public Double getHAngle() {
		if(hangle == null) return calculateAngle();
		return hangle;
	}
}

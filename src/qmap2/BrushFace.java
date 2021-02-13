package qmap2;

public class BrushFace {
	public QVector v1 = new QVector(), v2 = new QVector(), v3 = new QVector();
	public Texture texture = new Texture();
	public double xoffset, yoffset, rotation, xscale, yscale;

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
	
	public void transform(boolean flipx, boolean flipy, boolean flipz, double movex, double movey, double movez) {
		if(v1 != null) v1.transform( flipx,  flipy,  flipz,  movex,  movey,  movez);
		if(v2 != null) v2.transform( flipx,  flipy,  flipz,  movex,  movey,  movez);
		if(v3 != null) v3.transform( flipx,  flipy,  flipz,  movex,  movey,  movez);
	}
}

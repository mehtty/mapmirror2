package mmconfig;

public class TextureReplacement {
	public String oldTexture = null;
	public String newTexture = null;
	
	public boolean valid() {
		return oldTexture != null && newTexture != null;
	}
	
	public boolean parse(String s) {
		if(s == null) return false;
		String[] ss = s.split(" ", -1);
		if(ss.length < 2) return false;
		oldTexture = ss[0];
		newTexture = ss[1];
		return true;
	}
	
	public String toString() {
		return oldTexture + " " + newTexture;
	}
}

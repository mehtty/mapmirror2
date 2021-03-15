package mmconfig;

public class TextureReplacement {
	public final static String TAG_MIRROR = "M";
	public String oldTexture = null;
	public String newTexture = null;
	public boolean mirror = false;
	
	public boolean valid() {
		return oldTexture != null && newTexture != null;
	}
	
	public boolean parse(String s) {
		if(s == null) return false;
		String[] ss = s.split(" ", -1);
		if(ss.length < 2) return false;
		oldTexture = ss[0];
		newTexture = ss[1];
		if(ss.length > 2) {
			mirror = TAG_MIRROR.equals(ss[2]);
		}
		return true;
	}
	
	public String toString() {
		return oldTexture + " " + newTexture + (mirror?(" " + TAG_MIRROR):"");
	}
}

package mmconfig;

public class ConfigTest {
	public static void main(String[] args) {
		ConfigFile cf = new ConfigFile();
		ConfigFile.DEBUG = true;
		//cf.load("mapmirror.conf");
		cf.load("bob");
		
		System.out.println("Config " + cf.filename + " loaded");
		System.out.println("\tMap: " + cf.mapname);
		System.out.println("\tOutput: " + cf.outname);
		System.out.println("\tTexture Replacements: " + cf.texture_replacements.size());
		for(int i = 0; i < cf.texture_replacements.size(); i++) {
			System.out.println("\t\t " + cf.texture_replacements.get(i));
		}
		System.out.println("\tField Replacements: " + cf.field_replacements.size());
		for(int i = 0; i < cf.field_replacements.size(); i++) {
			System.out.println("\t\t " + cf.field_replacements.get(i).toDebugString());
		}
		
		cf.saveToFile("test.conf");
	}
}

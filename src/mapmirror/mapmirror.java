package mapmirror;

import java.util.Vector;

import mmconfig.*;
import qmap2.*;

public class mapmirror {
	public static final String VERSION = "0.2j";
	
	public static boolean DEBUG = false;
	public ConfigFile config = new ConfigFile();
	public QMapFile map = new QMapFile();
	
	public mapmirror() {
		config.filename = "mapmirror.conf";
		config.mapname = "a.map";
		config.outname = "out.map";
	}
	
	public int replaceTextures() {
		int count = 0;
		for(int i = 0; i < config.texture_replacements.size(); i++) {
			TextureReplacement tr = config.texture_replacements.get(i);
			if(tr == null) continue;
			for(int j = 0; j < map.textures.size(); j++) {
				Texture t = map.textures.get(j);
				if(t == null || t.name == null) continue;
				if(t.name.equals(tr.oldTexture)) {
					if(DEBUG) System.out.println("Replacing " + t.name + " -> " + tr.newTexture);
					map.textures.get(i).name = tr.newTexture;
					count++;
				}
			}
		}
		return count;
	}
	
	public int replaceEntities(Vector<MapThing> things, FieldReplacement fr) {
		if(things == null || fr == null || !fr.valid()) {
			return 0;
		}
		int count = 0;
		for(int i = 0; i < things.size(); i++) {
			MapThing mt = things.get(i);
			if(mt == null) {
				continue;
			}
			if(fr.classname.equals(mt.classname)) {
				for(int j = 0; j < mt.fields.size(); j++) {
					EntField ef = mt.fields.get(j);
					if(ef == null) continue;
					if(fr.fieldname.equals(ef.name)) {
						if(fr.oldValue.length() == 0 || fr.oldValue.equals(ef.value)) {
							ef.value = fr.newValue;
							count++;
							break;
						}
					}
				}
			}
			count += replaceEntities(mt.subobjects, fr);
		}
		return count;
	}
	
	public int replaceEntities() {
		int count = 0;
		for(int i = 0; i < config.field_replacements.size(); i++) {
			FieldReplacement fr = config.field_replacements.get(i);
			if(fr == null) continue;
			count += replaceEntities(map.stuff, fr);
		}
		return count;
	}

	public void parseCmd(String[] args) {
		if(args != null) {
			for(int i = 0; i < args.length; i++) {
				if("-h".equals(args[i]) || "--help".equals(args[i])) {
					printUsage();
					System.exit(0);
				}
				if("-d".equals(args[i])) {
					DEBUG = true;
				} else if("-c".equals(args[i]) && i+1 < args.length) {
					config.filename = args[++i];
				} else if("-o".equals(args[i]) && i+1 < args.length) {
					config.outname = args[++i];
				} else {
					config.mapname = args[i];
				}
			}
		}
		ConfigFile.DEBUG = DEBUG;
		QMapFile.DEBUG = DEBUG;
		MapThing.DEBUG = DEBUG;
	}
	
	public static void printUsage() {
	    System.out.print("Map Mirror %s\nMEHT\n\n" + VERSION);
	    System.out.print("Usage: mapmirror [options] <mapfile.map>\n");
	    System.out.print("\t-h | --help  Print this info and exit\n");
	    System.out.print("\t-d           Print extra debugging info\n");
	    System.out.print("\t-c <file>    Config file to use (default: mapmirror.conf)\n");
	    System.out.print("\t-o <file>    Output file to write (default: out.map)\n");
	}
	
	public static void main(String[] args) {
		mapmirror m = new mapmirror();
		m.parseCmd(args);
		System.out.println("MapMirror: " + VERSION);
		System.out.println("\tConfig: " + m.config.filename);
		m.config.load();
		System.out.println("\tInput Map: " + m.config.mapname);
		System.out.println("\tOutput Map: " + m.config.outname);
		m.map.loadFromFile(m.config.mapname);
		System.out.println("Replaced " + m.replaceTextures() + " textures");
		System.out.println("Replaced " + m.replaceEntities() + " fields");
		System.out.println("Saving Map: " + m.config.outname);
		m.map.saveToFile(m.config.outname);
		System.out.println("Completed");
	}
}

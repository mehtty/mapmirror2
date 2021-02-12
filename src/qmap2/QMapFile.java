package qmap2;

import java.io.*;
import java.util.Vector;

public class QMapFile {
	public static boolean DEBUG = false;

	public String name = "";
	public Vector<MapThing> stuff = new Vector<>();
	public Vector<String> wads = new Vector<>();
	public Vector<Texture> textures = new Vector<>();

	public void loadFromFile(String filename) {
		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader(filename));
			name = filename.substring(0, filename.lastIndexOf('.'));
			if (DEBUG)
				System.out.println("Reading map: " + name);
			MapThing qmo = new MapThing();
			while (qmo.readFromFile(bf, this)) {
				stuff.add(qmo);
				if (DEBUG)
					System.out.println("Added qmo: " + qmo);
				qmo = new MapThing();
			}
			if (DEBUG)
				System.out.println("Read " + stuff.size() + " objects.");
			bf.close();
		} catch (Exception ex) {
			System.out.println("Error reading file " + filename + ": " + ex.getMessage());
		}
	}

	public Texture addTexture(Texture tex) {
		if (tex == null || tex.name == null)
			return tex;

		for (int i = 0; i < textures.size(); i++) {
			Texture t = textures.get(i);
			if (t == null)
				continue;
			if (tex.name.equals(t.name)) {
				return t;
			}
		}

		textures.add(tex);
		return tex;
	}
	public void saveToFile(BufferedWriter bw, Vector<MapThing> things) throws IOException {
		if(things == null || bw == null) return;
		for(int i = 0; i < things.size(); i++) {
			MapThing mt = things.get(i);
			if(mt.comment != null) {
				bw.write(mt.comment);
//				bw.newLine();
			}
			if(mt.fields.size() > 0 || mt.faces.size() > 0 || mt.subobjects.size() > 0) {
				bw.write(MapThing.START_OBJ);
				bw.newLine();
				for(int j = 0; j < mt.fields.size(); j++) {
					bw.write(mt.fields.get(j).toString());
					bw.newLine();
				}
				for(int j = 0; j < mt.faces.size(); j++) {
					bw.write(mt.faces.get(j).toString());
					bw.newLine();
				}
				saveToFile(bw, mt.subobjects);
				bw.write(MapThing.END_OBJ);
				bw.newLine();
			}
		}
	}
	public void saveToFile(String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			saveToFile(bw, stuff);
			bw.close();
		} catch (Exception ex) {
			System.out.println("Error writing file " + filename + ": " + ex.getMessage());
		}
	}
}

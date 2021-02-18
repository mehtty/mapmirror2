package qmap2;

import java.io.*;
import java.util.Vector;

public class QMapFile {
	public static boolean DEBUG = false;

	public String name = "";
	public Vector<MapThing> stuff = new Vector<MapThing>();
	public Vector<String> wads = new Vector<String>();
	public Vector<Texture> textures = new Vector<Texture>();
	public MapThing worldspawn = null;

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
	
	@SuppressWarnings("unchecked")
	public QMapFile clone() {
		QMapFile mf = new QMapFile();
		try {
			mf.name = new String(name);
			mf.stuff = (Vector<MapThing>) stuff.clone();
			mf.wads = (Vector<String>) wads.clone();
			mf.textures = (Vector<Texture>) textures.clone();
		} catch (Exception ex) {
			System.out.println("Error cloning Map File " + name);
		}
		return mf;
	}
	
	public QMapFile merge(QMapFile target) {
		if(target == null || target.stuff == null) return clone();
		QMapFile mf = this;
		for(int i = 0; i < target.stuff.size(); i++) {
			MapThing mt = target.stuff.get(i);
			if(mt == target.worldspawn) {
				continue;
			}
			mf.stuff.add(mt);
		}
		if(mf.worldspawn != null && target.worldspawn != null) {
			//override fields with new values if exist, otherwise merge sets
			Vector<EntField> toAdd = new Vector<EntField>();
//			System.out.println("MERGING WORLDSPAWNS!");
			for(int i = 0; i < target.worldspawn.fields.size(); i++) {
				EntField tef = target.worldspawn.fields.get(i);
				if(tef == null) continue;
//				System.out.println("target " + i + " = " + tef);
				boolean found = false;
				for(int j = 0; j < mf.worldspawn.fields.size(); j++) {
					EntField mef = mf.worldspawn.fields.get(j);
					if(mef == null) continue;
//					System.out.println("original " + j + " = " + mef);
					if(tef.name.equals(mef.name)) {
//						System.out.println("They are the same");
						mef.value = tef.value;
						found = true;
					}
				}
				if(!found) {
//					System.out.println("Thus adding " + tef);
					toAdd.add(tef);
				}
			}
			mf.worldspawn.fields.addAll(toAdd);
			System.out.println("Worldspawn: before: " + mf.worldspawn.subobjects.size() + " + " + target.worldspawn.subobjects.size());
			//mf.worldspawn.subobjects.addAll(target.worldspawn.subobjects);
			//hmm, seems like having 2 "// brush 0" comments before brushes in worldspawn goes funky in TB, so just omit the comments since I cbf parsing them
			for(int i = 0; i < target.worldspawn.subobjects.size(); i++) {
				MapThing mt = target.worldspawn.subobjects.get(i);
				if(mt == null) continue;
				if(mt.comment == null || mt.comment.length() == 0) {
					mf.worldspawn.subobjects.add(mt);
				}
			}
			System.out.println("Worldspawn: after: " + mf.worldspawn.subobjects.size());
		}
		return mf;
	}
}

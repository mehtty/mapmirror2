package qmap2;

import java.io.BufferedReader;
import java.util.Vector;

public class MapThing {
	public static boolean DEBUG = false;
	
	public static String START_COMMENT = "//";
	public static String END_COMMENT = "\n";
	public static String START_OBJ= "{";
	public static String END_OBJ = "}";
	public static String START_FIELD = "\"";
	public static String END_FIELD = "\n";
	public static String START_FACE = "(";
	public static String END_FACE = "\n";
	
	public final String BRUSH = "brush", WORLDSPAWN = "worldspawn", CLASSNAME = "classname" ;
	
	public QMapObjects qType = QMapObjects.None;
	
	public String classname = BRUSH;
	public Vector<EntField> fields = new Vector<EntField>();
	public Vector<BrushFace> faces = new Vector<BrushFace>();
	public String comment = null;
	public boolean hasFields = false;
	
	public Vector<MapThing> subobjects = new Vector<MapThing>();

	public MapThing() {}

	public boolean readFromFile(BufferedReader fb, QMapFile map) {
		return readFromFile(fb, map, "");
	}
	public boolean readFromFile(BufferedReader fb, QMapFile map, String prefix) {
		try {
			int ch = 0;
			String line = "";
			String end = "";
			while(true) {
				ch = fb.read();
				if(ch <= 0) {
					if(DEBUG) System.out.println("Mapthing returning false");
					return false;
				}
				line += (char)ch;
				if(qType == QMapObjects.None) {
					if(line.length() > 1 && line.endsWith(START_COMMENT)) {
						end = END_COMMENT;
						qType = QMapObjects.Comment;
					} 
					if(START_COMMENT.equals(prefix)) {
						end = END_COMMENT;
						qType = QMapObjects.Comment;
						line = prefix + line;
					} 
					if(line.endsWith(START_OBJ)) {
						end = END_OBJ;
						qType = QMapObjects.Thing;
						if(DEBUG) System.out.println("Starting Thing");
					}
					if(START_OBJ.equals(prefix)) {
						end = END_OBJ;
						qType = QMapObjects.Thing;
						if(DEBUG) System.out.println("Continuing Thing");
					}
				} else if(qType == QMapObjects.Thing) {
					if(line.length() > 1 && line.endsWith(START_COMMENT)) {
						if(DEBUG) System.out.println("Comment in Thing");
						MapThing qmo = new MapThing();
						if(qmo.readFromFile(fb, map, START_COMMENT)) {
							subobjects.add(qmo);
							if(DEBUG) System.out.println("Added subcomment: <<" + qmo + ">>");
						}						
					}					
					if(line.endsWith(START_OBJ)) {
						//end = END_OBJ;
						//qType = QMapObjects.Thing;
						if(DEBUG) System.out.println("Starting Sub Thing");
//						QMapObject qmo = new QMapObject();
						MapThing mt = new MapThing();
						if(mt.readFromFile(fb, map, START_OBJ)) {
							subobjects.add(mt);
							//subobjects.add(qmo.getIt());
							if(DEBUG) System.out.println("Added subthing: <<" + mt + ">>");
						}						
					}
					if(line.endsWith(START_FIELD)) {
						String s = START_FIELD + fb.readLine();
						addField(s);
					}
					if(line.endsWith(START_FACE)) {
						String s = START_FACE + fb.readLine();
						BrushFace bf = addFace(s);
						bf.texture = map.addTexture(bf.texture);
					}
				}
				
				if(end.length() > 0 && line.endsWith(end)) {
					//System.out.println(line);
					//buffer = line;
					//line = "";
					if(qType == QMapObjects.Comment) {
						comment = line;
					}
					//System.out.println("Mapthing returning " + line);
					if(WORLDSPAWN.equals(classname)) {
						map.worldspawn = this;
						String wadstr = getField("wad");
						if(wadstr != null) {
							String[] wadarr = wadstr.split(";");
							for(int i = 0; i < wadarr.length; i++) {
								map.wads.add(wadarr[i]);
							}
						}
					}
					return true;
				}
				
				//System.out.println("Lining: " + ch);
			}
		} catch (Exception ex) {
			System.out.println("Error parsing object: " + ex.getMessage());
			if(DEBUG) System.out.println("Mapthing returning with exception");
			return false;
		}
		//return true;
	}
	
	public void addField(String str) {
		EntField ef = new EntField();
		ef.parse(str);
		if(CLASSNAME.equals(ef.name)) {
			classname = ef.value;
		}
		fields.add(ef);
		hasFields = true;
	}
	
	public BrushFace addFace(String str) {
		BrushFace bf = new BrushFace();
		bf.parse(str);
		faces.add(bf);
		return bf;
	}
	
	public String getField(String name) {
		if(fields == null) return null;
		
		for(int i = 0; i < fields.size(); i++) {
			EntField ef = fields.get(i);
			if(ef != null) {
				if(name.equals(ef.name)) {
					return ef.value;
				}
			}
		}
		return null;
	}
	
	public String toString() {
		String retval = ""; 
		if(qType == QMapObjects.Comment) {
			retval += "Comment: " + comment + "\n";
		} else {
			retval += "Thing: " + classname + "\n";
			retval += "Fields: " + fields.size() + "\n";
			for(int i = 0; i < fields.size(); i++) {
				retval += "\t" + fields.get(i) + "\n";
			}
			retval += "Faces: " + faces.size() + "\n";
			for(int i = 0; i < faces.size(); i++) {
				retval += "\t" + faces.get(i) + "\n";
			}
		}
		return retval;
	}	
}

package mmconfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import qmap2.QVector;

public class ConfigFile {
	public final String TAG_MAPNAME = "[mapname]", TAG_OUTNAME = "[outname]", TAG_TEXTURES = "[textures]", TAG_FIELDS = "[fields]", CHAR_COMMENT = "#";
	public final String TAG_FLIPX = "[flipx]", TAG_FLIPY = "[flipy]", TAG_OVERLAY = "[overlay]", TAG_TRANSLATE = "[translate]";
	public final int STATE_NONE = 0, STATE_MAPNAME = 1, STATE_TEXTURES = 2, STATE_FIELDS = 3, STATE_OUTNAME = 4, STATE_TRANSLATE = 5;
	
	public static boolean DEBUG = false;	
	
	public String filename = "";
	public String mapname = "";
	public String outname = "";
	public Vector<TextureReplacement> texture_replacements = new Vector<TextureReplacement>();
	public Vector<FieldReplacement> field_replacements = new Vector<FieldReplacement>();
	public boolean flip_horizontal = false;
	public boolean flip_vertical = false;
	public boolean overlay = false;
	public QVector translate = new QVector();
	
	public String getLine(BufferedReader br) throws IOException {
		while(true) {
			String line = br.readLine();
			if(line == null) return null;
			line = line.trim();
			if(line.startsWith(CHAR_COMMENT)) {
				continue;
			}
			return line;
		}
	}
	
	public void load() {
		load(filename);
	}
	
	public void load(String filename) {
		if(filename != null) {
			this.filename = filename;
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(filename));
				int state = STATE_NONE;
				while(true) {
					String line = getLine(br);
					if(DEBUG)System.out.println("Line: " + line);
					if(line == null) break;
					if(line.startsWith(TAG_MAPNAME)) {
						state = STATE_MAPNAME;
						if(DEBUG)System.out.println("Switching to mapname");
						continue;
					} else if(line.startsWith(TAG_OUTNAME)) {
						state = STATE_OUTNAME;
						if(DEBUG)System.out.println("Switching to outname");
						continue;
					} else if(line.startsWith(TAG_TEXTURES)) {
						state = STATE_TEXTURES;
						if(DEBUG)System.out.println("Switching to textures");
						continue;
					} else if(line.startsWith(TAG_FIELDS)) {
						if(DEBUG)System.out.println("Switching to fields");
						state = STATE_FIELDS;
						continue;
					} else if(line.startsWith(TAG_TRANSLATE)) {
						if(DEBUG)System.out.println("Switching to translate");
						state = STATE_TRANSLATE;
						continue;
					} else if(line.startsWith(TAG_FLIPX)) {
						if(DEBUG)System.out.println("Flip horizontal requested");
						flip_horizontal = true;
						continue;
					} else if(line.startsWith(TAG_FLIPY)) {
						if(DEBUG)System.out.println("Flip vertical requested");
						flip_vertical = true;
						continue;
					} else if(line.startsWith(TAG_OVERLAY)) {
						if(DEBUG)System.out.println("Overlay requested");
						overlay = true;
						continue;
					}
					if(state == STATE_MAPNAME) {
						if(line.length() == 0) continue;
						mapname = line;
						state = STATE_NONE;
						continue;
					} else if(state == STATE_OUTNAME) {
						if(line.length() == 0) continue;
						outname = line;
						state = STATE_NONE;
						continue;
					} else if(state == STATE_TRANSLATE) {
						if(line.length() == 0) continue;
						translate.parse(line);
						state = STATE_NONE;
						continue;
					} else if(state == STATE_TEXTURES) {
						if(line.length() == 0) continue;
						TextureReplacement tr = new TextureReplacement();
						if(!tr.parse(line)) {
							//Read old format
							tr.oldTexture = line;
							tr.newTexture = getLine(br);
						}
						if(tr.valid()) {
							texture_replacements.add(tr);
						}
					} else if(state == STATE_FIELDS) {
						//Note, blank lines are valid data here
						FieldReplacement fr = new FieldReplacement();
						if(!fr.parse(line)) {
							//Read old format
							fr.classname = line;
							fr.fieldname = getLine(br);
							fr.oldValue = getLine(br);
							fr.newValue = getLine(br);
						}
						if(fr.valid()) {
							field_replacements.add(fr);
						}
					}
				}
				br.close();
			} catch (Exception ex) {
				System.out.println("Error reading config file " + filename + ": " + ex.getMessage());
			}
		}
	}
}

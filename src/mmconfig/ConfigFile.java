package mmconfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import qmap2.QVector;

public class ConfigFile {
	public static final String TAG_MAPNAME = "[mapname]", TAG_OUTNAME = "[outname]", TAG_TEXTURES = "[textures]", TAG_FIELDS = "[fields]", CHAR_COMMENT = "#";
	public static final String TAG_FLIPX = "[flipx]", TAG_FLIPY = "[flipy]", TAG_OVERLAY = "[overlay]", TAG_TRANSLATE = "[translate]";
	public static final String TAG_FIELD_QUERY = "[query]", TAG_FIELD_RESULT = "[result]", TAG_DELETE = "[delete]";
	public static final int STATE_NONE = 0, STATE_MAPNAME = 1, STATE_TEXTURES = 2, STATE_FIELDS = 3, STATE_OUTNAME = 4, STATE_TRANSLATE = 5, STATE_FIELD_QUERY = 6, STATE_FIELD_RESULT = 7;
	
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
			if(line.startsWith(CHAR_COMMENT) || line.length() <= 0) {
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
				FieldReplacement fr = null;
				while(true) {
					String line = getLine(br);
					if(DEBUG)System.out.println("Line: " + line);
					if(line == null) break;
					if(state == STATE_FIELD_RESULT && line.startsWith("[") && !line.startsWith(TAG_DELETE)) {
						if(DEBUG)System.out.println("Switching to something else, resetting field_replacement: " + fr.toDebugString());
						if(fr != null && fr.valid()) {
							if(DEBUG)System.out.println("Saving off last one");
							field_replacements.add(fr);
						}
						fr = new FieldReplacement();
					}
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
					} else if(state == STATE_FIELDS || state == STATE_FIELD_RESULT) {
						if(line.startsWith(TAG_FIELD_QUERY)) {
							if(DEBUG)System.out.println("Switching to field query");
							if(fr != null && fr.valid()) {
								if(DEBUG)System.out.println("Saving off last one");
								field_replacements.add(fr);
							}
							fr = new FieldReplacement();
							state = STATE_FIELD_QUERY;
						} else {
							if(state == STATE_FIELD_RESULT) {
								if(line.startsWith(TAG_DELETE)) {
									if(DEBUG)System.out.println("Entity delete requested (from results)");
									fr.delete = true;
								} else {
									FieldResult f = new FieldResult();
									f.parse(line);
									fr.results.add(f);
								}
							}
						}
					} else if(state == STATE_FIELD_QUERY) {
						if(line.startsWith(TAG_FIELD_RESULT)) {
							if(DEBUG)System.out.println("Switching to field result");
							state = STATE_FIELD_RESULT;
							continue;
						} else if(line.startsWith(TAG_DELETE)) {
							if(DEBUG)System.out.println("Entity delete requested");
							fr.delete = true;
							state = STATE_FIELD_RESULT;
						} else {
							FieldCriteria fc = new FieldCriteria();
							fc.parse(line);
							fr.criteria.add(fc);
							if(DEBUG)System.out.println("Adding query criteria: " + fc);
						}
					}
				}
				if(state == STATE_FIELD_RESULT) {
					if(fr != null && fr.valid()) {
						if(DEBUG)System.out.println("Saving off last one at EOL");
						field_replacements.add(fr);
					}
				}
				br.close();
			} catch (Exception ex) {
				System.out.println("Error reading config file " + filename + ": " + ex.getMessage());
			}
		}
	}
	
	public void saveToFile() {
		if(filename != null) {
			saveToFile(filename);
		}
	}
	
	public void saveToFile(String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			if(mapname != null && mapname.length() > 0) {
				bw.write(TAG_MAPNAME);
				bw.newLine();
				bw.write(mapname);
				bw.newLine();
			}
			if(outname != null && outname.length() > 0) {
				bw.write(TAG_OUTNAME);
				bw.newLine();
				bw.write(outname);
				bw.newLine();
			}
			if(flip_horizontal) {
				bw.write(TAG_FLIPX);
				bw.newLine();
			}
			if(flip_vertical) {
				bw.write(TAG_FLIPY);
				bw.newLine();
			}
			if(overlay) {
				bw.write(TAG_OVERLAY);
				bw.newLine();
			}
			if(translate != null && !translate.isZero()) {
				bw.write(TAG_TRANSLATE);
				bw.newLine();
				bw.write(translate.toString());
				bw.newLine();
			}
			if(texture_replacements != null && texture_replacements.size() > 0) {
				bw.write(TAG_TEXTURES);
				bw.newLine();
				for(int i = 0; i < texture_replacements.size(); i++) {
					bw.write(texture_replacements.get(i).toString());
					bw.newLine();
				}
			}
			if(field_replacements != null && field_replacements.size() > 0) {
				bw.write(TAG_FIELDS);
				bw.newLine();
				for(int i = 0; i < field_replacements.size(); i++) {
					FieldReplacement fr = field_replacements.get(i);
					if(!fr.valid()) continue;
					bw.write(TAG_FIELD_QUERY);
					bw.newLine();
					for(int j = 0; j < fr.criteria.size(); j++) {
						bw.write(fr.criteria.get(j).toString());
						bw.newLine();
					}
					if(fr.delete) {
						bw.write(TAG_DELETE);
						bw.newLine();
					} else {
						bw.write(TAG_FIELD_RESULT);
						bw.newLine();
						for(int j = 0; j < fr.results.size(); j++) {
							bw.write(fr.results.get(j).toString());
							bw.newLine();
						}
					}
				}
			}
			bw.flush();
			bw.close();
		} catch (Exception ex) {
			System.out.println("Error writing config to file " + filename + ": " + ex.getMessage());
		}
	}
	
}

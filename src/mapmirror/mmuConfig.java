package mapmirror;

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

public class mmuConfig {
	public static String PREFIX_CFG = "cfg", PREFIX_WSIZE = "size", PREFIX_WLOCATION = "location", DIV = "=", SEP = ",",
			PREFIX_LWIDTH = "listwidth", PREFIX_LWEIGHT = "listweight", PREFIX_TSIZE = "textures_size", 
			PREFIX_FSIZE = "fields_size", PREFIX_QSIZE = "query_size", 
			PREFIX_RSIZE = "results_size";
	
	public String filename = "mmu.conf";
	
	public Vector<String> configFiles = new Vector<String>();
	public Dimension windowSize = new Dimension(600, 400), texturesSize, fieldsSize, querySize, resultsSize;
	public Point windowLocation = new Point(0, 0);
	public int listWidth = 100;
	public double divWeight = 0.5;
	
	public mmuConfig() {
	}
	
	public mmuConfig(String filename) {
		this.filename = filename;
	}
	
	public void load() {
		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader(filename));
			while (true) {
				String line = bf.readLine();
				if(line == null) break;
				int div = -1;
				div = line.indexOf(DIV);
				if(line.startsWith(PREFIX_CFG)) {
					if(div > -1 && div < line.length()) {
						configFiles.add(line.substring(div + 1).trim());
					}
				} else if(line.startsWith(PREFIX_WSIZE)) {
					if(div > -1 && div < line.length()) {
						try {
							String[] s = line.substring(div + 1).trim().split(SEP, -1);
							if(s.length > 1) {
								windowSize.width = Integer.parseInt(s[0].trim());
								windowSize.height = Integer.parseInt(s[1].trim());
							}
						} catch (NumberFormatException nfe) {
							System.out.println("ERROR loading window size '" + line + "' from " + filename);
						}
					}
				} else if(line.startsWith(PREFIX_TSIZE)) {
					if(div > -1 && div < line.length()) {
						try {
							String[] s = line.substring(div + 1).trim().split(SEP, -1);
							if(s.length > 1) {
								if(texturesSize == null) texturesSize = new Dimension();
								texturesSize.width = Integer.parseInt(s[0].trim());
								texturesSize.height = Integer.parseInt(s[1].trim());
							}
						} catch (NumberFormatException nfe) {
							System.out.println("ERROR loading texture window size '" + line + "' from " + filename);
						}
					}
				} else if(line.startsWith(PREFIX_FSIZE)) {
					if(div > -1 && div < line.length()) {
						try {
							String[] s = line.substring(div + 1).trim().split(SEP, -1);
							if(s.length > 1) {
								if(fieldsSize == null) fieldsSize = new Dimension();
								fieldsSize.width = Integer.parseInt(s[0].trim());
								fieldsSize.height = Integer.parseInt(s[1].trim());
							}
						} catch (NumberFormatException nfe) {
							System.out.println("ERROR loading fields window size '" + line + "' from " + filename);
						}
					}
				} else if(line.startsWith(PREFIX_QSIZE)) {
					if(div > -1 && div < line.length()) {
						try {
							String[] s = line.substring(div + 1).trim().split(SEP, -1);
							if(s.length > 1) {
								if(querySize == null) querySize = new Dimension();
								querySize.width = Integer.parseInt(s[0].trim());
								querySize.height = Integer.parseInt(s[1].trim());
							}
						} catch (NumberFormatException nfe) {
							System.out.println("ERROR loading query window size '" + line + "' from " + filename);
						}
					}
				} else if(line.startsWith(PREFIX_RSIZE)) {
					if(div > -1 && div < line.length()) {
						try {
							String[] s = line.substring(div + 1).trim().split(SEP, -1);
							if(s.length > 1) {
								if(resultsSize == null) resultsSize = new Dimension();
								resultsSize.width = Integer.parseInt(s[0].trim());
								resultsSize.height = Integer.parseInt(s[1].trim());
							}
						} catch (NumberFormatException nfe) {
							System.out.println("ERROR loading results window size '" + line + "' from " + filename);
						}
					}
				} else if(line.startsWith(PREFIX_WLOCATION)) {
					if(div > -1 && div < line.length()) {
						try {
							String[] s = line.substring(div + 1).trim().split(SEP, -1);
							if(s.length > 1) {
								windowLocation.x = Integer.parseInt(s[0].trim());
								windowLocation.y = Integer.parseInt(s[1].trim());
							}
						} catch (NumberFormatException nfe) {
							System.out.println("ERROR loading window location '" + line + "' from " + filename);
						}
					}
				} else if(line.startsWith(PREFIX_LWIDTH)) {
					if(div > -1 && div < line.length()) {
						try {
							listWidth = Integer.parseInt(line.substring(div + 1).trim());
						} catch (NumberFormatException nfe) {
							System.out.println("ERROR loading list width '" + line + "' from " + filename);
						}
					}
				} else if(line.startsWith(PREFIX_LWEIGHT)) {
					if(div > -1 && div < line.length()) {
						try {
							divWeight = Double.parseDouble(line.substring(div + 1).trim());
						} catch (NumberFormatException nfe) {
							System.out.println("ERROR loading list divider ratio '" + line + "' from " + filename);
						}
					}
				}
			}
			bf.close();
		} catch (Exception ex) {
			System.out.println("Error reading file " + filename + ": " + ex.getMessage());
		}
	}
	
	public void save() {
		System.out.println("Saving UI Config to " + filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			for(int i = 0; i < configFiles.size(); i++) {
				bw.write(PREFIX_CFG + DIV + configFiles.get(i));
				bw.newLine();
			}
			bw.write(PREFIX_WSIZE + DIV + windowSize.width + SEP + windowSize.height);
			bw.newLine();
			bw.write(PREFIX_WLOCATION + DIV + windowLocation.x + SEP + windowLocation.y);
			bw.newLine();
			bw.write(PREFIX_LWIDTH + DIV + listWidth);
			bw.newLine();
			bw.write(PREFIX_LWEIGHT + DIV + divWeight);
			bw.newLine();
			bw.write(PREFIX_TSIZE + DIV + texturesSize.width + SEP + texturesSize.height);
			bw.newLine();
			bw.write(PREFIX_FSIZE + DIV + fieldsSize.width + SEP + fieldsSize.height);
			bw.newLine();
			bw.write(PREFIX_QSIZE + DIV + querySize.width + SEP + querySize.height);
			bw.newLine();
			bw.write(PREFIX_RSIZE + DIV + resultsSize.width + SEP + resultsSize.height);
			bw.newLine();
			bw.close();
		} catch (Exception ex) {
			System.out.println("Error writing file " + filename + ": " + ex.getMessage());
		}
	}
}

package qmap2;

public class QMap2 {

	public static QMapFile loadMap(String mapname) {
		QMapFile qmf = new QMapFile();
		qmf.loadFromFile(mapname);
		return qmf;
	}
	
	public static void main(String[] args) {
		System.out.println("QMap 2 Starting...");
		String mapname = "groups-01.map";
		QMapFile qmf = loadMap(mapname);
		System.out.println("QMap 2 Loaded " + mapname);
		System.out.println("It has " + qmf.textures.size() + " unique textures:");
		for(int i = 0; i < qmf.textures.size(); i++) {
			System.out.println("\t'" + qmf.textures.get(i) + "'");
		}
		System.out.println("In " + qmf.wads.size() + " wads:");
		for(int i = 0; i < qmf.wads.size(); i++) {
			System.out.println("\t'" + qmf.wads.get(i) + "'");
		}
	}

}

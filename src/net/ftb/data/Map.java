package net.ftb.data;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.ftb.data.events.MapListener;
import net.ftb.gui.LaunchFrame;
import net.ftb.workers.MapLoader;

public class Map {
	private String name, author, version, url, mapname, mcversion;
	private Image logo, image;
	private String[] packs;
	private int size, index;
	
	private final static ArrayList<Map> maps = new ArrayList<Map>();
	
	private static List<MapListener> listeners = new ArrayList<MapListener>();
	
	public static void addListener(MapListener listener) {
		listeners.add(listener);
	}
	
	public static void loadAll() {
		MapLoader loader = new MapLoader();
		loader.start();
	}
	
	public static void addMap(Map map) {
		synchronized (maps) {
			maps.add(map);
		}
		for (MapListener listener : listeners) {
			listener.onMapAdded(map);
		}
	}
	
	public static ArrayList<Map> getMapArray() {
		return maps;
	}
	
	public static Map getMap(int i) {
		return maps.get(i);
	}
	
	public Map(String name, String author, String version, String url, String logo, String image, String compatible, String mcversion, String mapname, int idx) throws NoSuchAlgorithmException, IOException {
		index = idx;
		this.name = name;
		this.author = author;
		this.version = version;
		this.url = url;
		packs = compatible.split(":");
		this.mcversion = mcversion;
		this.mapname = mapname;
		String installPath = Settings.getSettings().getInstallPath();
		// TODO: Figure out how to do version checking on maps.
		File verFile = new File(installPath, "temp" + File.separator + "maps" + File.separator + mapname + File.separator + "version");
		File dir = new File(installPath, "temp" + File.separator + "maps" + File.separator + mapname);
		if(!upToDate(verFile)) {
			URL url_ = new URL(LaunchFrame.getCreeperhostLink(logo));
			this.logo = Toolkit.getDefaultToolkit().createImage(url_);
			BufferedImage tempImg = ImageIO.read(url_);
			ImageIO.write(tempImg, "png", new File(dir, logo));
			tempImg.flush();
			url_ = new URL(LaunchFrame.getCreeperhostLink(url));
			URLConnection c = url_.openConnection();
			this.size = c.getContentLength();
			url_ =  new URL(LaunchFrame.getCreeperhostLink(image));
			this.image = Toolkit.getDefaultToolkit().createImage(url_);
			tempImg = ImageIO.read(url_);
			ImageIO.write(tempImg, "png", new File(dir, image));
			tempImg.flush();
		} else {
			this.logo = Toolkit.getDefaultToolkit().createImage(dir.getPath() + File.separator + logo);
			URL url_ = new URL(LaunchFrame.getCreeperhostLink(url));
			this.size = url_.openConnection().getContentLength();
			this.image = Toolkit.getDefaultToolkit().createImage(dir.getPath() + File.separator + image);
		}
	}
	
	private boolean upToDate(File verFile) {
		boolean result = false;
		try {
			if(!verFile.exists()) {
				new File(Settings.getSettings().getInstallPath(), "temp" + File.separator + "maps" + File.separator + mapname + File.separator).mkdirs();
				verFile.createNewFile();
				result = false;
			}
			BufferedReader in = new BufferedReader(new FileReader(verFile));
			String line;
			if((line = in.readLine()) == null || Integer.parseInt(version) > Integer.parseInt(line)) {
				BufferedWriter out = new BufferedWriter(new FileWriter(verFile));
				out.write(version);
				out.flush();
				out.close();
				result = false;
			}
			in.close();
		} catch (IOException e) { e.printStackTrace(); }
		return result;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Image getLogo() {
		return logo;
	}
	
	public Image getImage() {
		return image;
	}
	
	public String[] getCompatible() {
		return packs;
	}
	
	public String getMcVersion() {
		return mcversion;
	}
	
	public String getMapName() {
		return mapname;
	}
}

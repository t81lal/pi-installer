package org.nullbool.pi.installer;

import static org.nullbool.pi.installer.IOHelper.checkmkdir;
import static org.nullbool.pi.installer.IOHelper.checkmkfile;
import static org.nullbool.pi.installer.IOHelper.safe_makeURL;

import java.io.File;
import java.net.URL;

public abstract interface ResourceConstants {

	public static final String JSON_EXTENSION = ".json";
	public static final String CONFIG_EXTENSION = JSON_EXTENSION;
	public static final String LOG_EXTENSION = ".ser";

	public static final String RUNESCAPE_URL = "http://oldschool%d.runescape.com/";
	public static final String REMOTE_GAMEPACK_NAME = "gamepack.jar";

	public static final URL SITE_URL = safe_makeURL("http://107.150.29.22/");
	public static final URL BASE_URL = safe_makeURL(SITE_URL, "programs/pi/");
	public static final URL BLOBS_URL = safe_makeURL(BASE_URL, "blobs");
	public static final URL GAME_DATA_URL = safe_makeURL(BASE_URL, "game/");
	public static final String VERSION_URL = GAME_DATA_URL.toExternalForm() + "%d";
	public static final String LOG_URL = GAME_DATA_URL.toExternalForm() + "%d/log.ser";
	public static final String API_URL = GAME_DATA_URL.toExternalForm() + "%d/api.jar";
	public static final String MAPPING_URL = GAME_DATA_URL.toExternalForm() + "%d/translate.json";
	
	public static URL baseURL(int version) {
		return safe_makeURL(String.format(VERSION_URL, version));
	}
	
	public static URL logURL(int version) {
		return safe_makeURL(String.format(LOG_URL, version));
	}
	
	public static URL apiURL(int version) {
		return safe_makeURL(String.format(API_URL, version));
	}
	
	public static URL mappingURL(int version) {
		return safe_makeURL(String.format(MAPPING_URL, version));
	}
	
	public static final File LOCAL_BASE_DIR   = checkmkdir(new File((new File(System.getProperty("user.home")).exists() ? System.getProperty("user.home"): "/root") + "/pi rs"));
	public static final File DATA_DIR         = checkmkdir(new File(LOCAL_BASE_DIR, "data"));
	public static final File OSGI_DATA_DIR    = checkmkdir(new File(DATA_DIR, "osgi"));
	public static final File SCRIPTS_DIR      = checkmkdir(new File(DATA_DIR, "scripts"));
	public static final File TASKS_DIR        = checkmkdir(new File(DATA_DIR, "tasks"));
	public static final File CACHE_DIR        = checkmkdir(new File(DATA_DIR, "game"));
	public static final File API_PROVIDER_DIR = checkmkdir(new File(DATA_DIR, "gameapi"));

	public static final File CLIENT_CONFIG = checkmkfile(new File(LOCAL_BASE_DIR, "config.json"));

	public static void bootstrap() {
		// static initialisation of the class
	}

	public static File getLocalDir(File base, String ver) {
		return new File(base, ver);
	}
}
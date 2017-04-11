package de.spiritaner.maz.util;

import de.spiritaner.maz.DatabaseApp;
import javafx.beans.NamedArg;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Settings {

	final static Logger logger = Logger.getLogger(DatabaseApp.class);
	final private static HashMap<String, String> kvStore = new HashMap<>();
	private static boolean loaded = false;

	private Settings() {

	}

	private static void load() {
		if(!loaded && new File("./settings.txt").exists()) {
			loaded = true;

			try {
				for(String line : FileUtils.readLines(new File("./settings.txt"), "UTF-8")) {
					String[] split = line.split("=");

					if(split.length == 2) {
						set(split[0],split[1]);
					}
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	public static String get(@NamedArg("key") final String key) {
		synchronized (kvStore) {
			load();
			return kvStore.get(key);
		}
	}

	public static String get(@NamedArg("key") final String key, @NamedArg("defaultValue") final String defaultValue) {
		synchronized (kvStore) {
			load();
			return (kvStore.containsKey(key)) ? get(key) : defaultValue;
		}
	}

	public static void set(@NamedArg("key") final String key, @NamedArg("value") final String value) {
		synchronized (kvStore) {
			load();
			kvStore.put(key, value);
		}
	}
}

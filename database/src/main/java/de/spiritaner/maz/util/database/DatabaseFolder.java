package de.spiritaner.maz.util.database;

import de.spiritaner.maz.util.Settings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DatabaseFolder extends File {

	final DateTimeFormatter fromFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
	final DateTimeFormatter toFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");

	private StringProperty version;
	private StringProperty displayName;
	private BooleanProperty outdated;
	private BooleanProperty locked;
	private BooleanProperty isMainDir;

	public DatabaseFolder(String pathname) {
		super(pathname);
		init();
	}

	public DatabaseFolder(String parent, String child) {
		super(parent, child);
		init();
	}

	public DatabaseFolder(File parent, String child) {
		super(parent, child);
		init();
	}

	public DatabaseFolder(URI uri) {
		super(uri);
		init();
	}

	public DatabaseFolder(File file) {
		super(file.getAbsolutePath());
		init();
	}

	private void init() {
		version = new SimpleStringProperty("");
		outdated = new SimpleBooleanProperty(Boolean.TRUE);
		locked = new SimpleBooleanProperty(Boolean.TRUE);
		displayName = new SimpleStringProperty("");
		isMainDir = new SimpleBooleanProperty(Boolean.FALSE);

		final File[] versionFiles = this.listFiles((current, name) -> name.endsWith(".version"));
		version.set((versionFiles.length > 0) ? versionFiles[0].getName().replace(".version", "") : "");
		outdated.set(!new File(this, guiText.getString("version") + ".version").exists());
		locked.set(new File(this, "db.lock").exists());

		String postFix = (isLocked()) ? " (Lesezugriff)" : "";

		if (isOutdated()) {
			postFix += (getVersion().isEmpty()) ? " (alte Version)" : " (Version: " + getVersion() + ")";
		}

		if (getName().equals("dbfiles")) {
			displayName.set(guiText.getString("current") + postFix);
			isMainDir.set(true);
		} else if (getName().contains("-")) {
			String time = getName().substring(getName().indexOf("-") + 1);
			displayName.set(guiText.getString("backup") + " (" + toFormatter.format(fromFormatter.parse(time)) + ")" + postFix);
		} else {
			displayName.set(getName() + postFix);
		}
	}

	public String getVersion() {
		return version.get();
	}

	public void setVersion(String version) {
		this.version.set(version);
	}

	public StringProperty versionProperty() {
		return version;
	}

	public boolean isOutdated() {
		return outdated.get();
	}

	public void setOutdated(boolean outdated) {
		this.outdated.set(outdated);
	}

	public BooleanProperty outdatedProperty() {
		return outdated;
	}

	public boolean isLocked() {
		return locked.get();
	}

	public void setLocked(boolean locked) {
		this.locked.set(locked);
	}

	public BooleanProperty lockedProperty() {
		return locked;
	}

	public String getDisplayName() {
		return displayName.get();
	}

	public void setDisplayName(String displayName) {
		this.displayName.set(displayName);
	}

	public StringProperty displayNameProperty() {
		return displayName;
	}

	public boolean isMainDir() {
		return isMainDir.get();
	}

	public BooleanProperty isMainDirProperty() {
		return isMainDir;
	}

	public void setIsMainDir(boolean isMainDir) {
		this.isMainDir.set(isMainDir);
	}
}

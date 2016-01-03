package ch.danielsuter.subtitledownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public class SettingsFactory {
	private static final String PASSWORD_PROPERTY = "password";
	private static final String USER_PROPERTY = "user";
	private static final String PATH = "path";
	private static final String REPLACE_EXISTING = "replaceExisting";
	private static final String CHECK_FILES_NEWER_THAN = "checkFilesNewerThan";

	public static Settings parse(File file) {
		Properties settingsProperties = readProperties(file);

		Long timestamp = Long.parseLong(settingsProperties.getProperty(CHECK_FILES_NEWER_THAN));
		boolean replaceExisting = Boolean.parseBoolean(settingsProperties.getProperty(REPLACE_EXISTING));
		String user = settingsProperties.getProperty(USER_PROPERTY);
		String password = settingsProperties.getProperty(PASSWORD_PROPERTY);
		String excludesCommaSeparated = settingsProperties.getProperty("excludes");
		Set<String> excludes = new LinkedHashSet<String>(Arrays.asList(excludesCommaSeparated.split(";")));
		return new Settings(excludes, settingsProperties.getProperty(PATH), replaceExisting, timestamp, user, password);
	}

	public static void writeTimestamp(File settingsFile) {
		Properties properties = readProperties(settingsFile);
		properties.setProperty(CHECK_FILES_NEWER_THAN, "" + System.currentTimeMillis());
		writeProperties(properties, settingsFile);
	}

	private static Properties readProperties(File settingsFile) {
		Properties settingsProperties = new Properties();
		try (FileInputStream inputStream = new FileInputStream(settingsFile)) {
			settingsProperties.load(inputStream);
			return settingsProperties;
		} catch (IOException e) {
			throw new RuntimeException("Invalid settings file", e);
		}
	}

	private static void writeProperties(Properties settings, File settingsFile) {
		try (FileOutputStream out = new FileOutputStream(settingsFile)) {
			settings.store(out, "");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

package com.emc.test.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.test.common.Consts;

public class ConfigurationUtils {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConfigurationUtils.class);

	private static final String PROPERTIES_FILENAME = "application.properties";

	private static final Properties configProperties;

	static {
		LOGGER.info("Found the properties {}", PROPERTIES_FILENAME);
		configProperties = getProperties(PROPERTIES_FILENAME);
	}

	public static String generateUUID() {
		String str = UUID.randomUUID().toString();
		return str.substring(str.length() - Consts.UUID_NUM_CHARS);
	}

	public static Properties getProperties(String name) {
		Properties properties = null;

		InputStream is = ConfigurationUtils.class.getClassLoader()
				.getResourceAsStream(name);
		if (is != null) {
			try {
				properties = new Properties();
				properties.load(is);
			} catch (IOException | IllegalArgumentException e) {
				LOGGER.error("properties {} cannot be loaded.", e);
			} finally {
				IOUtils.closeQuietly(is);
			}
		} else {
			LOGGER.warn("properties {} cannot be found.");
		}

		return properties;
	}

	/**
	 * Retrieve the property value by key.
	 * 
	 * @param key
	 *            the property key
	 * @return the value or {@code null} if the key cannot be found.
	 * @see Properties#getProperty(String)
	 */
	public static String getValue(String key) {
		return configProperties.getProperty(key);
	}

	/**
	 * Retrieve the property value by key.
	 * 
	 * @param key
	 *            the property key
	 * @return the value or the default value specified if the key cannot be
	 *         found.
	 * @see Properties#getProperty(String, String)
	 */
	public static String getValue(String key, String defaultVaule) {
		return configProperties.getProperty(key, defaultVaule);
	}

	/**
	 * Splits this string around matches of the given regular expression.
	 * 
	 * @param key
	 *            the property key
	 * @param regex
	 *            the regular expression
	 * @return the split values
	 */
	public static List<String> splitValue(String key, String regex) {
		List<String> results = new ArrayList<String>();

		String value = ConfigurationUtils.getValue(key);
		if (value != null && !value.trim().isEmpty()) {
			String[] vArray = value.split(regex);
			for (String subValue : vArray) {
				results.add(subValue.trim());
			}
		}

		return results;
	}

	public static long getExecutionTimeout() {
		String timeout = ConfigurationUtils
				.getValue(Consts.PROPERTIES_KEY_TASK_EXECUTION_TIMEOUT_DEFAULT);
		try {
			return Long.valueOf(timeout);
		} catch (NumberFormatException e) {
			LOGGER.warn(
					"Failed  to cast timeout to long. Using default timeout 0.",
					e);
			return 0;
		}
	}
}

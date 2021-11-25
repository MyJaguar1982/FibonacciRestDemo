package com.emc.test.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JSONReadWriteHelper {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(JSONReadWriteHelper.class);

	/**
	 * De-serialize the JSON to an array of objects.
	 * 
	 * @param <T>
	 *            the object type
	 * @param is
	 *            the input stream contains JSON content
	 * @param type
	 *            the class for the object type
	 * @return
	 */
	public static <T> List<T> deSerializeJSONCollection(InputStream is,
			Class<T> type) {
		if (is == null) {
			return null;
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
				true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		List<T> result = null;
		try {
			try (final InputStream content = is) {
				result = mapper.readValue(
						content,
						TypeFactory.defaultInstance().constructCollectionType(
								List.class, type));
			}
		} catch (IOException e) {
			LOGGER.error("Failed to de-serialize the stream to type {}.", type,
					e);
		}
		return result;
	}

	/**
	 * De-serialize the JSON according to the specified type.
	 *
	 * @param <T>
	 *            the object type
	 * @param is
	 *            the JSON content
	 * @param type
	 *            the class of the type
	 * @return the instance of the specified type or {@code null} if
	 *         de-serialize failed
	 */
	public static <T> T deSerializeJSON(InputStream is, Class<T> type) {
		if (is == null) {
			return null;
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
				true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		T result = null;
		try {
			try (final InputStream content = is) {
				result = mapper.readValue(content, type);
			}
		} catch (IOException e) {
			LOGGER.error("Failed to de-serialize the stream to type {}.", type,
					e);
		}
		return result;
	}

	/**
	 * Serialize the the specified Object to JSON.
	 * <p>
	 * In case the serialize fails, it returns null.
	 *
	 * @param obj
	 *            the object to be serialized
	 * @return the JSON string or {@code null} if failed to serialize
	 */
	public static String serializeToJSON(Object obj) {
		if (obj == null) {
			return null;
		}

		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(obj);
		} catch (IOException e) {
			LOGGER.error("Failed to serialize the object to JSON.", e);
		}
		return json;
	}
}

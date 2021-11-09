/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workoss.boot.util.web;

import com.workoss.boot.util.Assert;
import com.workoss.boot.util.LinkedMultiValueMap;
import com.workoss.boot.util.MultiValueMap;
import com.workoss.boot.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * mediatype factory
 *
 * @author workoss
 */
public class MediaTypeFactory {
	private static final String MIME_TYPES_FILE_NAME = "/com/workoss/boot/util/web/mime.types";
	private static final MultiValueMap<String, String> fileExtensionToMediaTypes = parseMimeTypes();

	private MediaTypeFactory() {
	}

	private static MultiValueMap<String, String> parseMimeTypes() {
		InputStream is = MediaTypeFactory.class.getResourceAsStream(MIME_TYPES_FILE_NAME);
		Assert.state(is != null, MIME_TYPES_FILE_NAME + " not found in classpath");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII))) {
			MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#') {
					continue;
				}
				String[] tokens = StringUtils.tokenizeToStringArray(line, " \t\n\r\f", true, true);
				String mediaType = tokens[0];
				for (int i = 1; i < tokens.length; i++) {
					String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
					result.add(fileExtension, mediaType);
				}
			}
			return result;
		} catch (IOException ex) {
			throw new IllegalStateException("Could not read " + MIME_TYPES_FILE_NAME, ex);
		}
	}

	public static String getMediaType(String filename, String defaultValue) {
		if (StringUtils.isBlank(filename)) {
			return defaultValue;
		}
		String mediaType = getMediaType(filename);
		return mediaType != null ? mediaType : defaultValue;
	}

	public static String getMediaType(String filename) {
		List<String> mediaTypes = getMediaTypes(filename);
		return mediaTypes.size() > 0 ? mediaTypes.get(0) : null;
	}

	public static List<String> getMediaTypes(String filename) {
		List<String> mediaTypes = null;
		String ext = StringUtils.getFilenameExtension(filename);
		if (ext != null) {
			mediaTypes = fileExtensionToMediaTypes.get(ext.toLowerCase(Locale.ENGLISH));
		}
		return (mediaTypes != null ? mediaTypes : Collections.emptyList());
	}
}

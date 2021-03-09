package com.workoss.boot.storage.util;

import org.springframework.http.MediaTypeFactory;

public final class MimeTypeUtil {

	public static String getMediaType(String key) {
		return MediaTypeFactory.getMediaType(key).map(mediaType -> mediaType.toString()).orElse(null);
	}

}

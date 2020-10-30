package com.workoss.boot.http.util;

import com.workoss.boot.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpToolUtil {

	private static final Charset UTF_32BE = Charset.forName("UTF-32BE");

	private static final Charset UTF_32LE = Charset.forName("UTF-32LE");

	private static final byte ZERO = (byte) 0x00;

	private static final byte BB = (byte) 0xBB;

	private static final byte BF = (byte) 0xBF;

	private static final byte EF = (byte) 0xEF;

	private static final byte FE = (byte) 0xFE;

	private static final byte FF = (byte) 0xFF;

	private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([\\S]+)\\b", Pattern.CASE_INSENSITIVE);

	private HttpToolUtil() {

	}

	public static String bomAwareToString(byte[] bytes, String contentType) {
		if (bytes == null) {
			return null;
		}

		if (bytes.length >= 3 && bytes[0] == EF && bytes[1] == BB && bytes[2] == BF) {
			return new String(bytes, 3, bytes.length - 3, StandardCharsets.UTF_8);
		}
		else if (bytes.length >= 4 && bytes[0] == ZERO && bytes[1] == ZERO && bytes[2] == FE && bytes[3] == FF) {
			return new String(bytes, 4, bytes.length - 4, UTF_32BE);
		}
		else if (bytes.length >= 4 && bytes[0] == FF && bytes[1] == FE && bytes[2] == ZERO && bytes[3] == ZERO) {
			return new String(bytes, 4, bytes.length - 4, UTF_32LE);
		}
		else if (bytes.length >= 2 && bytes[0] == FE && bytes[1] == FF) {
			return new String(bytes, 2, bytes.length - 2, StandardCharsets.UTF_16BE);
		}
		else if (bytes.length >= 2 && bytes[0] == FF && bytes[1] == FE) {
			return new String(bytes, 2, bytes.length - 2, StandardCharsets.UTF_16LE);
		}
		else {
			/*
			 * Attempt to retrieve the default charset from the 'Content-Encoding' header,
			 * if the value isn't present or invalid fallback to 'UTF-8' for the default
			 * charset.
			 */
			if (!StringUtils.isBlank(contentType)) {
				try {
					Matcher charsetMatcher = CHARSET_PATTERN.matcher(contentType);
					if (charsetMatcher.find()) {
						return new String(bytes, Charset.forName(charsetMatcher.group(1)));
					}
					else {
						return new String(bytes, StandardCharsets.UTF_8);
					}
				}
				catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
					return new String(bytes, StandardCharsets.UTF_8);
				}
			}
			else {
				return new String(bytes, StandardCharsets.UTF_8);
			}
		}
	}

}

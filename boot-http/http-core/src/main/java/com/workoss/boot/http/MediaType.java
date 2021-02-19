package com.workoss.boot.http;

import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mediaType
 *
 * @author workoss
 */
public final class MediaType {

	/**
	 * Public constant media type that includes all media ranges (i.e. "&#42;/&#42;").
	 */
	public static final MediaType ALL;

	/**
	 * A String equivalent of {@link MediaType#ALL}.
	 */
	public static final String ALL_VALUE = "*/*";

	/**
	 * Public constant media type for {@code application/atom+xml}.
	 */
	public static final MediaType APPLICATION_ATOM_XML;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_ATOM_XML}.
	 */
	public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";

	/**
	 * Public constant media type for {@code application/cbor}.
	 */
	public static final MediaType APPLICATION_CBOR;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_CBOR}.
	 */
	public static final String APPLICATION_CBOR_VALUE = "application/cbor";

	/**
	 * Public constant media type for {@code application/x-www-form-urlencoded}.
	 */
	public static final MediaType APPLICATION_FORM_URLENCODED;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_FORM_URLENCODED}.
	 */
	public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

	/**
	 * Public constant media type for {@code application/json}.
	 */
	public static final MediaType APPLICATION_JSON;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_JSON}.
	 */
	public static final String APPLICATION_JSON_VALUE = "application/json";

	/**
	 * Public constant media type for {@code application/octet-stream}.
	 */
	public static final MediaType APPLICATION_OCTET_STREAM;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_OCTET_STREAM}.
	 */
	public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

	/**
	 * Public constant media type for {@code application/pdf}.
	 */
	public static final MediaType APPLICATION_PDF;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_PDF}.
	 */
	public static final String APPLICATION_PDF_VALUE = "application/pdf";

	/**
	 * Public constant media type for {@code application/problem+json}.
	 *
	 * @see <a href="https://tools.ietf.org/html/rfc7807#section-6.1"> Problem Details for
	 * HTTP APIs, 6.1. application/problem+json</a>
	 */
	public static final MediaType APPLICATION_PROBLEM_JSON;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_PROBLEM_JSON}.
	 */
	public static final String APPLICATION_PROBLEM_JSON_VALUE = "application/problem+json";

	/**
	 * Public constant media type for {@code application/problem+xml}.
	 *
	 * @see <a href="https://tools.ietf.org/html/rfc7807#section-6.2"> Problem Details for
	 * HTTP APIs, 6.2. application/problem+xml</a>
	 */
	public static final MediaType APPLICATION_PROBLEM_XML;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_PROBLEM_XML}.
	 */
	public static final String APPLICATION_PROBLEM_XML_VALUE = "application/problem+xml";

	/**
	 * Public constant media type for {@code application/rss+xml}.
	 */
	public static final MediaType APPLICATION_RSS_XML;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_RSS_XML}.
	 */
	public static final String APPLICATION_RSS_XML_VALUE = "application/rss+xml";

	/**
	 * Public constant media type for {@code application/stream+json}.
	 */
	public static final MediaType APPLICATION_STREAM_JSON;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_STREAM_JSON}.
	 */
	public static final String APPLICATION_STREAM_JSON_VALUE = "application/stream+json";

	/**
	 * Public constant media type for {@code application/xhtml+xml}.
	 */
	public static final MediaType APPLICATION_XHTML_XML;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_XHTML_XML}.
	 */
	public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";

	/**
	 * Public constant media type for {@code application/xml}.
	 */
	public static final MediaType APPLICATION_XML;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_XML}.
	 */
	public static final String APPLICATION_XML_VALUE = "application/xml";

	/**
	 * Public constant media type for {@code image/gif}.
	 */
	public static final MediaType IMAGE_GIF;

	/**
	 * A String equivalent of {@link MediaType#IMAGE_GIF}.
	 */
	public static final String IMAGE_GIF_VALUE = "image/gif";

	/**
	 * Public constant media type for {@code image/jpeg}.
	 */
	public static final MediaType IMAGE_JPEG;

	/**
	 * A String equivalent of {@link MediaType#IMAGE_JPEG}.
	 */
	public static final String IMAGE_JPEG_VALUE = "image/jpeg";

	/**
	 * Public constant media type for {@code image/png}.
	 */
	public static final MediaType IMAGE_PNG;

	/**
	 * A String equivalent of {@link MediaType#IMAGE_PNG}.
	 */
	public static final String IMAGE_PNG_VALUE = "image/png";

	/**
	 * Public constant media type for {@code multipart/form-data}.
	 */
	public static final MediaType MULTIPART_FORM_DATA;

	/**
	 * A String equivalent of {@link MediaType#MULTIPART_FORM_DATA}.
	 */
	public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

	/**
	 * Public constant media type for {@code multipart/mixed}.
	 */
	public static final MediaType MULTIPART_MIXED;

	/**
	 * A String equivalent of {@link MediaType#MULTIPART_MIXED}.
	 */
	public static final String MULTIPART_MIXED_VALUE = "multipart/mixed";

	/**
	 * Public constant media type for {@code multipart/related}.
	 */
	public static final MediaType MULTIPART_RELATED;

	/**
	 * A String equivalent of {@link MediaType#MULTIPART_RELATED}.
	 */
	public static final String MULTIPART_RELATED_VALUE = "multipart/related";

	/**
	 * Public constant media type for {@code text/event-stream}.
	 *
	 * @see <a href="https://www.w3.org/TR/eventsource/">Server-Sent Events W3C
	 * recommendation</a>
	 */
	public static final MediaType TEXT_EVENT_STREAM;

	/**
	 * A String equivalent of {@link MediaType#TEXT_EVENT_STREAM}.
	 */
	public static final String TEXT_EVENT_STREAM_VALUE = "text/event-stream";

	/**
	 * Public constant media type for {@code text/html}.
	 */
	public static final MediaType TEXT_HTML;

	/**
	 * A String equivalent of {@link MediaType#TEXT_HTML}.
	 */
	public static final String TEXT_HTML_VALUE = "text/html";

	/**
	 * Public constant media type for {@code text/markdown}.
	 */
	public static final MediaType TEXT_MARKDOWN;

	/**
	 * A String equivalent of {@link MediaType#TEXT_MARKDOWN}.
	 */
	public static final String TEXT_MARKDOWN_VALUE = "text/markdown";

	/**
	 * Public constant media type for {@code text/plain}.
	 */
	public static final MediaType TEXT_PLAIN;

	/**
	 * A String equivalent of {@link MediaType#TEXT_PLAIN}.
	 */
	public static final String TEXT_PLAIN_VALUE = "text/plain";

	/**
	 * Public constant media type for {@code text/xml}.
	 */
	public static final MediaType TEXT_XML;

	/**
	 * A String equivalent of {@link MediaType#TEXT_XML}.
	 */
	public static final String TEXT_XML_VALUE = "text/xml";

	private static final String PARAM_QUALITY_FACTOR = "q";

	private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";

	private static final String QUOTED = "\"([^\"]*)\"";

	private static final Pattern TYPE_SUBTYPE = Pattern.compile(TOKEN + "/" + TOKEN);

	private static final Pattern PARAMETER = Pattern
			.compile(";\\s*(?:" + TOKEN + "=(?:" + TOKEN + "|" + QUOTED + "))?");

	private final String type;

	private final String subtype;

	private String charset;

	static {
		// Not using "valueOf' to avoid static init cost
		ALL = new MediaType("*", "*");
		APPLICATION_ATOM_XML = new MediaType("application", "atom+xml");
		APPLICATION_CBOR = new MediaType("application", "cbor");
		APPLICATION_FORM_URLENCODED = new MediaType("application", "x-www-form-urlencoded");
		APPLICATION_JSON = new MediaType("application", "json");
		APPLICATION_OCTET_STREAM = new MediaType("application", "octet-stream");
		APPLICATION_PDF = new MediaType("application", "pdf");
		APPLICATION_PROBLEM_JSON = new MediaType("application", "problem+json");
		APPLICATION_PROBLEM_XML = new MediaType("application", "problem+xml");
		APPLICATION_RSS_XML = new MediaType("application", "rss+xml");
		APPLICATION_STREAM_JSON = new MediaType("application", "stream+json");
		APPLICATION_XHTML_XML = new MediaType("application", "xhtml+xml");
		APPLICATION_XML = new MediaType("application", "xml");
		IMAGE_GIF = new MediaType("image", "gif");
		IMAGE_JPEG = new MediaType("image", "jpeg");
		IMAGE_PNG = new MediaType("image", "png");
		MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");
		MULTIPART_MIXED = new MediaType("multipart", "mixed");
		MULTIPART_RELATED = new MediaType("multipart", "related");
		TEXT_EVENT_STREAM = new MediaType("text", "event-stream");
		TEXT_HTML = new MediaType("text", "html");
		TEXT_MARKDOWN = new MediaType("text", "markdown");
		TEXT_PLAIN = new MediaType("text", "plain");
		TEXT_XML = new MediaType("text", "xml");
	}

	private MediaType(String type, String subtype) {
		this(type, subtype, null);
	}

	private MediaType(String type, String subtype, String charset) {
		Assert.hasLength(type, "'type' must not be empty");
		Assert.hasLength(subtype, "'subtype' must not be empty");
		this.type = type.toLowerCase(Locale.ENGLISH);
		this.subtype = (subtype == null ? "*" : subtype).toLowerCase(Locale.ENGLISH);
		if (charset != null) {
			this.charset = charset.toLowerCase(Locale.ENGLISH);
		}
	}

	/**
	 * Returns a media type for {@code string}.
	 * @throws IllegalArgumentException if {@code string} is not a well-formed media type.
	 */
	public static MediaType get(String string) {
		Matcher typeSubtype = TYPE_SUBTYPE.matcher(string);
		if (!typeSubtype.lookingAt()) {
			throw new IllegalArgumentException("No subtype found for: \"" + string + '"');
		}
		String type = typeSubtype.group(1).toLowerCase(Locale.US);
		String subtype = typeSubtype.group(2).toLowerCase(Locale.US);

		String charset = null;
		Matcher parameter = PARAMETER.matcher(string);
		for (int s = typeSubtype.end(); s < string.length(); s = parameter.end()) {
			parameter.region(s, string.length());
			if (!parameter.lookingAt()) {
				throw new IllegalArgumentException(
						"Parameter is not formatted correctly: \"" + string.substring(s) + "\" for: \"" + string + '"');
			}

			String name = parameter.group(1);
			if (name == null || !name.equalsIgnoreCase("charset"))
				continue;
			String charsetParameter;
			String token = parameter.group(2);
			if (token != null) {
				// If the token is 'single-quoted' it's invalid! But we're lenient and
				// strip the quotes.
				charsetParameter = (token.startsWith("'") && token.endsWith("'") && token.length() > 2)
						? token.substring(1, token.length() - 1) : token;
			}
			else {
				// Value is "double-quoted". That's valid and our regex group already
				// strips the quotes.
				charsetParameter = parameter.group(3);
			}
			if (charset != null && !charsetParameter.equalsIgnoreCase(charset)) {
				throw new IllegalArgumentException("Multiple charsets defined: \"" + charset + "\" and: \""
						+ charsetParameter + "\" for: \"" + string + '"');
			}
			charset = charsetParameter;
		}

		return new MediaType(type, subtype, charset);
	}

	/**
	 * Returns a media type for {@code string}, or null if {@code string} is not a
	 * well-formed media type.
	 */
	public static @Nullable MediaType parse(String string) {
		try {
			return get(string);
		}
		catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	/**
	 * Returns the high-level media type, such as "text", "image", "audio", "video", or
	 * "application".
	 */
	public String type() {
		return type;
	}

	/**
	 * Returns a specific media subtype, such as "plain" or "png", "mpeg", "mp4" or "xml".
	 */
	public String subtype() {
		return subtype;
	}

	/**
	 * Returns the charset of this media type, or null if this media type doesn't specify
	 * a charset.
	 */
	public @Nullable Charset charset() {
		return charset(null);
	}

	/**
	 * Returns the charset of this media type, or {@code defaultValue} if either this
	 * media type doesn't specify a charset, of it its charset is unsupported by the
	 * current runtime.
	 */
	public @Nullable Charset charset(@Nullable Charset defaultValue) {
		try {
			return charset != null ? Charset.forName(charset) : defaultValue;
		}
		catch (IllegalArgumentException e) {
			return defaultValue; // This charset is invalid or unsupported. Give up.
		}
	}

	/**
	 * Returns the encoded media type, like "text/plain; charset=utf-8", appropriate for
	 * use in a Content-Type header.
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(this.type).append("/").append(this.subtype);
		if (this.charset != null) {
			stringBuilder.append("; charset=").append(this.charset);
		}
		return stringBuilder.toString();
	}

	@Override
	public boolean equals(@Nullable Object other) {
		return other instanceof MediaType && ((MediaType) other).toString().equals(toString());
	}

}

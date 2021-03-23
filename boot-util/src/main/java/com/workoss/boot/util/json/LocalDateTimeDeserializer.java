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
package com.workoss.boot.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.workoss.boot.util.DateUtils;
import com.workoss.boot.util.collection.CollectionUtils;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义jacksonlocalDate格式化
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class LocalDateTimeDeserializer extends com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer {

	private String[] patterns = new String[0];

	public LocalDateTimeDeserializer(String... patterns) {
		super(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_TIME_PATTERN));
		if (CollectionUtils.isNotEmpty(patterns)) {
			this.patterns = patterns;
		}
	}

	@Override
	public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
			return _fromString(parser, context, parser.getText());
		}
		// 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
		if (parser.isExpectedStartObjectToken()) {
			return _fromString(parser, context, context.extractScalarFromObject(parser, this, handledType()));
		}
		if (parser.isExpectedStartArrayToken()) {
			JsonToken t = parser.nextToken();
			if (t == JsonToken.END_ARRAY) {
				return null;
			}
			boolean valueBoolean = (t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)
					&& context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
			if (valueBoolean) {
				final LocalDateTime parsed = deserialize(parser, context);
				if (parser.nextToken() != JsonToken.END_ARRAY) {
					handleMissingEndArrayForSingle(parser, context);
				}
				return parsed;
			}
			if (t == JsonToken.VALUE_NUMBER_INT) {
				LocalDateTime result;

				int year = parser.getIntValue();
				int month = parser.nextIntValue(-1);
				int day = parser.nextIntValue(-1);
				int hour = parser.nextIntValue(-1);
				int minute = parser.nextIntValue(-1);

				t = parser.nextToken();
				if (t == JsonToken.END_ARRAY) {
					result = LocalDateTime.of(year, month, day, hour, minute);
				}
				else {
					int second = parser.getIntValue();
					t = parser.nextToken();
					if (t == JsonToken.END_ARRAY) {
						result = LocalDateTime.of(year, month, day, hour, minute, second);
					}
					else {
						int partialSecond = parser.getIntValue();
						if (partialSecond < 1_000
								&& !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
							partialSecond *= 1_000_000;
						}
						if (parser.nextToken() != JsonToken.END_ARRAY) {
							throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY,
									"Expected array to end");
						}
						result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
					}
				}
				return result;
			}
			context.reportInputMismatch(handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
					t);
		}
		if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
			return (LocalDateTime) parser.getEmbeddedObject();
		}
		if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
			_throwNoNumericTimestampNeedTimeZone(parser, context);
		}
		return _handleUnexpectedToken(context, parser, "Expected array or string.");
	}

	@Override
	protected LocalDateTime _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
		String string = string0.trim();
		if (string.length() == 0) {
			// 22-Oct-2020, tatu: not sure if we should pass original (to distinguish
			// b/w empty and blank); for now don't which will allow blanks to be
			// handled like "regular" empty (same as pre-2.12)
			return _fromEmptyString(p, ctxt, string);
		}
		try {
			return DateUtils.parse(string, patterns);
		}
		catch (DateTimeException e) {
			return _handleDateTimeException(ctxt, e, string);
		}
	}

}

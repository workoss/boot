/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.workoss.boot.util.DateUtils;
import com.workoss.boot.util.collection.CollectionUtils;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public class LocalDateDeserializer extends com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer {

	private String[] patterns = new String[0];

	public LocalDateDeserializer(String... patterns) {
		super(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_PATTERN));
		if (CollectionUtils.isNotEmpty(patterns)) {
			this.patterns = patterns;
		}
	}

	@Override
	public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		LocalDate localDate = null;
		if (parser.hasToken(JsonToken.VALUE_STRING)) {
			localDate = _fromString(parser, context, parser.getText());
		}
		// 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
		if (parser.isExpectedStartObjectToken()) {
			localDate = _fromString(parser, context, context.extractScalarFromObject(parser, this, handledType()));
		}
		return localDate != null ? localDate : super.deserialize(parser, context);
	}

	@Override
	protected LocalDate _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
		String string = string0.trim();
		if (string.length() == 0) {
			// 22-Oct-2020, tatu: not sure if we should pass original (to distinguish
			// b/w empty and blank); for now don't which will allow blanks to be
			// handled like "regular" empty (same as pre-2.12)
			return _fromEmptyString(p, ctxt, string);
		}
		try {
			// as per [datatype-jsr310#37], only check for optional (and, incorrect...)
			// time marker 'T'
			// if we are using default formatter
			return DateUtils.localDateParse(string, patterns);
		}
		catch (DateTimeException e) {
			// ignore
		}
		return null;
	}

}

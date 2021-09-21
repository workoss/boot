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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.workoss.boot.util.DateUtils;
import com.workoss.boot.util.collection.CollectionUtils;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("ALL")
public class LocalTimeDeserializer extends com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer {

	private String[] patterns = new String[0];

	public LocalTimeDeserializer(String... patterns) {
		super(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_TIME_PATTERN));
		if (CollectionUtils.isNotEmpty(patterns)) {
			this.patterns = patterns;
		}
	}

	@Override
	public LocalTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		return super.deserialize(parser, context);
	}

	@Override
	protected LocalTime _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
		String string = string0.trim();
		if (string.length() == 0) {
			// 22-Oct-2020, tatu: not sure if we should pass original (to distinguish
			// b/w empty and blank); for now don't which will allow blanks to be
			// handled like "regular" empty (same as pre-2.12)
			return _fromEmptyString(p, ctxt, string);
		}
		try {
			return DateUtils.localTimeParse(string,patterns);
		}
		catch (DateTimeException e) {
			//ignore
		}
		return null;
	}

}

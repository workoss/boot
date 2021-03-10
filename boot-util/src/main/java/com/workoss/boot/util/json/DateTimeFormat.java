/*
 * Copyright © 2020-2021 workoss (WORKOSS)
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

import com.workoss.boot.util.DateUtils;
import com.workoss.boot.util.collection.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeFormat extends SimpleDateFormat {

	private String[] patterns = new String[0];

	public DateTimeFormat(String... patterns) {
		super("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		if (CollectionUtils.isNotEmpty(patterns)) {
			this.patterns = patterns;
		}
	}

	@Override
	public Date parse(String source) throws ParseException {
		return DateUtils.parseDate(source, patterns);
	}

}

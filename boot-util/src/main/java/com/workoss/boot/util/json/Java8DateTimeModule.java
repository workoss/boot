/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.workoss.boot.util.DateUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author workoss
 */
public class Java8DateTimeModule {

	public Module getModule() {
		LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer();
		LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer();
		LocalTimeDeserializer localTimeDeserializer = new LocalTimeDeserializer();
		return new JavaTimeModule().addDeserializer(LocalDateTime.class, localDateTimeDeserializer)
			.addSerializer(LocalDateTime.class,
					new LocalDateTimeSerializer(DateUtil.getDateTimeFormatter(DateUtil.DEFAULT_DATE_TIME_PATTERN)))
			.addDeserializer(LocalDate.class, localDateDeserializer)
			.addSerializer(LocalDate.class,
					new LocalDateSerializer(DateUtil.getDateTimeFormatter(DateUtil.DEFAULT_DATE_PATTERN)))
			.addDeserializer(LocalTime.class, localTimeDeserializer)
			.addSerializer(LocalTime.class,
					new LocalTimeSerializer(DateUtil.getDateTimeFormatter(DateUtil.DEFAULT_TIME_PATTERN)))
			.addDeserializer(Duration.class, DurationDeserializer.INSTANCE)
			.addSerializer(Duration.class, DurationSerializer.INSTANCE);
	}

}

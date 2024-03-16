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
package com.cedarpolicy;

import com.cedarpolicy.model.slice.Slice;
import com.cedarpolicy.serializer.ValueCedarDeserializer;
import com.cedarpolicy.serializer.ValueCedarSerializer;
import com.cedarpolicy.value.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.workoss.boot.util.Lazy;
import com.workoss.boot.util.json.JsonMapper;

final class CedarJson {

	private static final Lazy<ObjectMapper> OBJECT_MAPPER = Lazy.of(CedarJson::createObjectMapper);

	private CedarJson() {
		throw new IllegalStateException("Utility class");
	}

	public static ObjectWriter objectWriter() {
		return OBJECT_MAPPER.get().writer();
	}

	public static ObjectReader objectReader() {
		return OBJECT_MAPPER.get().reader();
	}

	private static ObjectMapper createObjectMapper() {
		final ObjectMapper mapper = JsonMapper.build().getMapper();

		final SimpleModule module = new SimpleModule();
		module.addSerializer(Slice.class, new SliceJsonSerializer());
		module.addSerializer(Value.class, new ValueCedarSerializer());
		module.addDeserializer(Value.class, new ValueCedarDeserializer());
		mapper.registerModule(module);

		return mapper;
	}

}

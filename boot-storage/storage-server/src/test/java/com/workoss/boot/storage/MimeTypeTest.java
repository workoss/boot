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
package com.workoss.boot.storage;

import com.workoss.boot.util.collection.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import javax.activation.MimetypesFileTypeMap;
import java.util.List;
import java.util.Optional;

public class MimeTypeTest {

	@Test
	public void test02() {
		MediaType mediaType = null;
		Optional<MediaType> optionalS = Optional.ofNullable(mediaType);
		String result = optionalS.map(mediaType1 -> mediaType1.toString()).orElse(null);
		System.out.println(result);
	}

	@Test
	public void test01() {
		System.out.println(getMediaType("11/1.png"));
		System.out.println(getMediaType("11/1.jpg"));
		System.out.println(getMediaType("11/1.gif"));

		System.out.println(getMediaType("11/1.txt"));
		System.out.println(getMediaType("11/1.html"));
		System.out.println(getMediaType("11/1.cfg"));

		System.out.println(getMediaType("11/1.doc"));
		System.out.println(getMediaType("11/1.docx"));
		System.out.println(getMediaType("11/1.ppt"));
		System.out.println(getMediaType("11/1.pptx"));
		System.out.println(getMediaType("11/1.xls"));
		System.out.println(getMediaType("11/1.xlsx"));

		System.out.println("-------------------------------");

		System.out.println(getContentType("11/1.png"));
		System.out.println(getContentType("11/1.jpg"));
		System.out.println(getContentType("11/1.gif"));

		System.out.println(getContentType("11/1.txt"));
		System.out.println(getContentType("11/1.html"));
		System.out.println(getContentType("11/1.cfg"));

		System.out.println(getContentType("11/1.doc"));
		System.out.println(getContentType("11/1.docx"));
		System.out.println(getContentType("11/1.ppt"));
		System.out.println(getContentType("11/1.pptx"));
		System.out.println(getContentType("11/1.xls"));
		System.out.println(getContentType("11/1.xlsx"));
	}

	String getMediaType(String key) {
		List<MediaType> list = MediaTypeFactory.getMediaTypes(key);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0).toString();
	}

	String getContentType(String key) {
		return MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(key);
	}

}

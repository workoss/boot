package com.workoss.boot.storage;

import com.yifengx.popeye.util.collection.CollectionUtils;
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

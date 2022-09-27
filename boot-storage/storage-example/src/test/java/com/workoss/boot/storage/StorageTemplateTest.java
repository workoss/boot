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
package com.workoss.boot.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;

public class StorageTemplateTest extends StorageApplicationTest {

	@Autowired
	private StorageTemplate storageTemplate;

	@Test
	public void testOSS() throws IOException {
//		boolean bucketExist = storageTemplate.client().doesBucketExist();
//		System.out.println("bucket exists:"+bucketExist);
//		List<StorageBucketInfo> list = storageTemplate.client().listBuckets();
//		System.out.println("buckets list:"+JsonMapper.toJSONString(list));

//		boolean objectExist = storageTemplate.client().doesObjectExist("66.txt");
//		System.out.println("object exists:"+objectExist);
//	  StorageFileInfo fileInfo = storageTemplate.client().getObject("66.txt");
//		System.out.println("getObject :"+JsonMapper.toJSONString(fileInfo));
//		StorageFileInfoListing listing = storageTemplate.client().listObjects("/", "/", "3.5 文章的添加和删除.mp4", 1);
//		System.out.println("listObjects:" + JsonMapper.toJSONString(listing));
//		File file = new File("/Users/workoss/upload.md");
//		StorageFileInfo fileInfo =storageTemplate.client().putObject("demo.md",file,null,null);
//		System.out.println("putObjects:"+JsonMapper.toJSONString(fileInfo));
//		File destFile = new File("/Users/workoss/upload_back.md");
//		File file1 = storageTemplate.client().download("demo.md",destFile,null);
//		System.out.println("download:"+file1.getName()+"-"+file1.getPath());

//		byte[] bytes = storageTemplate.client().download("demo.md",null);
//		File destFile = new File("/Users/workoss/upload_back1.md");
//		FileOutputStream fileOutputStream = new FileOutputStream(destFile);
//		fileOutputStream.write(bytes);
//		fileOutputStream.flush();
	}
}

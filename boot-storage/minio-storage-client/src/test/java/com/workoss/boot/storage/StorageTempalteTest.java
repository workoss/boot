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

import com.fasterxml.jackson.databind.JsonNode;
import com.workoss.boot.util.json.JsonMapper;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class StorageTempalteTest {

	// @Autowired
	// private StorageTemplate storageTemplate;

	@Test
	void testMinio() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException,
			NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
		String token = "{\n" + "        \"storageType\": \"OSS\",\n"
				+ "        \"accessKey\": \"STS.NTtMU7nxB1NqskswRcxzTSLwr\",\n"
				+ "        \"secretKey\": \"3qvkGyaGy8UFHdXkpqdKTuDinFJgjaa2ZoycP5jDYqpz\",\n"
				+ "        \"stsToken\": \"CAISnQJ1q6Ft5B2yfSjIr5fBBu+Dg6djhoyacU3Ck1I2dPV4vInckDz2IHpMf3lhAuEbs/kxnGxT7P8alq9rTIdIVEGc6Vb1UQMRo22beIPkl5Gf/N5t0e+5ewW6Dxr8w7WQAYHQR8/cffGAck3NkjQJr5LxaTSlWS7fU+WOkoU1QtkTUxW1SjBECdxKXFwAzvUXLnzML/2gHwf3i27LdipStxF7lHl05Nb0oISV4QGMi0bhmK1H5dbuJYKnaNJ3e5tyVtDyxuVzL+zZ1iFyk0EQr/cm1/UZqW2Z4Y3BXgBrjk/YY7aOwLpGNxRkY6U2IalAocXnmOdw0u6pzN6vkEYcZrAIDnmBHNz7mpKcAoKuLc18abH4NnLdKURI/S5LQmkagAEF+9AUMNJzYkwSicfQsSYWVKx3k0y8O3lWqFwUFdUwfvZrauvIgDq9dBMNL4J0DrTqKLAlCOchOUJdVvZREYTHE8nnRCkkW9B8aaep44HWjpHih/00Bdpwoks0XHfMtz3Qp/piiGY9ANMU8qS9uyZaNJFZsduaF9O6MR/dSLaxWg==\",\n"
				+ "        \"expiration\": \"2021-03-04 10:05:21\",\n"
				+ "        \"endpoint\": \"https://workoss.oss-cn-shenzhen.aliyuncs.com\"\n" + "    }";
		JsonNode jsonNode = JsonMapper.parse(token);
		String ak = jsonNode.get("accessKey").asText();
		String sk = jsonNode.get("secretKey").asText();
		String stsToken = jsonNode.get("stsToken").asText();
		String endpoint = "https://oss-cn-shenzhen.aliyuncs.com";
		// Provider provider = new StaticProvider(ak, sk, stsToken);
		//
		// MinioClient minioClient =
		// MinioClient.builder().endpoint(endpoint).region("oss").credentialsProvider(provider)
		// .build();

		// minioClient.listBuckets()
		// .stream().forEach(bucket -> {
		// System.out.println(bucket.name());
		// });

		// boolean bucketExist =
		// minioClient.bucketExists(BucketExistsArgs.builder().bucket("yf-res-te").build());
		// System.out.println(bucketExist);

		// Iterable<Result<Item>> listObjects =
		// minioClient.listObjects(ListObjectsArgs.builder().bucket("yf-res-te").maxKeys(10).build());
		// for (Result<Item> listObject : listObjects) {
		// System.out.println(listObject.get().objectName()+"--"+listObject.get().size());
		// }

		// GetObjectResponse objectResponse =
		// minioClient.getObject(GetObjectArgs.builder().bucket("yf-res-te").object("index.html").build());
		// System.out.println(objectResponse.headers());

		// File file = new File("/home/workoss/oss_ram.png");
		// InputStream inputStream = new FileInputStream(file);
		// ObjectWriteResponse putResponse
		// =minioClient.putObject(PutObjectArgs.builder().bucket("workoss").contentType("image/png").object("demo/ram1.png").stream(inputStream,
		// file.length(), 0).build());
		// System.out.println(putResponse.headers().toMultimap());

		// minioClient.downloadObject(DownloadObjectArgs.builder().bucket("workoss").object("demo/ram1.png").filename("/home/workoss/down-ram.png").build());

		// minioClient.removeObject(RemoveObjectArgs.builder().bucket("workoss").object("demo/ram1.png").build());

	}

}

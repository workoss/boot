package com.workoss.boot.storage.client;

import com.amazonaws.services.s3.AmazonS3;
import com.workoss.boot.storage.config.StorageClientConfig;
import com.workoss.boot.storage.model.StorageSignature;
import com.workoss.boot.storage.model.StorageStsToken;
import com.workoss.boot.storage.model.StorageType;

/**
 * 华为云对象存储
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class OBSClient extends AbstractS3Client {

	@Override
	public StorageType type() {
		return StorageType.OBS;
	}

	@Override
	protected void initConfig(StorageClientConfig config) {

	}

	@Override
	protected AmazonS3 createClient(StorageClientConfig config, StorageStsToken stsToken) {
		return createS3Client(config, config.getEndpoint(), stsToken);
	}

	@Override
	protected StorageStsToken getStsToken(StorageClientConfig config, String key, String action) {
		return requestSTSToken(config, key, action);
	}

	@Override
	protected StorageSignature generateSignagure(StorageClientConfig config, String key, String mimeType,
												 String successActionStatus) {
		return requestSign(config, key, mimeType, successActionStatus);
	}

}

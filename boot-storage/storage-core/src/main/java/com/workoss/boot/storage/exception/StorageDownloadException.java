package com.workoss.boot.storage.exception;

/**
 * 下载异常
 *
 * @author workoss
 */
public class StorageDownloadException extends StorageException {

	public StorageDownloadException(String s) {
		super(s);
	}

	public StorageDownloadException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public StorageDownloadException(Throwable throwable) {
		super(throwable);
	}

}

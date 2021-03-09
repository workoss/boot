package com.workoss.boot.storage.exception;

/**
 * 存储客户端找不到异常
 *
 * @author workoss
 */
public class StorageClientNotFoundException extends StorageException {

	public StorageClientNotFoundException(String s) {
		super(s);
	}

	public StorageClientNotFoundException(String errcode, String errorMsg) {
		super(errcode, errorMsg);
	}

	public StorageClientNotFoundException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public StorageClientNotFoundException(Throwable throwable) {
		super(throwable);
	}

}

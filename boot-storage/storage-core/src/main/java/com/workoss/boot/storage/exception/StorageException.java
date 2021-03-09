package com.workoss.boot.storage.exception;


import com.workoss.boot.util.exception.BootException;

/**
 * 存储异常
 *
 * @author workoss
 */
public class StorageException extends BootException {

	public StorageException(String errcode) {
		super(errcode);
	}

	public StorageException(String errcode, String errorMsg) {
		super(errcode, errorMsg);
	}

	public StorageException(String errcode, Throwable throwable) {
		super(errcode, throwable);
	}

	public StorageException(Throwable throwable) {
		super(throwable);
	}

}

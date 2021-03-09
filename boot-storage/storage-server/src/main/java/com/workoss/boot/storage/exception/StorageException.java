package com.workoss.boot.storage.exception;

import com.yifengx.popeye.util.exception.PopeyeException;

/**
 * 存储异常
 *
 * @author workoss
 */
public class StorageException extends PopeyeException {

	public StorageException(String errcode) {
		super(errcode);
	}

	public StorageException(String errcode, String errmsg) {
		super(errcode, errmsg);
	}

	public StorageException(String errcode, Throwable throwable) {
		super(errcode, throwable);
	}

}

package com.workoss.boot.storage.model;

/**
 * 进度事件
 *
 * @author workoss
 */
public class StorageProgressEvent {

	private final long bytes;

	private final String eventType;

	private final long bytesTransferred;

	public StorageProgressEvent(long bytes, String eventType, long bytesTransferred) {
		this.bytes = bytes;
		this.eventType = eventType;
		this.bytesTransferred = bytesTransferred;
	}

	public long getBytes() {
		return bytes;
	}

	public String getEventType() {
		return eventType;
	}

	public long getBytesTransferred() {
		return bytesTransferred;
	}
}

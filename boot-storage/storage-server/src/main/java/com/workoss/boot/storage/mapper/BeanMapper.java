package com.workoss.boot.storage.mapper;

public interface BeanMapper<S, T> {

	T toTarget(S source);

	S toSource(T target);

}

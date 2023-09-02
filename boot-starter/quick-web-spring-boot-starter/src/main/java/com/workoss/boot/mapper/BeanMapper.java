/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
package com.workoss.boot.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

/**
 * 基础对象映射
 *
 * @param <S> source
 * @param <T> target
 * @author workoss
 */
public interface BeanMapper<S, T> {

	/**
	 * S -> T
	 * @param source source
	 * @return target
	 */
	T toTarget(S source);

	/**
	 * List<S> -> List<T>
	 * @param sList sourceList
	 * @return targetList
	 */
	@IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
	List<T> toTargetList(List<S> sList);

	/**
	 * T -> S
	 * @param target target
	 * @return source
	 */
	S toSource(T target);

	/**
	 * List<T> -> List<S>
	 * @param tList targetList
	 * @return sourceList
	 */
	@IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
	List<S> toSourceList(List<T> tList);

}

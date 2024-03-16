/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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
package com.workoss.boot.util.concurrent.fast;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于ConcurrentHashMap实现的支持并发的Set（实际上就是使用map的set，原理类比：HashSet和HashMap），所以在mina中该类被称为ConcurrentHashSet
 *
 * @author workoss
 */
public class ConcurrentSet<E> extends AbstractSet<E> implements Serializable {

	private static final long serialVersionUID = -1244664838594578508L;

	/**
	 * 基本数据结构
	 */
	private ConcurrentMap<E, Boolean> map;

	/**
	 * 创建ConcurrentSet实例 并初始化内部的ConcurrentHashMap
	 */
	public ConcurrentSet() {
		map = new ConcurrentHashMap<>();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean add(E e) {
		return map.putIfAbsent(e, Boolean.TRUE) == null;
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o);
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public int size() {
		return map.size();
	}

}

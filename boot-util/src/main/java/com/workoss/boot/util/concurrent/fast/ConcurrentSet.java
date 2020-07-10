/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
 * @param <E>
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

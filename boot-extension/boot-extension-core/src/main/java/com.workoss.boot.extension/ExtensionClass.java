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
package com.workoss.boot.extension;

import com.workoss.boot.util.reflect.ClassUtils;

import java.util.Arrays;

/**
 * 扩展类
 *
 * @author: workoss
 */
public class ExtensionClass<T> implements Sortable {

	/**
	 * 扩展接口实现类名
	 */
	protected final Class<? extends T> clazz;

	/**
	 * 扩展别名,不是provider uniqueId
	 */
	protected final String alias;

	/**
	 * 扩展编码，必须唯一
	 */
	protected byte code;

	/**
	 * 是否单例
	 */
	protected boolean singleton;

	/**
	 * 扩展点排序值，大的优先级高
	 */
	protected int order;

	/**
	 * 是否覆盖其它低{@link #order}的同名扩展
	 */
	protected boolean override;

	/**
	 * 排斥其它扩展，可以排斥掉其它低{@link #order}的扩展
	 */
	protected String[] rejection;

	/**
	 * 服务端实例对象（只在是单例的时候保留）
	 */
	private volatile transient T instance;

	/**
	 * 构造函数
	 * @param clazz 扩展实现类名
	 * @param alias 扩展别名
	 */
	public ExtensionClass(Class<? extends T> clazz, String alias) {
		this.clazz = clazz;
		this.alias = alias;
	}

	/**
	 * 得到服务端实例对象，如果是单例则返回单例对象，如果不是则返回新创建的实例对象
	 * @return 扩展点对象实例
	 */
	public T getExtInstance() {
		return getExtInstance(null, null);
	}

	/**
	 * 得到服务端实例对象，如果是单例则返回单例对象，如果不是则返回新创建的实例对象
	 * @param argTypes 构造函数参数类型
	 * @param args 构造函数参数值
	 * @return 扩展点对象实例 ext instance
	 */
	public T getExtInstance(Class[] argTypes, Object[] args) {
		if (clazz != null) {
			try {
				// 如果是单例
				if (singleton) {
					if (instance == null) {
						synchronized (this) {
							if (instance == null) {
								instance = ClassUtils.newInstanceWithArgs(clazz, argTypes, args);
							}
						}
					}
					// 保留单例
					return instance;
				}
				else {
					return ClassUtils.newInstanceWithArgs(clazz, argTypes, args);
				}
			}
			catch (Exception e) {
				throw new RuntimeException("create " + clazz.getCanonicalName() + " instance error", e);
			}
		}
		throw new RuntimeException("Class of ExtensionClass is null");
	}

	/**
	 * Gets tag.
	 * @return the tag
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Gets code.
	 * @return the code
	 */
	public byte getCode() {
		return code;
	}

	/**
	 * Sets code.
	 * @param code the code
	 * @return the code
	 */
	public ExtensionClass setCode(byte code) {
		this.code = code;
		return this;
	}

	/**
	 * Is singleton boolean.
	 * @return the boolean
	 */
	public boolean isSingleton() {
		return singleton;
	}

	/**
	 * Sets singleton.
	 * @param singleton the singleton
	 */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	/**
	 * Gets clazz.
	 * @return the clazz
	 */
	public Class<? extends T> getClazz() {
		return clazz;
	}

	/**
	 * Gets order.
	 * @return the order
	 */
	@Override
	public int getOrder() {
		return order;
	}

	/**
	 * Sets order.
	 * @param order the order
	 * @return the order
	 */
	public ExtensionClass setOrder(int order) {
		this.order = order;
		return this;
	}

	/**
	 * Is override boolean.
	 * @return the boolean
	 */
	public boolean isOverride() {
		return override;
	}

	/**
	 * Sets override.
	 * @param override the override
	 * @return the override
	 */
	public ExtensionClass setOverride(boolean override) {
		this.override = override;
		return this;
	}

	/**
	 * Get rejection string [ ].
	 * @return the string [ ]
	 */
	public String[] getRejection() {
		return rejection;
	}

	/**
	 * Sets rejection.
	 * @param rejection the rejection
	 * @return the rejection
	 */
	public ExtensionClass setRejection(String[] rejection) {
		this.rejection = rejection;
		return this;
	}

	@Override
	public String toString() {
		return "ExtensionClass{" + "clazz=" + clazz + ", alias='" + alias + '\'' + ", code=" + code + ", singleton="
				+ singleton + ", order=" + order + ", override=" + override + ", rejection="
				+ Arrays.toString(rejection) + ", instance=" + instance + '}';
	}

}

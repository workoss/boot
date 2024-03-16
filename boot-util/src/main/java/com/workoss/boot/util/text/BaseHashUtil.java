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
package com.workoss.boot.util.text;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.CRC32;

/**
 * Created by admin on 2017/1/19.
 *
 * @author workoss
 */
public abstract class BaseHashUtil {

	public static final int MURMUR_SEED = 1318007700;

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * createThreadLocalMessageDigest
	 * @param digest
	 * @return
	 */
	private static ThreadLocal<MessageDigest> createThreadLocalMessageDigest(final String digest) {
		return new ThreadLocal<MessageDigest>() {
			@Override
			protected MessageDigest initialValue() {
				try {
					return MessageDigest.getInstance(digest);
				}
				catch (NoSuchAlgorithmException e) {
					throw new RuntimeException(
							"unexpected exception creating MessageDigest instance for [" + digest + "]", e);
				}
			}
		};
	}

	private static final ThreadLocal<MessageDigest> MD5_DIGEST = createThreadLocalMessageDigest("MD5");

	private static final ThreadLocal<MessageDigest> SHA_1_DIGEST = createThreadLocalMessageDigest("SHA-1");

	private static SecureRandom random = new SecureRandom();

	public static byte[] md5(byte[] input, int iterations) {
		return digest(input, get(MD5_DIGEST), null, iterations);
	}

	////////////////// SHA1 ///////////////////

	public static byte[] sha1(byte[] input) {
		return digest(input, get(SHA_1_DIGEST), null, 1);
	}

	public static byte[] sha1(String input) {
		return digest(input.getBytes(UTF_8), get(SHA_1_DIGEST), null, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt) {
		return digest(input, get(SHA_1_DIGEST), salt, 1);
	}

	public static byte[] sha1(String input, byte[] salt) {
		return digest(input.getBytes(UTF_8), get(SHA_1_DIGEST), salt, 1);
	}

	/**
	 * 对输入字符串进行sha1散列，带salt而且迭代达到更高更高的安全性.
	 * @param input 输入值
	 * @param salt 盐
	 * @param iterations 迭代次数
	 * @return 加密后
	 */
	public static byte[] sha1(byte[] input, byte[] salt, int iterations) {
		return digest(input, get(SHA_1_DIGEST), salt, iterations);
	}

	/**
	 * 对输入字符串进行sha1散列，带salt而且迭代达到更高更高的安全性.
	 * @param input 输入值
	 * @param salt 盐
	 * @param iterations 迭代次数
	 * @return 加密后
	 */
	public static byte[] sha1(String input, byte[] salt, int iterations) {
		return digest(input.getBytes(UTF_8), get(SHA_1_DIGEST), salt, iterations);
	}

	private static MessageDigest get(ThreadLocal<MessageDigest> messageDigest) {
		MessageDigest instance = messageDigest.get();
		instance.reset();
		return instance;
	}

	/**
	 * 对字符串进行散列, 支持md5与sha1算法.
	 * @param input 输入值
	 * @param digest messageDigest
	 * @param salt 盐
	 * @param iterations 迭代次数
	 * @return 加密
	 */
	private static byte[] digest(byte[] input, MessageDigest digest, byte[] salt, int iterations) {
		// 带盐
		if (salt != null) {
			digest.update(salt);
		}

		// 第一次散列
		byte[] result = digest.digest(input);

		// 如果迭代次数>1，进一步迭代散列
		for (int i = 1; i < iterations; i++) {
			digest.reset();
			result = digest.digest(result);
		}

		return result;
	}

	/**
	 * 用SecureRandom生成随机的byte[]作为salt.
	 * @param numBytes salt数组的大小
	 * @return 生成
	 */
	public static byte[] generateSalt(int numBytes) {
		if (numBytes < 1) {
			throw new IllegalArgumentException("numBytes argument must be a positive integer (1 or larger)");
		}
		byte[] bytes = new byte[numBytes];
		random.nextBytes(bytes);
		return bytes;
	}

	public static byte[] sha1File(InputStream input) throws IOException {
		return digestFile(input, get(SHA_1_DIGEST));
	}

	public static byte[] md5File(InputStream input) throws IOException {
		return digestFile(input, get(MD5_DIGEST));
	}

	private static byte[] digestFile(InputStream input, MessageDigest messageDigest) throws IOException {
		int bufferLength = 8 * 1024;
		byte[] buffer = new byte[bufferLength];
		int read = input.read(buffer, 0, bufferLength);

		while (read > -1) {
			messageDigest.update(buffer, 0, read);
			read = input.read(buffer, 0, bufferLength);
		}

		return messageDigest.digest();
	}

	////////////////// 基于JDK的CRC32 ///////////////////

	/**
	 * 对输入字符串进行crc32散列返回int, 返回值有可能是负数.
	 * <p>
	 * Guava也有crc32实现, 但返回值无法返回long，所以统一使用JDK默认实现
	 * @param input 输入值
	 * @return crc32
	 */
	public static int crc32AsInt(String input) {
		return crc32AsInt(input.getBytes(UTF_8));
	}

	/**
	 * 对输入字符串进行crc32散列返回int, 返回值有可能是负数.
	 * <p>
	 * Guava也有crc32实现, 但返回值无法返回long，所以统一使用JDK默认实现
	 * @param input 输入值
	 * @return crc32
	 */
	public static int crc32AsInt(byte[] input) {
		CRC32 crc32 = new CRC32();
		crc32.update(input);
		// CRC32 只是 32bit int，为了CheckSum接口强转成long，此处再次转回来
		return (int) crc32.getValue();
	}

	/**
	 * 对输入字符串进行crc32散列，与php兼容，在64bit系统下返回永远是正数的long
	 * <p>
	 * Guava也有crc32实现, 但返回值无法返回long，所以统一使用JDK默认实现
	 * @param input 输入值
	 * @return crc32
	 */
	public static long crc32AsLong(String input) {
		return crc32AsLong(input.getBytes(UTF_8));
	}

	/**
	 * 对输入字符串进行crc32散列，与php兼容，在64bit系统下返回永远是正数的long
	 * <p>
	 * Guava也有crc32实现, 但返回值无法返回long，所以统一使用JDK默认实现
	 * @param input 输入值
	 * @return crc32
	 */
	public static long crc32AsLong(byte[] input) {
		CRC32 crc32 = new CRC32();
		crc32.update(input);
		return crc32.getValue();
	}

	/**
	 * 对输入字符串进行murmur128散列, 返回值可能是负数
	 */
	public static void removeMd5Digest() {
		MD5_DIGEST.remove();
	}

	public static void removeSha1Digest() {
		SHA_1_DIGEST.remove();
	}

}

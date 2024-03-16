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
package com.workoss.boot.util.security;

import com.workoss.boot.util.text.BaseEncodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

/**
 * 加密
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class CryptoUtil {

	private static final Logger log = LoggerFactory.getLogger(CryptoUtil.class);

	private static final String ASA_ALG = "RSA";

	private static final String AES_ALG = "AES";

	private static final String AES_CBC_ALG = "AES/CBC/PKCS5Padding";

	private static final String AES_CBC_ALG_7 = "AES/CBC/PKCS7Padding";

	private static final String AES_CBC_NOPADING_ALG = "AES/CBC/NoPadding";

	private static final String HMACSHA1_ALG = "HmacSHA1";

	private static final int DEFAULT_HMACSHA1_KEYSIZE = 160;

	private static final int DEFAULT_AES_KEYSIZE = 128;

	private static final int DEFAULT_IVSIZE = 16;

	private static SecureRandom random = secureRandom();

	/**
	 * 使用HMAC-SHA256进行消息签名, 返回字节数组,长度为20字节.
	 * @param input 原始输入字符数组
	 * @param key HMAC-SHA1密钥
	 * @return 加密后
	 */
	public static byte[] hmacSha256(byte[] input, byte[] key) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(secretKey);
			return mac.doFinal(input);
		}
		catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	// -- HMAC-SHA1 funciton --//

	/**
	 * 使用HMAC-SHA1进行消息签名, 返回字节数组,长度为20字节.
	 * @param input 原始输入字符数组
	 * @param key HMAC-SHA1密钥
	 * @return 加密
	 */
	public static byte[] hmacSha1(byte[] input, byte[] key) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, HMACSHA1_ALG);
			Mac mac = Mac.getInstance(HMACSHA1_ALG);
			mac.init(secretKey);
			return mac.doFinal(input);
		}
		catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static SecureRandom secureRandom() {
		try {
			return SecureRandom.getInstance("SHA1PRNG");
		}
		catch (NoSuchAlgorithmException e) {// NOSONAR
			return new SecureRandom();
		}
	}

	/**
	 * 校验HMAC-SHA1签名是否正确.
	 * @param expected 已存在的签名
	 * @param input 原始输入字符串
	 * @param key 密钥
	 * @return true/false
	 */
	public static boolean isMacValid(byte[] expected, byte[] input, byte[] key) {
		byte[] actual = hmacSha1(input, key);
		return Arrays.equals(expected, actual);
	}

	public static boolean isMacSha256Valid(byte[] expected, byte[] input, byte[] key) {
		byte[] actual = hmacSha256(input, key);
		return Arrays.equals(expected, actual);
	}

	/**
	 * 生成HMAC-SHA1密钥,返回字节数组,长度为160位(20字节). HMAC-SHA1算法对密钥无特殊要求, RFC2401建议最少长度为160位(20字节).
	 * @return hmacsha1key
	 */
	public static byte[] generateHmacSha1Key() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(HMACSHA1_ALG);
			keyGenerator.init(DEFAULT_HMACSHA1_KEYSIZE);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		}
		catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	///////////// -- AES funciton --//////////

	public static byte[] aesEncrypt(byte[] input, byte[] key) {
		return aes(input, key, Cipher.ENCRYPT_MODE);
	}

	public static byte[] aesEncryptNoPading(byte[] input, byte[] key, byte[] iv) {
		byte[] encryptResult = aes(input, key, iv, Cipher.ENCRYPT_MODE, AES_CBC_NOPADING_ALG);
		return encryptResult;
	}

	/**
	 * 使用AES加密原始字符串.
	 * @param input 原始输入字符数组
	 * @param key 符合AES要求的密钥
	 * @param iv 初始向量
	 * @return 加密
	 */
	public static byte[] aesEncrypt(byte[] input, byte[] key, byte[] iv) {
		return aes(input, key, iv, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * @param input Hex编码的加密字符串
	 * @param key 符合AES要求的密钥
	 * @return 加密
	 */
	public static String aesDecrypt(byte[] input, byte[] key) {
		byte[] decryptResult = aes(input, key, Cipher.DECRYPT_MODE);
		return new String(decryptResult, StandardCharsets.UTF_8);
	}

	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * @param input Hex编码的加密字符串
	 * @param key 符合AES要求的密钥
	 * @param iv 初始向量
	 * @return 加密
	 */
	public static String aesDecrypt(byte[] input, byte[] key, byte[] iv) {
		byte[] decryptResult = aes(input, key, iv, Cipher.DECRYPT_MODE);
		return new String(decryptResult, StandardCharsets.UTF_8);
	}

	// public static byte[] aesDecryptPkcs7Padding(byte[] input,byte[] key, byte[] iv){
	// byte[] decryptResult = aesPKC7Padding(input, key, iv, Cipher.DECRYPT_MODE);
	// return decryptResult;
	// }

	public static byte[] aesDecryptNoPading(byte[] input, byte[] key, byte[] iv) {
		byte[] decryptResult = aes(input, key, iv, Cipher.DECRYPT_MODE, AES_CBC_NOPADING_ALG);
		return decryptResult;
	}

	/**
	 * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * @param input 原始字节数组
	 * @param key 符合AES要求的密钥
	 * @param mode Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 * @return 加密
	 */
	private static byte[] aes(byte[] input, byte[] key, int mode) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, AES_ALG);
			Cipher cipher = Cipher.getInstance(AES_ALG);
			cipher.init(mode, secretKey);
			return cipher.doFinal(input);
		}
		catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * @param input 原始字节数组
	 * @param key 符合AES要求的密钥
	 * @param iv 初始向量
	 * @param mode Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 * @return 加密
	 */
	private static byte[] aes(byte[] input, byte[] key, byte[] iv, int mode) {
		return aes(input, key, iv, mode, AES_CBC_ALG);
	}

	// private static byte[] aesPKC7Padding(byte[] input, byte[] key, byte[] iv, int mode)
	// {
	// try {
	// Security.addProvider(new BouncyCastleProvider());
	// SecretKey secretKey = new SecretKeySpec(key, AES_ALG);
	// Cipher cipher = Cipher.getInstance(AES_CBC_ALG_7,"BC");
	// IvParameterSpec ivSpec = new IvParameterSpec(iv);
	// cipher.init(mode, secretKey, ivSpec);
	// return cipher.doFinal(input);
	// } catch (GeneralSecurityException e) {
	// throw new RuntimeException(e);
	// }
	// }

	private static byte[] aes(byte[] input, byte[] key, byte[] iv, int mode, String aesCbcFlag) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, AES_ALG);
			AlgorithmParameters param = AlgorithmParameters.getInstance(AES_ALG);
			Cipher cipher = Cipher.getInstance(aesCbcFlag);
			param.init(new IvParameterSpec(iv));
			cipher.init(mode, secretKey, param);
			return cipher.doFinal(input);
		}
		catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成AES密钥,返回字节数组, 默认长度为128位(16字节).
	 * @return 加密
	 */
	public static byte[] generateAesKey() {
		return generateAesKey(DEFAULT_AES_KEYSIZE);
	}

	/**
	 * 生成AES密钥,可选长度为128,192,256位.
	 * @param keysize key大小
	 * @return 加密
	 */
	public static byte[] generateAesKey(int keysize) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALG);
			keyGenerator.init(keysize);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		}
		catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成随机向量,默认大小为cipher.getBlockSize(), 16字节.
	 * @return 加密
	 */
	public static byte[] generateIv() {
		byte[] bytes = new byte[DEFAULT_IVSIZE];
		random.nextBytes(bytes);
		return bytes;
	}

	public static String getRandomStr(int length) {
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static PublicKey getRSAPublicKey(String publicKey) {
		try {
			byte[] keyBytes = BaseEncodeUtil.decodeBase64(publicKey);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(ASA_ALG);
			return keyFactory.generatePublic(keySpec);
		}
		catch (Exception e) {
			log.error("获取PublicKey实例失败:", e);
		}
		return null;
	}

	public static PrivateKey getRSAPrivateKey(String privateKey) {
		try {
			byte[] keyBytes = BaseEncodeUtil.decodeBase64(privateKey);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(ASA_ALG);
			return keyFactory.generatePrivate(keySpec);
		}
		catch (Exception e) {
			log.error("获取PrivateKey实例:", e);
		}
		return null;
	}

	public static RSAKeys generateRSAKeys() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ASA_ALG);
			keyPairGenerator.initialize(2048);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			RSAKeys rsaKeys = new RSAKeys(keyPair.getPublic(), keyPair.getPrivate());
			return rsaKeys;
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("generate rsakeys error:" + e.getMessage());
		}

	}

	public static class RSAKeys {

		private PublicKey publicKey;

		private PrivateKey privateKey;

		public RSAKeys(PublicKey publicKey, PrivateKey privateKey) {
			this.publicKey = publicKey;
			this.privateKey = privateKey;
		}

		public PublicKey getPublicKey() {
			return publicKey;
		}

		public void setPublicKey(PublicKey publicKey) {
			this.publicKey = publicKey;
		}

		public PrivateKey getPrivateKey() {
			return privateKey;
		}

		public void setPrivateKey(PrivateKey privateKey) {
			this.privateKey = privateKey;
		}

	}

}

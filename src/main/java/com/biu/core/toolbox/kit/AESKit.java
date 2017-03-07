package com.biu.core.toolbox.kit;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESKit {

	private static final String key = "springblade";

	/**
	 * 加密
	 * @param content 需要加密的内容
	 * @param password  加密密码
	 * @return
	 */
	private static byte[] encryptByte(String content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * @param content  待解密内容
	 * @param password 解密密钥
	 * @return
	 */
	private static byte[] decryptByte(byte[] content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * AES加密
	 * @param str
	 * @return
	 */
	public static String encrypt(String str) {
		return HexKit.binary2Hex(encryptByte(str, key));
	}

	/**
	 * AES解密
	 * @param str
	 * @return
	 */
	public static String decrypt(String str) {
		// 16进制转2进制
		byte[] decryptFrom = HexKit.hex2Byte(str);
		// 根据byte进行解码
		byte[] decryptResult = decryptByte(decryptFrom, key);
		return new String(decryptResult);
	}

	public static void main(String[] args) {
		String str = "spring+springmvc+beetl+beetlsql+shiro开发框架";
		System.out.println("加密前：" + str);
		String s = AESKit.encrypt(str);
		System.out.println("加密后：" + s);
		System.out.println("解密后：" + AESKit.decrypt(s));
	}
}

package com.boyiqove.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/*
 * 
 * AES 与PHP互通
 */
public class AES{
	//private static final String CHARSET   = "UTF-8";
	private static final String ALGORITHMS = "AES/CBC/PKCS5Padding";
	//private static final String ALGORITHMS = "AES/CBC/NoPadding";
	private static final String ALGORITHM  = "AES";
//	private static final  byte[] KEYS     = {0x32, 0x10, 0xc2, 0xda, 
//											0x3f, 0x13, 0xc8, 0x3e,
//											0xf8, 0x2a, 0x43, 0xa1, 
//											0x32, 0xc0, 0x73, 0x21};
//    
	private static final String STR_KEYS = "1234567812345678";
    private static final byte[] KEYS = STR_KEYS.getBytes();

//	public static byte[] encrypt(String source, String charset) {  
//		try {      
//			return encrypt(source.getBytes(charset));
//		} catch (Exception e) {  
//			e.printStackTrace();  
//			return null;  
//		}  
//	}
//
//	public static byte[] encrypt(byte[] data) {  
//		try {      
//			IvParameterSpec zeroIv = new IvParameterSpec(KEYS);  
//			SecretKeySpec key1 = new SecretKeySpec(KEYS, ALGORITHM);  
//			Cipher cipher = Cipher.getInstance(ALGORITHMS);  
//			cipher.init(Cipher.ENCRYPT_MODE, key1, zeroIv);  
//			return cipher.doFinal(data);
//		} catch (Exception e) {  
//			e.printStackTrace();  
//			return null;  
//		}  
//	}
//
//	public static String decrypt(byte[] data, String charset) {  
//		try {  
//			IvParameterSpec zeroIv = new IvParameterSpec(KEYS);  
//			SecretKeySpec key1 = new SecretKeySpec(KEYS, ALGORITHM);  
//			Cipher cipher = Cipher.getInstance(ALGORITHMS);  
//			cipher.init(Cipher.DECRYPT_MODE, key1, zeroIv);  
//			return new String(cipher.doFinal(data), charset); 
//		} catch (Exception e) {  
//			e.printStackTrace();  
//			return null;  
//		}  
//	}  
//    
	
    public static String encrypt(byte[] data, String charset) {
        try {
        	Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        	int blockSize = cipher.getBlockSize();
            
        	int plaintextLength = data.length;
        	if (plaintextLength % blockSize != 0) {
        		plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        	}

        	byte[] plaintext = new byte[plaintextLength];
        	System.arraycopy(data, 0, plaintext, 0, data.length);

        	SecretKeySpec keyspec = new SecretKeySpec(KEYS, "AES");
        	IvParameterSpec ivspec = new IvParameterSpec(KEYS);

        	cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            
        	return new String(Base64.encodeBase64(cipher.doFinal(plaintext)), charset);
        } catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }

    public static String decrypt(byte[] data, String charset) {
    	try
    	{
            byte[] base64 = Base64.decodeBase64(data);
            
    		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
    		SecretKeySpec keyspec = new SecretKeySpec(KEYS, "AES");
    		IvParameterSpec ivspec = new IvParameterSpec(KEYS);
            
    		cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

    		return new String(cipher.doFinal(base64), charset).trim();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
}


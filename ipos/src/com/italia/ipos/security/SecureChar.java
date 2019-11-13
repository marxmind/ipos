package com.italia.ipos.security;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.italia.ipos.enm.Ipos;

/**
 * 
 * @author mark italia
 * @since 9/27/2016
 * this class is use for encoding and decoding of character
 */
public class SecureChar {

	public static String encode(String val){
		
		try{
		// encode with padding
		String encoded = Base64.getEncoder().encodeToString(val.getBytes(Ipos.SECURITY_ENCRYPTION_FORMAT.getName()));
		// encode without padding
		//String encoded = Base64.getEncoder().withoutPadding().encodeToString(val.getBytes(Ipos.SECURITY_ENCRYPTION_FORMAT.getName()));
		
		return encoded;
		}catch(Exception e){}
		return null;
	}
	public static String decode(String val){
		try{
			byte [] barr = Base64.getDecoder().decode(val);
			return new String(barr,Ipos.SECURITY_ENCRYPTION_FORMAT.getName());
			}catch(Exception e){}
			return null;
	}
	
	
	public static void main(String[] args) {
		String val = SecureChar.encode("octmarkoberrivera1819italia86*");
		System.out.println("Encode: " + val);
		System.out.println("Decode: " + SecureChar.decode(val));
		val = SecureChar.decode(val);
		val = val.replace("mark", "");
		val = val.replace("rivera", "");
		val = val.replace("italia", "");
		System.out.println(val);
	}
	
}

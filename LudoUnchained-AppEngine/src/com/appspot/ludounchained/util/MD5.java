package com.appspot.ludounchained.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5 {

	private MD5() {
	}

	public static String encrypt(String input) {
	    MessageDigest digest;
	    try 
	    {
	        digest = MessageDigest.getInstance("MD5");
	        digest.update(input.getBytes(),0, input.length());
	        String hash = new BigInteger(1, digest.digest()).toString(16);
	        return hash;
	    } 
	    catch (NoSuchAlgorithmException e) 
	    {
	        e.printStackTrace();
	    }
	    return "";
	}
}

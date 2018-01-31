package com.eportal.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Hex {
	private static MessageDigest digest;
	
	private Sha256Hex(){
		try {
			digest = MessageDigest.getInstance("Sha-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			digest = null;
			System.out.println("Cannot instantiate sha256Hex singleton class");
		}
	}

	public static MessageDigest getInstance(){
		if (digest == null){
			Sha256Hex obj = new Sha256Hex();
		}
		return digest;
	}
}

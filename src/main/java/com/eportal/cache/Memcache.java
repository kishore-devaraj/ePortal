package com.eportal.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.eportal.models.SignInModel;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class Memcache {
	public static MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	
	public static Entity getEmployee(SignInModel signInModel){
		byte[] stream;
		ObjectInputStream in;
		ByteArrayInputStream byteIn;
		Entity entity = null;
		
		System.out.println("Employee get from memcache");
		Object obj = syncCache.get(signInModel.getEmployeeId());
		stream = (byte[]) obj;
		
		if (stream != null){
			try{
				byteIn = new ByteArrayInputStream(stream);
				in = new ObjectInputStream(byteIn);
				obj = in.readObject();
				entity = (Entity) obj;
			}catch(Exception e){
				System.out.println(e);
				System.out.println("Error occured while reading from memcache");
			}
		}
		
		return entity;
	}
	
	public static void putEmployee(Entity entity){
		
		try{
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream out;
			byte[] stream;
			try{
				out = new ObjectOutputStream(byteOut);
				out.writeObject(entity);
				out.flush();
			}catch(Exception ee){
				System.out.println(ee);
			}
			stream = byteOut.toByteArray();
			
			syncCache.put(entity.getProperty("employeeId"),stream);
			System.out.println("Employee put in memcache");
		}catch(Exception e){
			System.out.println(e);
			System.err.println("Error while updating Memcache");
		}
	}
	
}

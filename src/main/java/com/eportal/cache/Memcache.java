package com.eportal.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class Memcache {
	public static MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	public static Logger LOGGER = Logger.getLogger(Memcache.class.getName());
	
	static {
		LOGGER.setLevel(Level.INFO);
	}
	
	public static Entity getEmployee(String employeeId){
		byte[] stream;
		ObjectInputStream in;
		ByteArrayInputStream byteIn;
		Entity entity = null;
		
		LOGGER.info("Fetching employee details from memcache");
		Object obj = syncCache.get(employeeId);
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
			LOGGER.info("Employee details are stored in memcache");
		}catch(Exception e){
			System.out.println(e);
			System.err.println("Error while updating Memcache");
		}
	}
	
	
	public static void deleteEmployee(String employeeId){
		try{
			Object obj = syncCache.get(employeeId);
			LOGGER.info("Obj from memcache " + obj);
			if (obj != null){
				syncCache.delete(employeeId);
				LOGGER.info("Employee details deleted from memcache");
			}
		}catch(Exception e){
			LOGGER.info("No Employee was found for deleting");
		}
	}
	
}

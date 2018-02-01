package com.eportal.cache;

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
		Entity entity = null;
		try{
			entity = (Entity) syncCache.get(employeeId);
			if (entity != null){
				LOGGER.info("Entity being retrieved from memcache");
			}else{
				LOGGER.info("No entity found in the memcache");
			}
		}catch(Exception e){
			LOGGER.info("No entity found in the memcache");
		}
		return entity;
	}
	
	public static void putEmployee(Entity entity){
		
		try{
			syncCache.put(entity.getProperty("employeeId"),entity);
			LOGGER.info("Entity successfully added to memcache");
		}catch(Exception e){
			LOGGER.warning("Exception while adding entity to the memcache");
		}
	}
	
	
	public static void deleteEmployee(String employeeId){
		try{
			Object obj = syncCache.get(employeeId);
			// LOGGER.info("Obj from memcache " + obj);
			if (obj != null){
				syncCache.delete(employeeId);
				LOGGER.info("Employee details deleted from memcache");
			}
		}catch(Exception e){
			LOGGER.info("No Employee was found for deleting");
		}
	}
	
}

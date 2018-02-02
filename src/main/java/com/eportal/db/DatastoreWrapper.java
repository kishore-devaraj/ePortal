package com.eportal.db;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class DatastoreWrapper {
	private DatastoreService datastore;
	private MemcacheService cache;
	private Transaction trx;
	
	private static Logger logger = Logger.getLogger(DatastoreWrapper.class.getName());
	
	static {
		logger.setLevel(Level.INFO); 
	}
	
	public DatastoreWrapper(String namespace){
		datastore = DatastoreServiceFactory.getDatastoreService();
		cache = MemcacheServiceFactory.getMemcacheService();
		NamespaceManager.set(namespace);
	}
	
	
	/*
	 * Description: Used to put or update the entity in Datastore and Cache
	 * Param: KeyName, Entity Object
	 * return: boolean
	 */
	
	public boolean put(String id,Entity entity){
		
		// Begin the transaction
		trx = this.datastore.beginTransaction();
		
		try{
			this.datastore.put(entity);
			this.cache.put(id,entity);
			this.trx.commit();
			logger.info("Value updated in both datastore and memcache");
		}catch(Exception e){
			logger.warning("Exception occured while putting the entity");
			logger.info("Excpetion Name" + e);
		}finally{
			if(this.trx.isActive()){
				this.trx.rollback();
				logger.warning("Value not updated in the datastore & Memcache");
				return false;
			}
		}
		return true;
	}
	
	
	/*
	 * Description: Getting Entity details from the datastore
	 * Params: Kind, IdName
	 * returns: Entity, null
	 */
	
	public Entity get(String kind,String id){
		try{
			Entity entity = (Entity) cache.get(id);
			if (entity != null){
				return entity;
			}
			
			Key key = KeyFactory.createKey(kind, id);
			try{
				Entity dEntity = this.datastore.get(key);
				cache.put(id, dEntity);
				return dEntity;
			}catch(EntityNotFoundException e){
				logger.info("No entity found for the given key");
				return null;
			}
			
		}catch(Exception e){
			logger.warning("Exception occured while retreiving value from the datastore");
			return null;
		}		
	}
	
	
	/*
	 * Deleting Entry from datastore and cache
	 * Params: Kind, id
	 * returns: boolean
	 */
	
	public boolean delete(String kind,String id){
		this.trx = this.datastore.beginTransaction();
		
		try{
			Object obj = this.cache.get(id);
			if (obj != null){
				this.cache.delete(id);
			}
			
			Key key = KeyFactory.createKey(kind, id);
			this.datastore.delete(key);
			trx.commit();
			
		}catch(Exception e){
			logger.warning("Exception occurred while deleting");
			logger.info("Exception name: " + e);
		}finally{
			if(this.trx.isActive()){
				this.trx.rollback();
				return false;
			}
		}
		return true;
	}
}


/*
 * Datastore connection
 * Memcache connection
 * Namespace
 * 
 * 1. put
 * 2. get
 * 3, delete
 * 
 * sync up with cache
 */

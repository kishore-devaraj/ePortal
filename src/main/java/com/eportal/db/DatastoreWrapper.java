package com.eportal.db;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
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
	
	public void put(String id,Entity entity) throws Exception{
		
		// Begin the transaction
		trx = this.datastore.beginTransaction();
		logger.info("Namespace is " + NamespaceManager.get());
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
				throw new Exception();
			}
		}
	}
	
	
	/*
	 * Description: Getting Entity details from the datastore
	 * Params: Kind, IdName
	 * returns: Entity, null
	 */
	
	public Entity get(String kind,String id) throws EntityNotFoundException{
			Entity entity = (Entity) cache.get(id);
			if (entity != null){
				logger.info("Returning from memcache");
				return entity;
			}
			
			Key key = KeyFactory.createKey(kind, id);
			Entity dEntity = this.datastore.get(key);
			cache.put(id, dEntity);
			return dEntity;		
	}
	
	
	/*
	 * Deleting Entry from datastore and cache
	 * Params: Kind, id
	 * returns: boolean
	 */
	
	public void delete(String kind,String id) throws Exception{
		this.trx = this.datastore.beginTransaction();
		
		try{
			Object obj = this.cache.get(id);
			if (obj != null){
				this.cache.delete(id);
			}
			Key key = KeyFactory.createKey(kind, id);
			this.datastore.delete(key);
			this.trx.commit();
		}catch(Exception e){
			logger.warning("Exception occurred while deleting");
			logger.info("Exception name: " + e);
			throw new Exception();
		}finally{
			if(this.trx.isActive()){
				this.trx.rollback();
			}
		}
	}
	
	public void deleteEntityOnly(Entity entity) throws EntityNotFoundException{
		this.datastore.get(entity.getKey());
	}
	
	public List<Entity> getAll(String kind) throws Exception{
		Query query = new Query(kind);
		List<Entity> entities = this.datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		return entities;
	}
	
	public List<Entity> executeQuery(Query query) throws Exception{
		return this.datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
	}	
	
	public List<Entity> getByFitler(Filter filter,String kind) throws Exception {
		Query query = new Query(kind).setFilter(filter);
		List <Entity> entities = this.datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		return entities;
	}
	
	public List<Entity> getbyProjection(PropertyProjection projection, String Kind) throws Exception{
		Query query = new Query(Kind).addProjection(projection);
		List<Entity> entities = this.datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		return entities;
	}

	public List<Entity> getByComposite(Filter filter1, Filter filter2, String Kind) throws Exception {
		CompositeFilter compositeFilter = CompositeFilterOperator.and(filter1,filter2);
		Query query = new Query(Kind).setFilter(compositeFilter);
		return this.datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
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

package com.eportal.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eportal.cache.Memcache;
import com.eportal.db.Datastore;
import com.eportal.utils.GenericResponse;
import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class DeleteResource {
	
public DatastoreService datastore;
	private static Logger LOGGER = Logger.getLogger(DeleteResource.class.getName());
	
	static{
		LOGGER.setLevel(Level.INFO);
	}

	public DeleteResource(){
		if (this.datastore == null){
			this.datastore = Datastore.getInstance();
		}
	}

	public GenericResponse Employee(String org,String id, GenericResponse response){
		String kind = "Employee";
		NamespaceManager.set(org);
		
		Key key = KeyFactory.createKey(kind,id);
		
		try{
			Entity entity = datastore.get(key);
			// System.out.println(entity);
			datastore.delete(key);
			LOGGER.info("Employee Details deleted from datastore");
			
			// Deleting from memcache
			Memcache.deleteEmployee(id);
			
			// Deleting the entry in address table too
			key = (Key) entity.getProperty("Address");
			if (key != null){
				datastore.delete(key);
			}else{
				LOGGER.info("No data found in address table");
			}
			
			response.setCode(200);
			response.setData("message", "Employee Deleted");
		}catch(EntityNotFoundException e){
			LOGGER.warning("Invalid employeeId given for deleting");
			response.setCode(400);
			response.setData("error", "No such Employee exists");
		}catch(Exception e){
			System.err.println(e);
			response.setCode(400);
			response.setData("error", "Deletion operation cannot be performed now");
		}
		return response;
	}
}


/*
 * Set the namespace
 * Check whether the resource exists
 * Delete only if it is
 */


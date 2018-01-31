package com.eportal.service;

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
			
			
			// Deleting the entry in address table too
			key = (Key) entity.getProperty("Address");
			if (key != null){
				datastore.delete(key);
			}else{
				System.out.println("No data found in address table");
			}
			
			System.out.println("Employee Deleted");
			response.setCode(200);
			response.setData("message", "Employee Deleted");
		}catch(EntityNotFoundException e){
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


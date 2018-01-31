package com.eportal.db;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class Datastore {

	private static DatastoreService datastore;
	
	private Datastore(){
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
	public static DatastoreService getInstance(){
		if (datastore == null){
			Datastore obj = new Datastore();
			System.out.println("Datastore instance created");
		}
		return datastore;
	}	
}

/*
 * 1. Multilatency
 * 2. Create a Key for a new employee
 * 3. Make this class singleton
 * 
 */ 

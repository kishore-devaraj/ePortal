package com.eportal.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eportal.db.Datastore;
import com.eportal.utils.GenericResponse;
import com.google.appengine.api.datastore.DatastoreService;

public class GetEmployeeBySkillsets {
	private DatastoreService datastore;
	private static final Logger LOGGER = Logger.getLogger(GetEmployeeService.class.getName());
	
	
	static{
		LOGGER.setLevel(Level.INFO);
	}
	
	public GetEmployeeBySkillsets(){
		if (this.datastore == null){
			this.datastore = Datastore.getInstance();
		}
	}
	
	public GenericResponse get(String org, String[] skillsets, GenericResponse response){
		
		
		return response;
	}
	
	
	
}

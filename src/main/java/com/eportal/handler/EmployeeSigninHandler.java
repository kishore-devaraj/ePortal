package com.eportal.handler;

import java.util.logging.Logger;

import com.eportal.db.DatastoreWrapper;
import com.eportal.models.SignInModel;
import com.eportal.utils.GenericResponse;
import com.eportal.utils.Sha256Hex;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

public class EmployeeSigninHandler {
	private final static String employeeKind = "Employee";
	private final static Logger logger = Logger.getLogger(EmployeeSigninHandler.class.getName());
	private static DatastoreWrapper datastore;
	
	// Authenticate the user
	public GenericResponse auth(SignInModel signInModel, GenericResponse response){
		datastore = new DatastoreWrapper(signInModel.getOrganisation());
		try{
			Entity entity = datastore.get(employeeKind,signInModel.getEmployeeId());
			if (((String)entity.getProperty("password")).equals(Sha256Hex.hashPassword(signInModel.getPassword()))){
				response.setCode(200);
				response.setData("message","Sign in successful");
			}else{
				response.setCode(200);
				response.setData("message","Password Incorrect");
			}
		}catch(EntityNotFoundException e){
			response.setCode(200);
			response.setData("message","User is not signed up yet");
		}catch(Exception e){
			logger.warning("Exception occured while signin");
			logger.info("Exception name " + e);
			response.setCode(400);
			response.setData("error","Exception occured while signin");
		}
		return response;
	}
}
/*
 * Check whether exists an entity
 * if present check the hashMap value
 * return True or False
 */

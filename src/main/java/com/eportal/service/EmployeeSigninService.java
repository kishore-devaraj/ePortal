package com.eportal.service;

import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import com.eportal.cache.Memcache;
import com.eportal.db.Datastore;
import com.eportal.models.SignInModel;
import com.eportal.utils.GenericResponse;
import com.eportal.utils.Sha256Hex;
import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class EmployeeSigninService {
	public DatastoreService datastore;
	public String salt = "mysalt";
	public String hashed;
	
	public EmployeeSigninService(){
		if (this.datastore == null){
			this.datastore = Datastore.getInstance();
		}
	}
	
	// Authenicate the user
	public GenericResponse auth(SignInModel signInModel, GenericResponse response){
		String kind = "Employee";
		Entity entity;
		
		// Set the namespace
		NamespaceManager.set(signInModel.getOrganisation());
		
		
		// Check the memcache
		Entity memcacheEntity = Memcache.getEmployee(signInModel.getEmployeeId());
		if (memcacheEntity != null){
			System.out.println("Entity received from memcache");
			entity = memcacheEntity;
		}else{
			Key key = KeyFactory.createKey(kind,signInModel.getEmployeeId());
			try{ 
				entity = this.datastore.get(key);
				
				// Update the memcache
				Memcache.putEmployee(entity);
				
			} catch (EntityNotFoundException e) {
				response.setCode(400);
				response.setData("message","No Employee Data exists with this Id!");
				return response;
			} catch(Exception e){
				System.out.println("Exception in sign in service");
				response.setCode(400);
				response.setData("error","Error happend while signing in!");
				return response;
			}
		}
			

			
			// Check for the password match
			try{
				MessageDigest digest = Sha256Hex.getInstance();
				String appendedPassword = salt + signInModel.getPassword();
				byte[] bytes = digest.digest(appendedPassword.getBytes("UTF-8"));
				hashed = DatatypeConverter.printHexBinary(bytes);
				
				if (hashed.equals(entity.getProperty("password"))){
					response.setCode(200);
					response.setData("message", "Successfully sign in!");
				}else{
					response.setCode(200);
					response.setData("message", "Incorrect password");
				}
			}catch(Exception ee){
				System.out.println(ee);
				response.setCode(400);
				response.setData("error","There is a problem with authentication");
			}
		
			return response;
		}
}
/*
 * Check whether exists an entity
 * if present check the hashMap value
 * return True or False
 */

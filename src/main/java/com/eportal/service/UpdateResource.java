package com.eportal.service;

import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import com.eportal.db.Datastore;
import com.eportal.models.Employee;
import com.eportal.utils.GenericResponse;
import com.eportal.utils.Sha256Hex;
import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class UpdateResource {
	public DatastoreService datastore;
	private String salt = "mysalt";
	
	public UpdateResource(){
		if (this.datastore == null){
			this.datastore = Datastore.getInstance();
		}
	}
	public GenericResponse Employee(Employee employee, GenericResponse response){
		NamespaceManager.set(employee.getOrganisation());
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String kind = "Employee";
		Entity addressEntity;
		
		// Check whether the employee is present or not
		Key key = KeyFactory.createKey(kind,employee.getEmployeeId());
		try{
			Entity entity = datastore.get(key);
			
			// If they are updating the password check whether confirm password is present
			if((employee.getPassword() != null) && (employee.getConfirmPassword() != null)){
				if (employee.getPassword().equals(employee.getConfirmPassword())){
						
						// Storing password in encrypted form (SHA-256)
						try{
							MessageDigest digest = Sha256Hex.getInstance();
							String saltedPassword = salt + employee.getPassword(); 
							byte[] bytes = digest.digest(saltedPassword.getBytes("UTF-8"));
							String hashed = DatatypeConverter.printHexBinary(bytes);
							entity.setUnindexedProperty("password", hashed);
							
						}catch(Exception ee){
							System.out.println(ee);
							System.out.println("Some error while creating password hash");
							response.setCode(400);
							response.setData("error", "This user cannot be created. Something wrong with the password");
						}
					
				}else{
					response.setCode(400);
					response.setData("error", "Password doesn't match");
					return response;
				}
			}else if((employee.getPassword() != null) && (employee.getConfirmPassword() == null)){
				response.setCode(400);
				response.setData("error","confirm Password is missing");
				return response;
			}
			
			// Check if employeeName is given
			if (employee.getEmployeeName() != null){
				entity.setIndexedProperty("employeeName", employee.getEmployeeName());
			}
			
			// Updating skillsets
			if (employee.getSkillsets() != null){
				entity.setIndexedProperty("skillsets",employee.getSkillsets());
			}
			
			// Updating the Address
			if (employee.getAddress() != null){
				try{
					key = (Key) entity.getProperty("Address");
					if (key == null){
						addressEntity = new Entity("Address",employee.getEmployeeId());
					}else{
						addressEntity = datastore.get(key);
					}
					addressEntity.setUnindexedProperty("address",employee.getAddress());
					entity.setIndexedProperty("Address",addressEntity.getKey());
					datastore.put(addressEntity);
				}catch(Exception e){
					System.out.println(e);
					System.err.println("Error while updating the Address table");
				}
			}
			
			entity.setIndexedProperty("organisation", employee.getOrganisation());
			entity.setIndexedProperty("employeeId", employee.getEmployeeId());
			
			try{
				datastore.put(entity);
				System.out.println("Entity successfully updated");
				response.setCode(200);
				response.setData("message", "Successfully updated employee details");
			}catch(Exception e){
				System.out.println(e);
				response.setCode(400);
				response.setData("error", "Error occurred while updating the employee details");
			}
		}catch(EntityNotFoundException e){
			System.out.println(e);
			response.setCode(400);
			response.setData("error","Employee not found");
		}
		return response;
	}
}


/*
 * Set namespace
 * Check whether the employee exits
 * If so, update the given details
 */

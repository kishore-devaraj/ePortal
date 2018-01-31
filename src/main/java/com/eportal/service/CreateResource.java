package com.eportal.service;

import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import com.eportal.db.Datastore;
import com.eportal.models.Employee;
import com.eportal.utils.GenericResponse;
import com.eportal.utils.Sha256Hex;
import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class CreateResource {
	public DatastoreService datastore;
	private String salt = "mysalt";
	private String hashed = "";
	
	public CreateResource(){
		if (this.datastore == null){
			this.datastore = Datastore.getInstance();
		}
	}
	
	public GenericResponse Employee(Employee employee, GenericResponse response){
		// Set the namespace
		NamespaceManager.set(employee.getOrganisation());
		
		// Creating the new key
		Key key = KeyFactory.createKey("Employee",employee.getEmployeeId());
		
		// Check whether it already exists or not
		try{
			Entity entity = this.datastore.get(key);
			response.setCode(400);
			response.setData("error","This Employee Id already exists");
		}catch(EntityNotFoundException e){
			// Checking the passwords equality
			if (employee.getPassword().equals(employee.getConfirmPassword())){
			
				System.out.println("Creating Employee");
				Entity entity = new Entity("Employee",employee.getEmployeeId());
				
				try{
					// Setting up the Employee
					entity.setIndexedProperty("employeeId",employee.getEmployeeId());
					entity.setIndexedProperty("employeeName", employee.getEmployeeName());
					entity.setIndexedProperty("organisation",employee.getOrganisation());
					
					if (employee.getSkillsets() != null){
						System.out.println(employee.getSkillsets());
						entity.setIndexedProperty("skillsets",employee.getSkillsets());
					}
					
					
					// Storing password in encrypted form (SHA-256)
					try{
						MessageDigest digest = Sha256Hex.getInstance();
						String saltedPassword = salt + employee.getPassword(); 
						byte[] bytes = digest.digest(saltedPassword.getBytes("UTF-8"));
						hashed = DatatypeConverter.printHexBinary(bytes);
						entity.setUnindexedProperty("password", hashed);
						
					}catch(Exception ee){
						System.out.println(ee);
						System.out.println("Some error while creating password hash");
						response.setCode(400);
						response.setData("error", "This user cannot be created. Something wrong with the password");
					}					
					
										
					// Separate Address table
					if (employee.getAddress() != null){
						Entity addressEntity = new Entity("Address",employee.getEmployeeName(),entity.getKey());
						addressEntity.setIndexedProperty("address", employee.getAddress());
						entity.setUnindexedProperty("Address",addressEntity.getKey());
						datastore.put(addressEntity);
					}

					datastore.put(entity);
					System.out.println("Employee added");
					response.setCode(200);
					response.setData("message", "Employee was successfully created");
				}catch(Exception ee){
					System.out.println(ee);
					response.setCode(400);
					response.setData("error", "Error while creating entity");
				}
			}else{
				System.out.println("Password doesn't match");
				response.setCode(400);
				response.setData("error", "Passwords does not match");
			}
			
		}catch(Exception e){
			System.out.println(e);
			response.setCode(400);
			response.setData("error", "Error happened while creating this employee");
		}
		
		return response;	
	}	
}


/* 
 * Check whether employee already exists
 * Use multilatency
 * Create a unique Key and save the employee
 * Hash the password
 * Create the separate address and store its instance
 * Store the skillset in the ArrayList
 */
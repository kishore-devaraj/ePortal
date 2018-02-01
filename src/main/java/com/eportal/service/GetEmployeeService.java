package com.eportal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eportal.cache.Memcache;
import com.eportal.db.Datastore;
import com.eportal.models.EmployeeResponse;
import com.eportal.utils.EmployeeDetailsBuilder;
import com.eportal.utils.GenericResponse;
import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class GetEmployeeService {
	public DatastoreService datastore;
	private EmployeeResponse employeeResponse;
	private List<EmployeeResponse> listOfEmployees = new ArrayList<EmployeeResponse>();
	private ArrayList<Entity> arrayList;
	private static final Logger LOGGER = Logger.getLogger(GetEmployeeService.class.getName());
	
	
	{
		LOGGER.setLevel(Level.INFO);
		arrayList = new ArrayList<Entity>();
	}
	
	public GetEmployeeService(){
		if (this.datastore == null){
			this.datastore = Datastore.getInstance();
		}
	}
	
	
	
	
	// Get all Employees using query
	public GenericResponse getAllEmployees(String orgid, GenericResponse response){
		String kind = "Employee";
		
		// Set the namespace
		NamespaceManager.set(orgid);
		
		try{
			Query q = new Query(kind);
			PreparedQuery pq = datastore.prepare(q);
			arrayList.addAll(pq.asList(FetchOptions.Builder.withDefaults()));
			
			// Check whether it returns empty list
			if (arrayList == null){
				response.setCode(200);
				response.setData("message",arrayList);
				LOGGER.info("No employee data found");
				return response;
			}
			
			
			for (Entity e: arrayList){
					employeeResponse = new EmployeeResponse();
					employeeResponse = EmployeeDetailsBuilder.build(e);
					System.out.println("---------------------------------");
				// System.out.println("Employee response: " + employeeResponse);
				listOfEmployees.add(employeeResponse);
			}
			response.setCode(200);
			response.group("listofEmployees",listOfEmployees);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			LOGGER.warning("Exception occured while retrieving all results of Employee Kind");
			response.setCode(400);
			response.setData("error","Problems occured while fetching the Employees details");
		}
		return response;
	}
	
	
	
	
	
	
	public GenericResponse getEmployeeById(String org, String employeeId, GenericResponse response){
		String kind = "Employee";
		EmployeeResponse employeeResponse = new EmployeeResponse();
		
		// Set the namespace
		NamespaceManager.set(org);
		
		// Check the memcache
		Entity memcacheEntity = Memcache.getEmployee(employeeId);
		
		if (memcacheEntity != null){
			LOGGER.info("Employee found in memcache");
			employeeResponse = EmployeeDetailsBuilder.build(memcacheEntity);
			response.group("employeeDetails",employeeResponse);
			return response;
		}else{
			LOGGER.info("Fetching from datastore");
			try{
				Key key = KeyFactory.createKey(kind, employeeId);
				try{
					Entity entity = datastore.get(key);
					
					//Putting employee in memcache
					Memcache.putEmployee(entity);
					
					employeeResponse = EmployeeDetailsBuilder.build(entity);
					response.setCode(200);
					response.group("employeeDetails", employeeResponse);
				}catch(EntityNotFoundException e){
					LOGGER.warning("No entity found in datastore for the specified id");
					response.setCode(400);
					response.setData("error","No entity found for the specified employeeId");
				}
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
				response.setCode(400);
				response.setData("error", "Error happened while retrieving employee details");
			}
		}
		return response;
	}
	
}


/*
 * Get all the employee details
 * Get single employee
 * 
 * Details to be returned
 * id, name, address, organsation, skillsets
 */


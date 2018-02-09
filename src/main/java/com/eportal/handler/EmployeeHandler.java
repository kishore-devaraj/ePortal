package com.eportal.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import com.eportal.db.DatastoreWrapper;
import com.eportal.models.Employee;
import com.eportal.utils.GenericResponse;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class EmployeeHandler {
	private static DatastoreWrapper datastore;
	private final static String addressKind = "Address";
	private final static String employeeKind = "Employee";
	private final static String skillsetsKind = "Skillsets";
	
	private final static Logger logger = Logger.getLogger(EmployeeHandler.class.getName());
	
	public GenericResponse create(Employee employee, GenericResponse response){
		datastore = new DatastoreWrapper(employee.getOrganisation());
		try{
			Entity entity = datastore.get(employeeKind, employee.getEmployeeId());
			response.setCode(400);
			response.setData("error","This Employee Id already exists");
		}catch(EntityNotFoundException e){
			
			// Checking if both password matches
			if(employee.getPassword().equals(employee.getConfirmPassword())){
				try{
					Entity entity = employee.toEntity();
					datastore.put(employee.getEmployeeId(), entity);
					response.setCode(200);
					response.setData("message", "Employee was successfully created");
				}catch(Exception ee){
					logger.warning("Exception occured while creating new employee");
					logger.info("Exception name " + ee);
					response.setCode(400);
					response.setData("error", "Exception happened while creating this employee");
				}
			}else{
				logger.warning("Password doesn't match");
				response.setCode(400);
				response.setData("error", "Passwords does not match");
			}
		}
		return response;
	}
	
	
	
	public GenericResponse delete(String org, String id, GenericResponse response){
		datastore = new DatastoreWrapper(org);
		try{
			Entity entity = datastore.get(employeeKind,id);
			datastore.delete(employeeKind, id);
			
			// Deleting addressEntry
			try{
				Entity addressEntity = datastore.get(addressKind,id +"_addr");
				datastore.delete(addressKind, id + "_addr");
			}catch(Exception e){
				logger.info("No address record for this entity");
			}
			
			// Deleting Skillset
			try{
				Filter filter = new FilterPredicate("employeeId",FilterOperator.EQUAL,id);
				List<Entity> entities = this.datastore.getByFitler(filter, skillsetsKind);
				
				if(!(entities.isEmpty())){
					for(Entity e:entities){
						String key = e.getProperty("employeeId") + "_" + e.getProperty("skillset");
						System.out.println(key);
						this.datastore.delete(skillsetsKind,key);
					}
				}
			}catch(Exception e){
				logger.warning("No skillsets record found for this entity");
			}
			
			response.setCode(200);
			response.setData("message","Employee successfully deleted");
		}catch(EntityNotFoundException e){
			logger.info("No Such employee exists");
			response.setCode(400);
			response.setData("error", "No such employee exist");
		}catch(Exception e){
			response.setCode(400);
			response.setData("error", "Error happened while deleting this employee");
		}
		return response;
	}
	
	
	public GenericResponse update(Employee employee,GenericResponse response){
		datastore = new DatastoreWrapper(employee.getOrganisation());
		if((employee.getPassword() != null) && (employee.getConfirmPassword() != null)){
			if (employee.getPassword().equals(employee.getConfirmPassword())){
				logger.info("Password matches");
			}else{
				logger.warning("Password doesn't match");
				response.setCode(400);
				response.setData("error", "Password doesn't match");
				return response;
			}
		}else if((employee.getPassword() != null) && (employee.getConfirmPassword() == null)){
			response.setCode(400);
			response.setData("error","confirm Password is missing");
			return response;
		}
		
		try{
			Entity entity = employee.toEntity();
			datastore.put(employee.getEmployeeId(), entity);
			response.setCode(200);
			response.setData("message","Employee Details updated");
		}catch(EntityNotFoundException e){
			response.setCode(400);
			response.setData("error", "No such Employee exists");
		}catch(Exception e){
			response.setCode(400);
			logger.warning("Exception occured while updating the employee details");
			logger.warning("Exception name: " + e);
			e.printStackTrace();
			response.setData("error", "Exception occured while updating the employee details");
		}
		return response;
	}
	
	public GenericResponse getAll(String orgid, GenericResponse response){
		datastore = new DatastoreWrapper(orgid);
		Employee employee = new Employee();
		List<Entity> entities;
		List<Employee> listOfEmployees;
		try{
			entities = datastore.getAll(employeeKind);
			
			// Check whether it returns empty list
			if (entities.isEmpty()){
				response.setCode(200);
				response.setData("message",entities);
				logger.info("No employee data found");
				return response;
			}
			
			listOfEmployees = employee.fromEntities(entities);
			response.setCode(200);
			response.group("Employees",listOfEmployees);
		}catch(Exception e){
			logger.warning("Exception occured while getting all the employees");
			logger.info("Exception name: " + e);
			response.setCode(400);
			response.setData("error", "Exception occured while getting all the employees");
		}
		return response;
	}
	
	public GenericResponse getById(String org, String id, GenericResponse response){
		datastore = new DatastoreWrapper(org);
		Employee employee = new Employee();
		
		try{
			Entity entity = datastore.get(employeeKind, id);
			employee = employee.fromEntity(entity);
			response.setCode(200);
			response.group("employeeDetails",employee);
		}catch(EntityNotFoundException e){
			response.setCode(400);
			response.setData("error","No such Employee exists");
		}catch(Exception e){
			response.setCode(400);
			response.setData("error","Exception occured while retrieving Employye");
		}
		return response;
	}
	
	public GenericResponse getBySkill(String org, String[] skillsets, GenericResponse response){
		datastore = new DatastoreWrapper(org);
		Employee employee = new Employee();
		
		// Converting to ArrayList
		List<String> skillsetsList = new ArrayList<String>(Arrays.asList(skillsets));
		
		// Adding filter
		Filter filter = new FilterPredicate("skillsets",FilterOperator.IN, skillsetsList);
		
		try{
			List<Entity> entities = datastore.getByFitler(filter, employeeKind);
			if (!(entities.isEmpty())){
				List<Employee> listOfEmployees = employee.fromEntities(entities);
				response.setCode(200);
				response.group("listOfObjects", listOfEmployees);
			}else{
				response.setCode(200);
				response.setData("message","No data found");
			}
		}catch(Exception e){
			logger.warning("Exception occured while getting employees by skillsets");
			logger.info("Exception name " + e);
			response.setCode(400);
			response.setData("error","Exception occured while getting employees by skillsets");
		}
		return response;
	}
	
	public GenericResponse getEmployeeBySkillsetsAndExperience(String organisation, String skillsets, int experience,GenericResponse response)
	{
		datastore = new DatastoreWrapper(organisation);
		
		// Query the skillset record
		Filter filter1 = new FilterPredicate("skillset", FilterOperator.EQUAL, skillsets);
		Filter filter2 = new FilterPredicate("experience",FilterOperator.GREATER_THAN_OR_EQUAL, experience);
		
		List<Entity> employeeEntities = new ArrayList<Entity>();
		
		try {
			List<Entity> queryEntities = datastore.getByComposite(filter1, filter2, skillsetsKind);
			if(!(queryEntities.isEmpty())){
				for(Entity ent: queryEntities){
					Entity employeeEntity = datastore.get(employeeKind,(String) ent.getProperty("employeeId"));
					employeeEntities.add(employeeEntity);
				}
				
				Employee employee = new Employee();
				List<Employee> listofEmployees = employee.fromEntities(employeeEntities);
				
				
				response.setCode(200);
				response.group("listOfEmployees", listofEmployees);
			}else{
				response.setCode(200);
				response.setData("message","No Employees Record found");
			}
		} catch (Exception e) {
			logger.warning("Exception occured while query skillsets");
			logger.info("Exception name:" + e);
			response.setCode(400);
			response.setData("error","No employees found for this skillset");
		}
		
		
		
		return response;
	}
}

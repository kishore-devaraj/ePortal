package com.eportal.models;

import java.util.ArrayList;
import java.util.List;

import com.eportal.db.DatastoreWrapper;
import com.eportal.utils.Sha256Hex;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

public class Employee {
	private String employeeName;
	private String organisation;
	private String address;
	private String password;
	private String confirmPassword;
	private String employeeId;
	private String employeeKind = "Employee";
	private String addressKind = "Address";
	private ArrayList<Object> skillsets = new ArrayList<Object>();
		
	
	/* Getters and Setters */
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public ArrayList<Object> getSkillsets() {
		return skillsets;
	}	

	public void setSkillsets(ArrayList<Object> skillsets) {
		this.skillsets = skillsets;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	
	public Entity toEntity() throws Exception{
		Entity entity = new Entity(employeeKind,this.getEmployeeId());
		
		// First set the indexed property
		entity.setIndexedProperty("employeeId", this.getEmployeeId());
		entity.setIndexedProperty("employeeName", this.getEmployeeName());
		entity.setIndexedProperty("organisation",this.getOrganisation());
		
		if (!(this.getSkillsets().isEmpty())){
			entity.setIndexedProperty("skillsets",this.getSkillsets());
		}
		
		if (this.getAddress() != null){
			Entity addressEntity = new Entity(addressKind,this.getEmployeeId());
			addressEntity.setIndexedProperty("address", this.getAddress());
			entity.setUnindexedProperty("Address",addressEntity.getKey());
			
			// Specific to this scenarios
			DatastoreWrapper datastore = new DatastoreWrapper(this.getOrganisation());
			datastore.put(this.getEmployeeId(), addressEntity);			
		}
		// UnindexedProperty
		entity.setUnindexedProperty("password", Sha256Hex.hashPassword(password));
		
		return entity;
	}
	
	public Employee fromEntity(Entity entity) throws EntityNotFoundException {
		this.setEmployeeId((String) entity.getProperty("employeeId"));
		this.setEmployeeName((String) entity.getProperty("employeeName"));
		this.setOrganisation((String) entity.getProperty("organisation"));
		// this.setPassword((String) entity.getProperty("password"));
		
		if(entity.hasProperty("skillsets")){
			this.setSkillsets( (ArrayList<Object>) entity.getProperty("skillsets"));
		}
		
		if(entity.hasProperty("Address")){
			DatastoreWrapper datastore = new DatastoreWrapper(this.getOrganisation());
			Entity addressEntity;
			try{
				addressEntity = datastore.get("Address",(String) entity.getProperty("address"));
				this.setAddress((String) addressEntity.getProperty("address"));
			}catch(Exception e){
				
			}
		}
		return this;
	}
	
	public List<Employee> fromEntities(List<Entity> entities) throws Exception{
		List<Employee> listOfEmployees = new ArrayList<Employee>();
		for (Entity e: entities){
			listOfEmployees.add(this.fromEntity(e));
		}
		return listOfEmployees;
	}
	  
}

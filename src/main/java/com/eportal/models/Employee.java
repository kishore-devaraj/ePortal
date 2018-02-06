package com.eportal.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eportal.db.DatastoreWrapper;
import com.eportal.utils.Sha256Hex;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class Employee {
	private String employeeName;
	private String organisation;
	private String address;
	private String password;
	private String confirmPassword;
	private String employeeId;
	
	private final String employeeKind = "Employee";
	private final String addressKind = "Address";
	private final String skillsetsKind = "Skillsets";
	private Map<String,Object> skillset = new HashMap<String,Object>();
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
	
	public Map<String, Object> getSkillset() {
		return skillset;
	}

	public void setSkillset(Map<String, Object> skillset) {
		this.skillset = skillset;
	}
	
	public Entity toEntity() throws Exception{
		DatastoreWrapper datastore = new DatastoreWrapper(this.getOrganisation());
		Entity entity = new Entity(employeeKind,this.getEmployeeId());
		
		// First set the indexed property
		entity.setIndexedProperty("employeeId", this.getEmployeeId());
		entity.setIndexedProperty("employeeName", this.getEmployeeName());
		entity.setIndexedProperty("organisation",this.getOrganisation());
		
		if (!(this.getSkillsets().isEmpty())){
			//entity.setIndexedProperty("skillsets",this.getSkillsets());
			// List<Entity> entities = new ArrayList<Entity>();
			
			for(Object skillset: this.getSkillsets()){
				this.setSkillset((Map<String,Object>) skillset);
				Entity skillsetEntity = new Entity(this.skillsetsKind, this.getEmployeeId() + "_" + this.getSkillset().get("name"));
				skillsetEntity.setIndexedProperty("employeeId",this.getEmployeeId());
				skillsetEntity.setIndexedProperty("skillset", this.getSkillset().get("name"));
				skillsetEntity.setIndexedProperty("experience",this.getSkillset().get("experience"));
				datastore.put(this.getEmployeeId() + "_" + this.getSkillset().get("name"), skillsetEntity);
			}
		}
		
		if (this.getAddress() != null){
			Entity addressEntity = new Entity(addressKind,this.getEmployeeId());
			addressEntity.setIndexedProperty("address", this.getAddress());
			entity.setUnindexedProperty("Address",addressEntity.getKey());
			
			
			// Specific to this scenarios
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
		this.setSkillset(null);
		
		DatastoreWrapper datastore = new DatastoreWrapper(this.getOrganisation());

		// Query his skillsets
		Filter filter = new FilterPredicate("employeeId",FilterOperator.EQUAL, this.getEmployeeId());
		List<Entity> skillsetsEntities;
		try {
			skillsetsEntities = datastore.getByFitler(filter,skillsetsKind);

			ArrayList<Object> skillsetsList = new ArrayList<Object>();
			if (!(skillsetsEntities.isEmpty())){
				for(Entity ent: skillsetsEntities){
					Map<String,Object> tempMap = new HashMap<String,Object>();
					tempMap.put("name",ent.getProperty("skillset"));
					tempMap.put("experience", ent.getProperty("experience"));
					skillsetsList.add(tempMap);
				}
				this.setSkillsets(skillsetsList);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		if(entity.hasProperty("Address")){
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

package com.eportal.models;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private String password;
	private String confirmPassword;
	private String employeeId;
	
	private List<Skillsets> skillsets;
	private Address address;
	
	private final String employeeKind = "Employee";
	private final String addressKind = "Address";
	private final String skillsetsKind = "Skillsets";	
	private final static Logger logger = Logger.getLogger(Employee.class.getName()); 
	
	{
		logger.setLevel(Level.INFO);
	}
	
	
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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
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

	public List<Skillsets> getSkillsets() {
		return skillsets;
	}

	public void setSkillsets(List<Skillsets> skillsets) {
		this.skillsets = skillsets;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	
	public Entity toEntity() throws Exception{
		DatastoreWrapper datastore = new DatastoreWrapper(this.getOrganisation());
		Entity entity = new Entity(employeeKind,this.getEmployeeId());
		
		// First set the indexed property
		entity.setIndexedProperty("employeeId", this.getEmployeeId());
		entity.setIndexedProperty("employeeName", this.getEmployeeName());
		entity.setIndexedProperty("organisation",this.getOrganisation());
		
		try{
			if (!(this.getSkillsets().isEmpty())){
				for(Skillsets skillset: this.getSkillsets()){
					skillset = skillset.mapEmployeetoSkillset(this, skillset);
					Entity skillsetEntity = skillset.toEntity();
					String id = this.getEmployeeId() + "_" + skillsetEntity.getProperty("skillset");
					datastore.put(id,skillsetEntity);
				}
			}
		}catch(NullPointerException e){
			// Pass
		}
		
		// Setting Address
		
		if (this.getAddress() != null){
			try{
				 Entity addrEntity = this.getAddress().toEntity();
				 String id  = this.getAddress().getEmployeeId() + "_addr";
				 datastore.put(id,addrEntity);
				 
			}catch(Exception e){
				e.printStackTrace();
				logger.warning("Exception occured while updating address Entity from Employee");
				logger.info("Exception name " + e);
			}
		}
		
		// UnindexedProperty
		entity.setUnindexedProperty("password", Sha256Hex.hashPassword(password));
		
		return entity;
	}

	public Employee fromEntity(Entity entity) throws EntityNotFoundException {

		this.setEmployeeId((String) entity.getProperty("employeeId"));
		this.setEmployeeName((String) entity.getProperty("employeeName"));
		this.setOrganisation((String) entity.getProperty("organisation"));
		this.skillsets = new ArrayList<Skillsets>();
		
		DatastoreWrapper datastore = new DatastoreWrapper(this.getOrganisation());
		
		// Query his skillsets
		Filter filter = new FilterPredicate("employeeId",FilterOperator.EQUAL, entity.getProperty("employeeId"));
		List<Entity> skillsetsEntities;
		
		try {
			skillsetsEntities = datastore.getByFitler(filter,skillsetsKind);
			if (!(skillsetsEntities.isEmpty())){
				for(Entity e: skillsetsEntities){
					 Skillsets skillset = new Skillsets();
					 skillset = skillset.fromEntity(e).toResponse();
					 this.skillsets.add(skillset);
				}
			}else{
				this.setSkillsets(null);
			}
		} catch (Exception e1) {
			System.out.println(e1);
			e1.printStackTrace();
		}
		
		
		try{
			try	{
				Entity addressEntity = datastore.get(addressKind,((String) entity.getProperty("employeeId") + "_addr"));
				Address address = new Address();
				address = address.fromEntity(addressEntity);
				this.setAddress(address);
			}catch(EntityNotFoundException e){
				logger.info("No Address Detail found for employee " + this.getEmployeeId());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return this;
	}
	
	public List<Employee> fromEntities(List<Entity> entities) throws Exception{
		List<Employee> listOfEmployees = new ArrayList<Employee>();
		for (Entity e: entities){
			Employee employee = new Employee();
			listOfEmployees.add(employee.fromEntity(e));
		}
		return listOfEmployees;
	}
	  
}

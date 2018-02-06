package com.eportal.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Entity;

public class Skillsets {
	private String EmployeeName;
	
	// Foreign Key
	private String EmployeeId;
	private String organisation;
	private Map<String,Object> skillset = new HashMap <String,Object>();
	private List<Object> skillsets = new ArrayList<Object>();
	
	private final String skillsetsKind = "Skillsets";
	
	
	public String getEmployeeName() {
		return EmployeeName;
	}
	public void setEmployeeName(String employeeName) {
		EmployeeName = employeeName;
	}
	public String getEmployeeId() {
		return EmployeeId;
	}
	public void setEmployeeId(String employeeId) {
		EmployeeId = employeeId;
	}
	public String getOrganisation() {
		return organisation;
	}
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	public Map<String, Object> getSkillset() {
		return skillset;
	}
	public void setSkillset(Map<String, Object> skillset) {
		this.skillset = skillset;
	}
	public List<Object> getSkillsets() {
		return skillsets;
	}
	public void setSkillsets(List<Object> skillsets) {
		this.skillsets = skillsets;
	}
	
	public List<Entity> toEntity(){
		List<Entity> entities = new ArrayList<Entity>();
		
		for(Object skillset: this.getSkillsets()){
			this.setSkillset((Map<String,Object>) skillset);
			Entity entity = new Entity(this.skillsetsKind, this.getEmployeeId() + "_" + this.getSkillset().get("name"));
			entity.setIndexedProperty("employeeId",this.getEmployeeId());
			entity.setIndexedProperty("skillset", this.getSkillset().get("name"));
			entity.setIndexedProperty("experience",this.getSkillset().get("experience"));
			entities.add(entity);
		}
		
		return entities;
	}
	
}

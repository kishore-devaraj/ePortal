package com.eportal.models;

import com.eportal.models.request.SkillsetsRequest;
import com.google.appengine.api.datastore.Entity;

public class Skillsets {
	
	// Foreign Key
	private String EmployeeId;
	private String EmployeeName;
	private String organisation;
	private String skillset;
	private long experience;
	
	private final String skillsetsKind = "Skillsets";

	public String getEmployeeId() {
		return EmployeeId;
	}

	public void setEmployeeId(String employeeId) {
		EmployeeId = employeeId;
	}

	public String getEmployeeName() {
		return EmployeeName;
	}

	public void setEmployeeName(String employeeName) {
		EmployeeName = employeeName;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getSkillset() {
		return skillset;
	}

	public void setSkillset(String skillset) {
		this.skillset = skillset;
	}

	public long getExperience() {
		return experience;
	}

	public void setExperience(long experience) {
		this.experience = experience;
	}
	
	public Entity toEntity(){
		Entity skillsetEntity = new Entity(this.skillsetsKind, this.getEmployeeId() + "_" + this.getSkillset());
		skillsetEntity.setIndexedProperty("employeeId",this.getEmployeeId());
		skillsetEntity.setIndexedProperty("skillset", this.getSkillset());
		skillsetEntity.setIndexedProperty("experience",this.getExperience());	
		return skillsetEntity;
	}
	
	public Skillsets fromEntity(Entity entity){
		this.setEmployeeId((String) entity.getProperty("employeeId"));
		this.setEmployeeName((String) entity.getProperty("employeeName"));
		this.setOrganisation((String) entity.getProperty("organisation"));
		this.setExperience(Long.valueOf((long) entity.getProperty("experience")));
		this.setSkillset((String) entity.getProperty("skillset"));
		return this;
	}
	
	public Skillsets mapEmployeetoSkillset(Employee employee,Skillsets skillset){
		skillset.setEmployeeId(employee.getEmployeeId());
		skillset.setEmployeeName(employee.getEmployeeName());
		skillset.setOrganisation(employee.getOrganisation());
		return skillset;
	}
	
	public SkillsetsRequest toSkillsetsRequest(){
		SkillsetsRequest skillsetRequest = new SkillsetsRequest();
		skillsetRequest.setName(this.getSkillset());
		skillsetRequest.setExperience(this.getExperience());
		return skillsetRequest;
	}
	
	public Skillsets toResponse(){
		this.setEmployeeId(null);
		return this;
	}

	
}

package com.eportal.models;

import java.util.ArrayList;

public class Employee {
	private String employeeName;
	private String organisation;
	private String address;
	private String password;
	private String confirmPassword;
	private String employeeId;
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
	
}

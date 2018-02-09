package com.eportal.models.request;

import java.util.ArrayList;
import java.util.List;

import com.eportal.models.Address;
import com.eportal.models.Employee;
import com.eportal.models.Skillsets;

public class EmployeeRequest {
	private String employeeName;
	private String organisation;
	private String password;
	private String confirmPassword;
	private String employeeId;
	
	private List<SkillsetsRequest> skillsets;
	private AddressRequest address;
	
	
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
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public List<SkillsetsRequest> getSkillsets() {
		return skillsets;
	}
	public void setSkillsets(List<SkillsetsRequest> skillsets) {
		this.skillsets = skillsets;
	}
	public AddressRequest getAddress() {
		return address;
	}
	public void setAddress(AddressRequest address) {
		this.address = address;
	}
	
	public Employee mapToEmployee(){
		Employee employee = new Employee();
		employee.setEmployeeId(this.getEmployeeId());
		employee.setEmployeeName(this.getEmployeeName());
		employee.setOrganisation(this.getOrganisation());	
		
		if(this.getPassword() != null){
			employee.setPassword(this.getPassword());
		}
		
		if(this.getConfirmPassword() != null){
			employee.setConfirmPassword(this.getConfirmPassword());
		}
		
		try{
			if(this.getAddress() != null){
				Address address = new Address();
				address = this.getAddress().MapToAddress();
				address.setEmployeeId(this.getEmployeeId());
				employee.setAddress(address);
			}
		}catch(NullPointerException e){
			employee.setAddress(null);
			// No Address was given
		}
		
		try{
			if(!(this.getSkillsets().isEmpty())){
				List<Skillsets> skillsets = new ArrayList<Skillsets>();
				for(SkillsetsRequest skill: this.getSkillsets()){
					Skillsets skillset = new Skillsets();
					skillset = skill.toSkillsets();
					skillset.setEmployeeId(this.getEmployeeId());
					skillset.setEmployeeName(this.getEmployeeName());
					skillset.setOrganisation(this.getOrganisation());
					skillsets.add(skillset);
				}
				employee.setSkillsets(skillsets);
			}
		}catch(NullPointerException e){
			employee.setSkillsets(null);
			// No Skillset was given
		}
		return employee;
		
	}
}

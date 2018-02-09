package com.eportal.models;

import com.google.appengine.api.datastore.Entity;

public class Address {
	private String location;
	private String city;
	private String EmployeeId;
	private long pincode;
	
	private final static String addressKind = "Address";

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmployeeId() {
		return EmployeeId;
	}

	public void setEmployeeId(String employeeId) {
		EmployeeId = employeeId;
	}

	public long getPincode() {
		return pincode;
	}

	public void setPincode(long pincode) {
		this.pincode = pincode;
	}

	public Entity toEntity(){
		Entity entity = new Entity(addressKind,this.getEmployeeId() + "_addr");
		entity.setIndexedProperty("employeeId",this.getEmployeeId());
		entity.setIndexedProperty("pincode",this.getPincode());
		entity.setIndexedProperty("location", this.getLocation());
		entity.setIndexedProperty("city",this.getCity());
		return entity;
	}
	
	public Address fromEntity(Entity entity){
		Address address = new Address();
		address.setCity((String) entity.getProperty("city"));
		address.setPincode((long) entity.getProperty("pincode"));
		address.setLocation((String) entity.getProperty("location"));
		address.setEmployeeId((String) entity.getProperty("employeeId"));
		return address;
	}
}

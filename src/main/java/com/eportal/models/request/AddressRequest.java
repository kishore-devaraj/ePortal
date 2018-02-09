package com.eportal.models.request;

import com.eportal.models.Address;
import com.google.appengine.api.datastore.Entity;

public class AddressRequest {
	private String location;
	private String city;
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

	public long getPincode() {
		return pincode;
	}

	public void setPincode(long pincode) {
		this.pincode = pincode;
	}
	
	public Address MapToAddress(){
		Address address = new Address();
		address.setCity(this.getCity());
		address.setLocation(this.getLocation());
		address.setPincode(this.getPincode());
		return address;
	}
}

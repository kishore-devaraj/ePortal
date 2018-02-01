package com.eportal.utils;

import java.util.ArrayList;

import com.eportal.models.EmployeeResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class EmployeeDetailsBuilder {
	private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public static EmployeeResponse build(Entity entity){
		EmployeeResponse employeeResponse = new EmployeeResponse();
		try{
			employeeResponse.setEmployeeId((String) entity.getProperty("employeeId"));
			employeeResponse.setEmployeeName((String) entity.getProperty("employeeName"));
			employeeResponse.setOrganisation((String) entity.getProperty("organisation"));
		
			// Get the skillsets 
			ArrayList<Object> skillsets = new ArrayList<Object>();
			try{
				skillsets = (ArrayList<Object>) entity.getProperty("skillsets"); 
				employeeResponse.setSkillsets(skillsets);
			}catch(Exception ee){
				employeeResponse.setSkillsets(null);
			}
			
			// Get the Address
			Key addressKey = (Key) entity.getProperty("Address");
			if (addressKey != null){
				Entity addressEntity;
				try {
					addressEntity = datastore.get(addressKey);
					employeeResponse.setAddress((String) addressEntity.getProperty("address"));
				} catch (EntityNotFoundException e) {
					System.out.println(e);
					e.printStackTrace();
					employeeResponse.setAddress(null);
				}
			}else{
				employeeResponse.setAddress(null);
			}
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		return employeeResponse;
	}
}

package com.eportal.handler;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.eportal.models.Employee;
import com.eportal.service.UpdateResource;
import com.eportal.utils.GenericResponse;

@Path("/update")
public class UpdateEmployee {
	
	@PUT
	@Path("/employee")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse updateEmployee(Employee employee){
		GenericResponse response = new GenericResponse();
		UpdateResource update = new UpdateResource();
		
		if ((employee.getOrganisation() !=  null) && 
			(employee.getEmployeeId() != null)){
			response = update.Employee(employee, response);
	
		}else{
			response.setCode(400);
			response.setData("error","EmployeeId and Organisation is must");
		}
		return response;
	}
	
}

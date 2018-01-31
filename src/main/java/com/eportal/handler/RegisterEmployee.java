package com.eportal.handler;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.eportal.models.Employee;
import com.eportal.service.CreateResource;
import com.eportal.utils.GenericResponse;

@Path("/create")
public class RegisterEmployee {
	
	@POST
	@Path("/employee")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse createEmployee(Employee employee){
		GenericResponse response = new GenericResponse();
		try{
			// Checking the mandatory field to create a employee
			if (employee.getEmployeeName() != null &&
				employee.getOrganisation() != null &&
				employee.getEmployeeId()!= null &&
				employee.getPassword() != null &&
				employee.getConfirmPassword() != null)
			{	
				CreateResource createResource = new CreateResource();
				response = createResource.Employee(employee,response);
			} else {
				response.setCode(400);
				response.setData("error","Please fill up all the required details");
			}
		}catch(Exception e){
			e.printStackTrace();
			response.setCode(400);
			response.setData("error", "Error happened in processing the data");
		}
		
		return response;
	}
}

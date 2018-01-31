package com.eportal.handler;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.eportal.models.Employee;
import com.eportal.service.CreateResource;
import com.eportal.service.DeleteResource;
import com.eportal.service.UpdateResource;
import com.eportal.utils.GenericResponse;

@Path("/")
public class Employeehandler {
	
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
	
	
	
	@DELETE
	@Path("/{org}/employee/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse deleteEmployee(
			@PathParam("id") String id,
			@PathParam("org") String org
			){
		GenericResponse response = new GenericResponse();
		if (id != null){
			DeleteResource delete = new DeleteResource();
			response = delete.Employee(org,id,response);
		}else{
			response.setCode(400);
			response.setData("error", "Specify in the Employee Id in the path param");
		}
		return response;	
	}
	
	
	
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

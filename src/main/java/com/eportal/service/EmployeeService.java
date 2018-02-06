package com.eportal.service;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.eportal.handler.EmployeeHandler;
import com.eportal.models.Employee;
import com.eportal.models.Skillsets;
import com.eportal.utils.GenericResponse;

@Path("/")
public class EmployeeService {
	private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
	
	
	{
		LOGGER.setLevel(Level.INFO);
	}
	
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
				if(!(employee.getSkillsets().isEmpty())){
					try{
						for(Object skillset : employee.getSkillsets()){
							employee.setSkillset((Map<String,Object>) skillset);
						}
					}catch(Exception e){
						response.setCode(400);
						response.setData("error","Please check whether skillsets field contains a list of dictionary contains skillsets and experience");
						return response;
					}
				}
					
				EmployeeHandler employeeHandler = new EmployeeHandler();
				response = employeeHandler.create(employee, response);
				
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
			EmployeeHandler employeeHandler = new EmployeeHandler();
			response = employeeHandler.delete(org,id,response);
			
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
		
		if ((employee.getOrganisation() !=  null) && 
			(employee.getEmployeeId() != null)){
			
			if(!(employee.getSkillsets().isEmpty())){
				try{
					for(Object skillset : employee.getSkillsets()){
						employee.setSkillset((Map<String,Object>) skillset);
					}
				}catch(Exception e){
					response.setCode(400);
					response.setData("error","Please check whether skillsets field contains a list of dictionary contains skillsets and experience");
					return response;
				}
			}
			
			EmployeeHandler employeeHandler = new EmployeeHandler();
			response = employeeHandler.update(employee,response);
		}else{
			response.setCode(400);
			response.setData("error","EmployeeId and Organisation is must");
		}
		return response;
	}
	
	
	
	
	@PUT
	@Path("/employee/skillsets")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse updateSkillsets(Skillsets skillsets){
		GenericResponse response = new GenericResponse();
		try{
			if((skillsets.getEmployeeId() != null) &&
					(skillsets.getEmployeeName() != null) && 
					(skillsets.getOrganisation() != null) && 
					(skillsets.getSkillsets() != null)
					){
			
				if(skillsets.getSkillsets().isEmpty()){
					response.setCode(400);
					response.setData("error", "Skillsets List is empty");
				}else{
					try{
						for(Object skillset : skillsets.getSkillsets()){
							skillsets.setSkillset((Map<String,Object>) skillset);
						}
						EmployeeHandler employeeHandler = new EmployeeHandler();
						response = employeeHandler.updateSkillsetsAndExperience(skillsets, response);
						
					}catch(Exception e){
						e.printStackTrace();
						response.setCode(400);
						response.setData("error","Please check whether skillsets field contains a list of dictionary contains skillsets and experience");
						return response;
					}
					
				}
				
			}else{
				response.setCode(400);
				response.setData("error","Please give all the neccessary details");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			response.setCode(400);
			response.setData("error","Exception occured while updating employee skillsets");
		}
		
		return response;
	}
	
	
	@GET
	@Path("/{orgid}/employee")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse getAllEmployees(@PathParam("orgid") String orgid){
		GenericResponse response = new GenericResponse();
		try{
			if (orgid != null){
				EmployeeHandler employeeHandler = new EmployeeHandler();
				response = employeeHandler.getAll(orgid,response);
			}else{
				response.setCode(400);
				response.setData("error","Organisation Id is missing");
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e);
			response.setCode(400);
			response.setData("error","Error while processing the request");
		}
		return response;
	}
	
	@GET
	@Path("/{org}/employee/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse getEmployeeById(
			@PathParam("org") String org,
			@PathParam("id") String id){
		GenericResponse response = new GenericResponse();
		
		try{
			EmployeeHandler employeeHandler = new EmployeeHandler();
			response = employeeHandler.getById(org,id,response);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			response.setCode(400);
			response.setData("error","Error happened while retriving the Employee");
		}
		return response;
	}
	
	
//	@GET
//	@Path("/{org}/employees/")
//	@Produces(MediaType.APPLICATION_JSON)
//	public GenericResponse getEmployeeBySkillsets(
//			@PathParam("org") String org,
//			@QueryParam("skillsets") String querySkillsets
//			){
//			GenericResponse response = new GenericResponse();
//			String skillsets[] = querySkillsets.split(",");
//			try{
//				if (!(querySkillsets.isEmpty())){
//					EmployeeHandler employeeHandler = new EmployeeHandler();
//					response = employeeHandler.getBySkill(org,skillsets,response);
//					
//				}else{
//					response.setCode(400);
//					response.setData("error","No query param found");
//				}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			return response;
//			}
//	
	@GET
	@Path("{org}/employees")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse getEmployeeBySkillsetsAndExperience(
			@PathParam("org") String organisation,
			@QueryParam("skillsets") String querySkillsets,
			@QueryParam("experience") int experience
			){
		GenericResponse response = new GenericResponse();
		EmployeeHandler employeeHandler = new EmployeeHandler();
		
		String skillsets[] = querySkillsets.split(",");
		
		try{
			if ((skillsets != null) && (experience == 0)){
				response = employeeHandler.getBySkill(organisation,skillsets,response);
			}else if ((skillsets != null) && (experience != 0)){
				response = employeeHandler.getEmployeeBySkillsetsAndExperience(organisation,skillsets,experience,response);
			}
		}catch(Exception e){
			e.printStackTrace();
			response.setCode(400);
			response.setData("error", "Exception occured while retrieving employees");
		}
		
		return response;
	}
	
}

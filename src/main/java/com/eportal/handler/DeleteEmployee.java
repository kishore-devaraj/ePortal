package com.eportal.handler;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.eportal.service.DeleteResource;
import com.eportal.utils.GenericResponse;

@Path("/delete")
public class DeleteEmployee {
	
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
}


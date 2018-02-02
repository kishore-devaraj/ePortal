package com.eportal.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.eportal.handler.EmployeeSigninHandler;
import com.eportal.models.SignInModel;
import com.eportal.utils.GenericResponse;

@Path("/signin")
public class SignInEmployee {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse signin(SignInModel signInModel){
		GenericResponse response = new GenericResponse();
		try{
			EmployeeSigninHandler signInService = new EmployeeSigninHandler();
			response = signInService.auth(signInModel, response);
		}catch(Exception e){
			response.setCode(400);
			response.setData("error","Error happened while sign in");
		}
		return response;
	}
}

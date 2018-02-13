package com.eportal.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.eportal.handler.EmployeeDataStorageHandler;
import com.eportal.utils.GenericResponse;

@Path("/")
public class EmployeeDataStorageService {
	
	@Context
	HttpServletRequest req;
	
	@Context
	HttpServletResponse resp;
	
	@Context
	ServletContext servletContext;
	
	
	@POST
	@Path("/{org}/employee/{id}/{category}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public GenericResponse storeData(
			@PathParam("org") String org,
			@PathParam("id") String id,
			@PathParam("category") String category
			){
		GenericResponse response = new GenericResponse();
		
		try{
			EmployeeDataStorageHandler storage = new EmployeeDataStorageHandler();
			String filename = org + "/" + id + "/" + category;
			response = storage.uploadEmployeeData(req, response, filename);
		}catch(Exception e){
			response.setCode(400);
			response.setData("error","Exception occurred while uploading the data");
		}
		return response;
	}
	
	@GET
	@Path("/{org}/employee/{id}/{category}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public GenericResponse getData(
			@QueryParam("download") boolean download,
			@PathParam("org") String org,
			@PathParam("id") String id,
			@PathParam("category") String category
			){
		GenericResponse response = new GenericResponse();
		try{
			EmployeeDataStorageHandler storage = new EmployeeDataStorageHandler();
			String filename = org + "/" + id + "/" + category;
			if(download==true){
				response = storage.downloadEmployeeData(resp, response, filename);
		        System.out.println("downloading");
			}else{
				System.out.println("Just displaying");
				response = storage.displayEmployeeData(resp,response, filename);
			}
		}catch(Exception e){
			response.setCode(400);
			response.setData("error", "Exception occured while downloading the data");
		}
		
		return response;
	}
}

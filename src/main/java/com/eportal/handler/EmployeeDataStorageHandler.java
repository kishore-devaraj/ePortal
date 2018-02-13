package com.eportal.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eportal.storage.GCSWrapper;
import com.eportal.utils.GenericResponse;

public class EmployeeDataStorageHandler {
	private final static Logger logger = Logger.getLogger(EmployeeDataStorageHandler.class.getName());
	private final static String BUCKET_NAME = "employeeBucket";
	
	{
		logger.setLevel(Level.INFO);
	}
	
	public GenericResponse uploadEmployeeData(HttpServletRequest req,GenericResponse response, String filename){
		GCSWrapper gcs = new GCSWrapper();	
		try {
			gcs.upload(req, BUCKET_NAME, filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	public GenericResponse displayEmployeeData(HttpServletResponse resp, GenericResponse response, String filename){
		GCSWrapper gcs = new GCSWrapper();
		try{
			gcs.display(resp, BUCKET_NAME, filename);
		}catch(Exception e){
			e.printStackTrace();
		}
		return response;
	}
	
	
	public GenericResponse downloadEmployeeData(HttpServletResponse resp, GenericResponse response, String filename){
		GCSWrapper gcs = new GCSWrapper();
		try{
			gcs.download(resp, BUCKET_NAME, filename);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return response;
	}
	
}

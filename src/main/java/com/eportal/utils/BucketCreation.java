package com.eportal.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

@Path("bucket")
@MultipartConfig(
		  maxFileSize = 10 * 1024 * 1024, // max size for uploaded files
		  maxRequestSize = 20 * 1024 * 1024, // max size for multipart/form-data
		  fileSizeThreshold = 5 * 1024 * 1024 // start writing to Cloud Storage after 5MB
		)
public class BucketCreation {
	
	private final GcsService gcsService =
		    GcsServiceFactory.createGcsService(
		        new RetryParams.Builder()
		            .initialRetryDelayMillis(10)
		            .retryMaxAttempts(10)
		            .totalRetryPeriodMillis(15000)
		            .build());
	
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;

	
	@Context
	HttpServletRequest req;
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse bucketTesting()
	{	
		  GenericResponse response = new GenericResponse();
		  GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
		  GcsFilename fileName = new GcsFilename("bucket1","object1");
		  GcsOutputChannel outputChannel;
		  
		  try {
			outputChannel = gcsService.createOrReplace(fileName, instance);
			copy(req.getInputStream(),Channels.newOutputStream(outputChannel));
			response.setCode(200);
			response.setData("message","file upload successful");
		  	} catch (IOException e) {
			response.setCode(400);
			response.setData("error","Error in uploading the file");
		  	}
		
		return response;
	}
	
	  private void copy(InputStream input, OutputStream output) throws IOException {
		    try {
		      byte[] buffer = new byte[BUFFER_SIZE];
		      int bytesRead = input.read(buffer);
		      while (bytesRead != -1) {
		        output.write(buffer, 0, bytesRead);
		        bytesRead = input.read(buffer);
		      }
		    } finally {
		      input.close();
		      output.close();
		    }
		  }
	
}

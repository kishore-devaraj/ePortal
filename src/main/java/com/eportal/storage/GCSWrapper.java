package com.eportal.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

@MultipartConfig(
		  maxFileSize = 10 * 1024 * 1024, // max size for uploaded files
		  maxRequestSize = 20 * 1024 * 1024, // max size for multipart/form-data
		  fileSizeThreshold = 5 * 1024 * 1024 // start writing to Cloud Storage after 5MB
		)
public class GCSWrapper {
	
	// Defining the service 
	private final GcsService gcsService =
		    GcsServiceFactory.createGcsService(
		        new RetryParams.Builder()
		            .initialRetryDelayMillis(10)
		            .retryMaxAttempts(10)
		            .totalRetryPeriodMillis(15000)
		            .build());
		
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;
	private static final boolean SERVE_USING_BLOBSTORE_API = false;
	private GcsFileOptions instance;
	private GcsOutputChannel outputChannel;
	private BlobstoreService blobstoreService;
	
	
	// Get the GcsFile instance
	public GCSWrapper(){
		instance = GcsFileOptions.getDefaultInstance();
		blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	}
	
	
	
	
	// Uploading new data
	public void upload(HttpServletRequest req, String bucketName, String objectName) throws IOException, Exception {
		GcsFilename fileName = new GcsFilename(bucketName,objectName);
		
		// System.out.println(printNames(req));
		this.outputChannel = this.gcsService.createOrReplace(fileName, this.instance);
		this.copy(req.getInputStream(),Channels.newOutputStream(outputChannel));
	}
	
	
	
	
	// Retrieving the data
	public void download(HttpServletResponse resp, String bucketName, String objectName) throws IOException, Exception{
		GcsFilename fileName = new GcsFilename(bucketName,objectName);
		 
	  if (SERVE_USING_BLOBSTORE_API) {
	    BlobKey blobKey = blobstoreService.createGsBlobKey(
	        "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName());
	    	 blobstoreService.serve(blobKey, resp);
	  } else {
	    GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
	    // resp.setContentType("APPLICATION/OCTET-STREAM");
    	resp.setHeader("Content-Disposition","attachment; filename=\"" + objectName + "\"");
	    copy(Channels.newInputStream(readChannel), resp.getOutputStream());
	  }
	}
	
	
	// Retrieving the data
		public void display(HttpServletResponse resp, String bucketName, String objectName) throws IOException, Exception{
			GcsFilename fileName = new GcsFilename(bucketName,objectName);
			 
		  if (SERVE_USING_BLOBSTORE_API) {
		    BlobKey blobKey = blobstoreService.createGsBlobKey(
		        "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName());
		    	blobstoreService.serve(blobKey, resp);
		  } else {
		    GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
		    copy(Channels.newInputStream(readChannel), resp.getOutputStream());
		  }
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

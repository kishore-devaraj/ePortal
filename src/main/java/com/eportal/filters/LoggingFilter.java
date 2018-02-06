package com.eportal.filters;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class LoggingFilter implements Filter {
	private FilterConfig filterConfig;
	// private final static Logger logger = Logger.getLogger(LoggingFilter.class.getName());
	
//	static{
//		logger.setLevel(Level.INFO);
//	}
	
	public void init(final FilterConfig filterConfig){
		this.filterConfig = filterConfig;
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		System.out.println("We got incomings");
		chain.doFilter(request, response);
		System.out.println("Blast them off");
	}
	
	@Override
	public void destroy(){
		filterConfig = null;
	}
}

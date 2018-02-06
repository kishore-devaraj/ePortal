package com.eportal.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class TimerFilter implements Filter {
	private FilterConfig filterConfig;
	
	public void init(final FilterConfig filterConfig){
		this.filterConfig = filterConfig;
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
		long start = System.currentTimeMillis();
		System.out.println("Starting milliseconds " + start);
		chain.doFilter(request, response);
		long end = System.currentTimeMillis();
		System.out.println("Ending milliseconds " + end);
	}
	
	public void destroy(){
		filterConfig = null;
	}
	
}

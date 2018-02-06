package com.eportal.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CustomFilterChain implements Filter{
	List<Filter> filter = new ArrayList<Filter>();
	private FilterConfig filterConfig;
	int index = 0;
	
	public void init(FilterConfig config){
		this.filterConfig = config;
	}
	
	public CustomFilterChain addFilter(Filter f){
		filter.add(f);
		return this;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
		if(index==filter.size()) return;
		Filter f = filter.get(index);
		index++;
		f.doFilter(request, response, chain);
	}
	public void destroy(){
		
	}
}
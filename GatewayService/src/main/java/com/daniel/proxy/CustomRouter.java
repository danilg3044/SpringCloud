package com.daniel.proxy;

import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

public class CustomRouter extends SimpleRouteLocator {
	public CustomRouter(String servletPath, ZuulProperties properties) {
		super(servletPath, properties);
	}
	
	@Override
    protected ZuulRoute getZuulRoute(String adjustedPath) {
		//String bestMatchingPattern = (String) ctx.getRequest().getAttribute(HandlerMapping.class.getName() + ".bestMatchingPattern");
        //ZuulRoute route = locateRoutes().get(bestMatchingPattern);
		//return route;
		return super.getZuulRoute(adjustedPath);  
    }
}

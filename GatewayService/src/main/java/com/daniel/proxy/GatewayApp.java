package com.daniel.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.daniel.proxy.filters.ErrorFilter;
import com.daniel.proxy.filters.PostFilter;
import com.daniel.proxy.filters.PreFilter;
import com.daniel.proxy.filters.RouteFilter;
import com.daniel.proxy.swagger.SwaggerConfig;

// http://localhost:8080/ - index.html
// http://localhost:8080/swagger-ui.html
// http://localhost:8080/student/v2/api-docs
// http://localhost:8080/course/v2/api-docs

@SpringBootApplication
@EnableZuulProxy
@Import(SwaggerConfig.class)
public class GatewayApp {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApp.class, args);
	}

	@Bean
    public CustomRouter customRouter(@Autowired ZuulProperties zuulProperties, @Autowired ServerProperties server) {
        return new CustomRouter(server.getServletPrefix(), zuulProperties);
    }
	
	@Bean
	public PreFilter preFilter() {
		return new PreFilter();
	}
	
	@Bean
	public PostFilter postFilter() {
		return new PostFilter();
	}
	
	@Bean
	public ErrorFilter errorFilter() {
		return new ErrorFilter();
	}
	
	@Bean
	public RouteFilter routeFilter() {
		return new RouteFilter();
	}
	
	@Bean
    public FallbackProvider fallbackProvider() {
        return new MyFallbackProvider();
    }	
}

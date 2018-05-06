package com.daniel.proxy.swagger;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

//@Component
//@Primary
//@EnableAutoConfiguration
public class UISwaggerProvider implements SwaggerResourcesProvider {
 
	@Autowired
    private SwaggerManager sm;
	
    @Override
    public List<SwaggerResource> get() {
    	return sm.getResources();
    }
}

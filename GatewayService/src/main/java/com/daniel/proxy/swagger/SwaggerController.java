package com.daniel.proxy.swagger;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.models.Path;
import io.swagger.models.Swagger;

// http://localhost:8080/v2/api-docs
// http://localhost:8080/swagger-ui.html

@RestController
public class SwaggerController {

	@Autowired
    private SwaggerManager sm;
	
	static final public String URL_SWAGGER = "/apigw-swagger";
	
	// http://localhost:8080/apigw-swagger/student
	@RequestMapping(value = URL_SWAGGER + "/{id:.+}", method = RequestMethod.GET, produces = "application/json")
    public Object getSwagger(@PathVariable("id") String id, HttpServletRequest req, HttpServletResponse resp) {
        UISwaggerRes uiSwaggerRes = sm.getResource(id.toLowerCase()).orElse(null);
        
        if (uiSwaggerRes == null) {
            resp.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        URI uri = SwaggerManager.toUri(uiSwaggerRes.getActualLocation());
        URI url = SwaggerManager.getServerUri(uri);
  
        // keep in mind that we could be running behind reverse proxy (e.g. nginx) so correct server name and port resolution
        // relies on processing X-ForwardedFor* headers. See 'server.use-forward-headers: true' property
        // and https://github.com/spring-projects/spring-boot/blob/2c8eaac78cd2d494e761493313f49a00103529b3/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/ServerProperties.java#L795
        //String myName = req.getServerName()+":"+req.getServerPort();
        return getSwagger(url);
    }
	
	private Swagger getSwagger(URI url) {
		Swagger swagger = sm.readSwagger(url).get();
        swagger.setHost(null);
        Map<String, Path> paths = swagger.getPaths();
        
        if (paths != null) {
            Map<String, Path> newPath = new LinkedHashMap<>(paths.size());

            for(Map.Entry<String, Path> me: paths.entrySet()) {
                // change path to route's URL
                String swaggerPath = me.getKey();
                String path = sm.getMappedPath(swaggerPath).orElse(null);
               
                if (path != null) {
                    newPath.put("/" + path + swaggerPath, me.getValue());
                } else {
                    newPath.put(swaggerPath, me.getValue());
                }
            }
            
            swagger.setPaths(newPath);
        }
        
        return swagger;
    }
}

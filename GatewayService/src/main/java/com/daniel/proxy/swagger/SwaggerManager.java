package com.daniel.proxy.swagger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;

import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import springfox.documentation.swagger.web.SwaggerResource;

public class SwaggerManager {

	private final SwaggerParser swaggerParser = new SwaggerParser();
	private Map<String, UISwaggerRes> resources;
	private Map<String, Swagger> swaggers;
	//private Set<URI> hosts;
	
	@Autowired
    private RouteLocator routeLocator;
	
	/**
     * @param uri of the service which exposes swagger. "/v2/api-docs" is added automatically
     */
    public Optional<Swagger> readSwagger(URI uri) {
        if (!MicroService.isServerListening(uri)) {
            return Optional.empty();
        }

        return Optional.ofNullable(swaggerParser.read(uri + "/v2/api-docs"));
    }
    
	private UISwaggerRes fromSwagger(String id, URI uri, Route route, @Nullable Swagger swagger) {
        //String id = uri.getHost() + "_" + MicroService.getPort(uri).orElse(0);
        String version = "UNKNOWN";

        if (swagger != null && swagger.getInfo() != null) {
            Info info = swagger.getInfo();
            /*if (info.getTitle() != null && !info.getTitle().equals("${project.name}")) {
                id = info.getTitle();
            }*/
            if (info.getVersion() != null && !info.getVersion().equals("${project.version}")) {
                version = info.getVersion();
            }
        }

        String logicalLocation = SwaggerController.URL_SWAGGER + "/" + id;
        return new UISwaggerRes(id, logicalLocation, version, route.getLocation());
    }
	
	private void loadResources() {
		Map<String, UISwaggerRes> _resourses = new LinkedHashMap<>();
		Map<String, Swagger> _swaggers = new LinkedHashMap<>();
        Set<URI> seenHosts = new HashSet<>();
        
        for(Route route: routeLocator.getRoutes()) {
        	URI uri = toUri(route.getLocation());
            URI host = getServerUri(uri);
            
            if (seenHosts.add(host)) {
            	String id  = route.getId();
                Swagger swagger = readSwagger(host).orElse(null);
                UISwaggerRes swaggerResource = fromSwagger(id, uri, route, swagger);
                _swaggers.put(id, swagger);
                _resourses.put(id, swaggerResource);
            }
        }

        swaggers = Collections.unmodifiableMap(_swaggers);
        //hosts = Collections.unmodifiableSet(seenHosts);
        resources = Collections.unmodifiableMap(_resourses);
    }

    private synchronized void loadResourcesIfNeeded() {
        if (resources == null) {
            loadResources();
        }
    }
    
    public Optional<String> getMappedPath(String endpoint) {
    	Map.Entry<String, Swagger> entry = swaggers.entrySet().stream().filter(e -> {
    		return e.getValue().getPaths().entrySet().stream().anyMatch(e1 -> {
    			return e1.getKey().equals(endpoint);
    		});
    	}).findFirst().get();
    	
    	if(entry != null) {
    		return Optional.of(entry.getKey());
    	}
    	//swaggers.entrySet().stream().findFirst(e -> e.getValue().getPaths().get);
        /*for(Swagger swagger: swaggers.v) {
            if (path.endsWith(endpoint)) {
                return Optional.of(path);
            }
        }*/
        
        return Optional.empty();
    }
    
    /*public Set<URI> getHosts() {
        loadResourcesIfNeeded();
        return hosts;
    }*/
    
    public List<SwaggerResource> getResources() {
        loadResourcesIfNeeded();
        return resources.values().stream().collect(Collectors.toList());
    }

    public Optional<UISwaggerRes> getResource(String id) {
        loadResourcesIfNeeded();
        return Optional.ofNullable(resources.get(id));
    }
    
    public Swagger getSwagger(String id) {
        loadResourcesIfNeeded();
        return swaggers.get(id);
    }
    
    public static URI toUri(String url) {
        try {
            return new URI(url);
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to construct URI from ["+url+"]");
        }
    }

    public static URI getServerUri(URI uri) {
        StringBuilder result = new StringBuilder().append(uri.getScheme()).append("://").append(uri.getHost());
        MicroService.getPort(uri).ifPresent(p -> result.append(':').append(p));
        return toUri(result.toString());
    }
}

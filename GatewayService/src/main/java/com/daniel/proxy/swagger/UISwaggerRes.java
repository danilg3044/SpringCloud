package com.daniel.proxy.swagger;

import springfox.documentation.swagger.web.SwaggerResource;

public class UISwaggerRes extends SwaggerResource {
    private String actualLocation;

    public UISwaggerRes(String name, String location, String version, String actualLocation) {
        setName(name);
        setLocation(location);
        setSwaggerVersion(version);
        this.actualLocation = actualLocation;
    }

    public String getActualLocation() {
        return actualLocation;
    }
}


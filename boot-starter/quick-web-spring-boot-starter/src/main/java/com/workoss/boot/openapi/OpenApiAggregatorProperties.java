package com.workoss.boot.openapi;

import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author workoss
 */
@ConfigurationProperties(prefix = "quick.openapi")
public class OpenApiAggregatorProperties {

    private OpenAPI base = new OpenAPI();

    public OpenApiAggregatorProperties() {
        base.paths(new Paths());
        base.components(new Components());
        base.info(new Info().title("Gateway API").description("Gateway API").version("1.0.0"));
    }

    public OpenAPI getBase() {
        return base;
    }
}

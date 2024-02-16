package com.workoss.boot.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.service.OpenAPIService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author workoss
 */
@AutoConfiguration
@ConditionalOnBean(OpenApiAggregatorSpecs.class)
@Import(SpringDocSpecConfiguration.class)
@EnableConfigurationProperties(OpenApiAggregatorProperties.class)
public class OpenApiAggregatorConfiguration {

    /**
     * Create a new {@link OpenApiAggregator} instance.
     *
     * @param specs      the specs to use
     * @param properties the configuration, e.g. for common info
     * @return an aggregator
     */
    @Bean
    public OpenApiAggregator openApiAggregator(OpenApiAggregatorSpecs specs, OpenApiAggregatorProperties properties) {
        return new OpenApiAggregator(specs, properties.getBase());
    }

    /**
     * Create a new {@link AggregatorEndpoint} instance to expose the aggregated spec over
     * HTTP.
     *
     * @param aggregator the aggregator to use
     * @return an endpoint that can be used in WebMVC or WebFlux
     */
    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(type = "org.springdoc.core.service.OpenAPIService")
    public AggregatorEndpoint aggregatorEndpoint(OpenApiAggregator aggregator, ObjectMapper objectMapper) {
        return new AggregatorEndpoint(aggregator, objectMapper);
    }

}

@SuppressWarnings("ALL")
@Configuration
@ConditionalOnClass(OpenAPIService.class)
class SpringDocSpecConfiguration {

    /**
     * Create a new {@link OpenAPI} instance to inject into the SpringDoc spec generator.
     *
     * @param aggregator the aggregator to use
     * @return an OpenAPI spec generated from the endpoints in this application
     */
    @Bean
    OpenAPI openAPIBaseSpec(OpenApiAggregator aggregator) {
        return aggregator.aggregate();
    }

}

@RestController
class AggregatorEndpoint implements InitializingBean {

    private final OpenApiAggregator aggregator;

    private OpenAPI api;

    private final ObjectMapper objectMapper;

    public AggregatorEndpoint(OpenApiAggregator aggregator, ObjectMapper objectMapper) {
        this.aggregator = aggregator;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "${quick.openapi.aggregator.path:/v3/api-docs}", produces = {"application/json"})
    public String api() {
        try {
            return objectMapper.writeValueAsString(api);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.api = aggregator.aggregate();
    }

}

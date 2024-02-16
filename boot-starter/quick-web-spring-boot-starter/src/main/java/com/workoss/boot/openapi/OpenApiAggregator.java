package com.workoss.boot.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author workoss
 */
public class OpenApiAggregator {

    private final OpenApiAggregatorSpecs specs;

    private final OpenAPI base;

    /**
     * Create a new {@link OpenApiAggregator} instance.
     * @param specs the specs to aggregate
     * @param base the base to merge with, e.g. for common info
     */
    public OpenApiAggregator(OpenApiAggregatorSpecs specs, OpenAPI base) {
        this.specs = specs;
        this.base = base;
    }

    /**
     * Aggregate the specs.
     * @return the aggregated spec
     */
    public OpenAPI aggregate() {
        OpenAPI api = new OpenAPI();
        merge(api, base);
        api.setInfo(base.getInfo());
        if (base.getTags() != null) {
            api.setTags(base.getTags());
        }
        Set<OpenAPI> apis = new LinkedHashSet<>();
        for (OpenApiAggregatorSpecs.Spec spec : specs.getSpecs()) {
            OpenAPI item;
            try {
                // Blocking...
                OpenAPIV3Parser parser = new OpenAPIV3Parser();
                item = parser.read(spec.resource().getURL().toString());
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
            apis.add(item);
            // Item might be mutated here. Maybe take a defensive clone copy?
            merge(api, spec.filter().apply(item));
        }
        return specs.getProcessor().apply(api, apis);
    }

    private void merge(OpenAPI api, OpenAPI item) {
        Paths paths = item.getPaths();
        if (paths != null) {
            if (api.getPaths() == null) {
                api.paths(new Paths());
            }
            for (String path : paths.keySet()) {
                api.getPaths().addPathItem(path, paths.get(path));
            }
            merge(paths.getExtensions(), api.getPaths()::getExtensions, api.getPaths()::setExtensions);
        }
        Components target = api.getComponents();
        if (target == null) {
            target = new Components();
            api.components(target);
        }
        Components source = item.getComponents();
        if (source != null) {
            merge(source.getCallbacks(), target::getCallbacks, target::setCallbacks);
            merge(source.getExamples(), target::getExamples, target::setExamples);
            merge(source.getExtensions(), target::getExtensions, target::setExtensions);
            merge(source.getHeaders(), target::getHeaders, target::setHeaders);
            merge(source.getLinks(), target::getLinks, target::setLinks);
            merge(source.getParameters(), target::getParameters, target::setParameters);
            merge(source.getPathItems(), target::getPathItems, target::setPathItems);
            merge(source.getRequestBodies(), target::getRequestBodies, target::setRequestBodies);
            merge(source.getResponses(), target::getResponses, target::setResponses);
            merge(source.getSchemas(), target::getSchemas, target::setSchemas);
            merge(source.getSecuritySchemes(), target::getSecuritySchemes, target::setSecuritySchemes);
        }
    }

    private <T> void merge(Map<String, T> source, Supplier<Map<String, T>> getter, Consumer<Map<String, T>> setter) {
        if (source != null) {
            if (getter.get() == null) {
                setter.accept(new HashMap<>());
            }
            getter.get().putAll(source);
        }
    }

}

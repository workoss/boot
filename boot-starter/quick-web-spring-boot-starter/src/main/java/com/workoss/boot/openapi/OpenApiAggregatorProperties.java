/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

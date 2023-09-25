/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
package com.workoss.boot.modulith.events.mybatis;

import com.workoss.boot.plugin.mybatis.DynamicDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;


/**
 * @author workoss
 */
class DatabaseSchemaInitializer implements InitializingBean {

    private final DatabaseType databaseType;
    private final ResourceLoader resourceLoader;

    private final DynamicDao dynamicDao;

    public DatabaseSchemaInitializer(DatabaseType databaseType, ResourceLoader resourceLoader, DynamicDao dynamicDao) {
        this.databaseType = databaseType;
        this.resourceLoader = resourceLoader;
        this.dynamicDao = dynamicDao;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String schemaResourceFilename = this.databaseType.getSchemaResourceFilename();
        Resource schemaDdlResource = this.resourceLoader.getResource("classpath:" + schemaResourceFilename);
        String schemaDdl = asString(schemaDdlResource);
        dynamicDao.executeUpdate(schemaDdl, null);
    }


    private static String asString(Resource resource) {
        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException var2) {
            throw new UncheckedIOException(var2);
        }
    }
}

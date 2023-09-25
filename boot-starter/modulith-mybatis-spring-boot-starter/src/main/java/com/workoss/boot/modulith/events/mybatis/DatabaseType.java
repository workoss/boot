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

import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.UUID;

/**
 * @author workoss
 */
public enum DatabaseType {
    HSQLDB("hsqldb"),
    H2("h2"),
    MYSQL("mysql") {
        Object uuidToDatabase(UUID id) {
            return id.toString();
        }

        UUID databaseToUUID(Object id) {
            return UUID.fromString(id.toString());
        }
    },
    POSTGRES("postgresql");

    private static final Map<DatabaseDriver, DatabaseType> DATABASE_DRIVER_TO_DATABASE_TYPE_MAP = Map.of(DatabaseDriver.H2, H2, DatabaseDriver.HSQLDB, HSQLDB, DatabaseDriver.POSTGRESQL, POSTGRES, DatabaseDriver.MYSQL, MYSQL);
    private final String value;

    static DatabaseType from(DatabaseDriver databaseDriver) {
        DatabaseType databaseType = (DatabaseType)DATABASE_DRIVER_TO_DATABASE_TYPE_MAP.get(databaseDriver);
        if (databaseType == null) {
            throw new IllegalArgumentException("Unsupported database type: " + databaseDriver);
        } else {
            return databaseType;
        }
    }

    private DatabaseType(String value) {
        this.value = value;
    }

    Object uuidToDatabase(UUID id) {
        return id;
    }

    UUID databaseToUUID(Object id) {
        Assert.isInstanceOf(UUID.class, id, "Database value not of type UUID!");
        return (UUID)id;
    }

    String getSchemaResourceFilename() {
        return "/modulith/schema-" + this.value + ".sql";
    }
}

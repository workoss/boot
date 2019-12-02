/*
 * #%L
 * %%
 * Copyright (C) 2019 Workoss Software, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.workoss.boot.util.plugin.mybatis.provider;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TableColumnInfo {

    private String tableName;

    private String idPropertyName = "id";

    private String idColumnName = "id";

    private List<Class> propertyTypes;

    private List<String> propertyNames;

    private List<String> columnNames;

    public TableColumnInfo addPropertyName(String propertyName) {
        if (propertyNames == null) {
            this.propertyNames = new ArrayList<>();
        }
        this.propertyNames.add(propertyName);
        return this;
    }

    public TableColumnInfo addPropertyType(Class propertyType) {
        if (propertyTypes == null) {
            this.propertyTypes = new ArrayList<>();
        }
        this.propertyTypes.add(propertyType);
        return this;
    }

    public TableColumnInfo addColumnName(String columnName) {
        if (columnNames == null) {
            this.columnNames = new ArrayList<>();
        }
        this.columnNames.add(columnName);
        return this;
    }

}

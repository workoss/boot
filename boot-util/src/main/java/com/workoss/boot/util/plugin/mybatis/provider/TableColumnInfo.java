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

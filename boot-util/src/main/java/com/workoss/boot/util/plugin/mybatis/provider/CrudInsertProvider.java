package com.workoss.boot.util.plugin.mybatis.provider;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderContext;

public class CrudInsertProvider extends BaseProvider {

    public CharSequence insert(Map<String, Object> params, ProviderContext context) {
        return executeSql(context, (tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("insert into ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(" ( ");
            List<String> columns = tableColumnInfo.getColumnNames();
            for (int i = 0, j = columns.size(); i < j; i++) {
                sqlBuilder.append(columns.get(i));
                if (i != columns.size() - 1) {
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.append(" ) values ( ");
            List<String> propertyNames = tableColumnInfo.getPropertyNames();
            for (int i = 0, j = propertyNames.size(); i < j; i++) {
                sqlBuilder.append(bindParameter("record." + propertyNames.get(i)));
                if (i != propertyNames.size() - 1) {
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.append(" ) ");
            return sqlBuilder.toString();
        }));
    }

    public CharSequence insertSelective(Map<String, Object> params, ProviderContext context) {
        return executeSql(context, (tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("<script> insert into ");
            sqlBuilder.append(tableColumnInfo.getTableName());


            StringBuilder columnBuilder = new StringBuilder();
            StringBuilder valueBuilder = new StringBuilder();
            List<String> columns = tableColumnInfo.getColumnNames();
            List<String> propertys = tableColumnInfo.getPropertyNames();
            for (int i = 0, j = propertys.size(); i < j; i++) {
                columnBuilder.append("<if test=\"record." + propertys.get(i) + "!=null\">");
                columnBuilder.append(columns.get(i));
                columnBuilder.append(",");
                columnBuilder.append("</if>");
                valueBuilder.append(" <if test=\"record." + propertys.get(i) + "!=null\">");
                valueBuilder.append(bindParameter("record." + propertys.get(i)));
                valueBuilder.append(",");
                valueBuilder.append(" </if> ");
            }
            sqlBuilder.append(" <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            sqlBuilder.append(columnBuilder);
            sqlBuilder.append(" </trim> ");
            sqlBuilder.append(" <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            sqlBuilder.append(valueBuilder);
            sqlBuilder.append(" </trim></script>");
            return sqlBuilder.toString();
        }));
    }

    public CharSequence insertBatch(Map<String, Object> params, ProviderContext context) {
        return executeSql(context, (tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("<script> insert into ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            List<String> columns = tableColumnInfo.getColumnNames();
            List<String> propertys = tableColumnInfo.getPropertyNames();

            StringBuilder valueBuilder = new StringBuilder();
            sqlBuilder.append("(");
            valueBuilder.append("(");
            for (int i = 0, j = columns.size(); i < j; i++) {
                sqlBuilder.append(columns.get(i));
                valueBuilder.append(bindParameter("item." + propertys.get(i)));
                if (i != columns.size() - 1) {
                    sqlBuilder.append(",");
                    valueBuilder.append(",");
                }
            }
            valueBuilder.append(")");
            sqlBuilder.append(")");

            sqlBuilder.append(" values ");
            sqlBuilder.append("<foreach collection=\"list\" index=\"index\" item=\"item\" open=\"\" separator=\",\" close=\"\">");
            sqlBuilder.append(valueBuilder);
            sqlBuilder.append("</foreach>");

            sqlBuilder.append(" </script>");
            return sqlBuilder.toString();
        }));
    }
}

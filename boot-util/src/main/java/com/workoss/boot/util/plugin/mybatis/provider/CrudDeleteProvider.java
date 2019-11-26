package com.workoss.boot.util.plugin.mybatis.provider;

import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderContext;

public class CrudDeleteProvider extends BaseProvider {

    public CharSequence deleteById(Map<String, Object> params, ProviderContext context) {
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder(" delete from ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(" where ");
            sqlBuilder.append(tableColumnInfo.getIdColumnName());
            sqlBuilder.append("=");
            sqlBuilder.append(bindParameter(tableColumnInfo.getIdPropertyName()));
            return sqlBuilder.toString();
        }));
    }

    public CharSequence deleteByIds(Map<String, Object> params, ProviderContext context) {
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("<script> delete from ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(" where ");
            sqlBuilder.append(tableColumnInfo.getIdColumnName());
            sqlBuilder.append(" in ( ");
            sqlBuilder.append("<foreach collection=\"ids\" index=\"index\" item=\"item\" open=\"\" separator=\",\" close=\"\">");
            sqlBuilder.append(bindParameter("item"));
            sqlBuilder.append("</foreach>");
            sqlBuilder.append(" ) </script>");
            return sqlBuilder.toString();
        }));
    }

    public CharSequence deleteSelective(Map<String, Object> record,ProviderContext context){
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("<script> delete from ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(getWhereSelectColumn(tableColumnInfo));
            sqlBuilder.append(" </script>");
            return sqlBuilder.toString();
        }));
    }
}

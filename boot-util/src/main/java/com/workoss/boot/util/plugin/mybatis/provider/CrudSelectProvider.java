package com.workoss.boot.util.plugin.mybatis.provider;

import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderContext;

public class CrudSelectProvider extends BaseProvider {

    public CharSequence selectById(Map<String, Object> parameter, ProviderContext context) {
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder(" select ");
            sqlBuilder.append(getSelectColumn(tableColumnInfo));
            sqlBuilder.append(" from ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(" where ");
            sqlBuilder.append(tableColumnInfo.getIdColumnName());
            sqlBuilder.append("=");
            sqlBuilder.append(bindParameter("id"));
            return sqlBuilder.toString();
        }));
    }

    public CharSequence selectByIds(Map<String, Object> parameter, ProviderContext context) {
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("<script> select ");
            sqlBuilder.append(getSelectColumn(tableColumnInfo));
            sqlBuilder.append(" from ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(" where ");
            sqlBuilder.append(tableColumnInfo.getIdColumnName());
            sqlBuilder.append(" in (");

            sqlBuilder.append("<foreach collection=\"ids\" index=\"index\" item=\"id\" open=\"\" separator=\",\" close=\"\">");
            sqlBuilder.append(bindParameter("id"));
            sqlBuilder.append("</foreach>");

            sqlBuilder.append(") </script>");
            return sqlBuilder.toString();
        }));
    }

    public CharSequence selectSelective(Map<String, Object> parameter, ProviderContext context) {
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("<script> select ");
            sqlBuilder.append(getSelectColumn(tableColumnInfo));
            sqlBuilder.append(" from ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(getWhereSelectColumn(tableColumnInfo));
            sqlBuilder.append(" </script>");
            return sqlBuilder.toString();
        }));
    }


    public CharSequence selectCountSelective(Map<String, Object> parameter, ProviderContext context) {
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("<script> select ");
            sqlBuilder.append(" count( ");
            sqlBuilder.append(tableColumnInfo.getIdColumnName());
            sqlBuilder.append(" ) ");
            sqlBuilder.append(" from ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(getWhereSelectColumn(tableColumnInfo));
            sqlBuilder.append(" </script>");
            return sqlBuilder.toString();
        }));
    }

}

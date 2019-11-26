package com.workoss.boot.util.plugin.mybatis.provider;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderContext;

public class CrudUpdateProvider extends BaseProvider {

    public CharSequence updateById(Map<String, Object> params, ProviderContext context){
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("<script> update ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(" <set> ");
            List<String> columns = tableColumnInfo.getColumnNames();
            for (int i = 0, j = columns.size(); i < j; i++) {
                if (tableColumnInfo.getIdColumnName().equalsIgnoreCase(tableColumnInfo.getColumnNames().get(i))){
                    continue;
                }
                sqlBuilder.append(" <if test=\"record." + tableColumnInfo.getPropertyNames().get(i) + "!=null\"> ");
                sqlBuilder.append(tableColumnInfo.getColumnNames().get(i));
                sqlBuilder.append("=");
                sqlBuilder.append(bindParameter("record."+tableColumnInfo.getPropertyNames().get(i)));
                sqlBuilder.append(",");
                sqlBuilder.append(" </if> ");
            }
            sqlBuilder.append(" </set> where ");
            sqlBuilder.append(tableColumnInfo.getIdColumnName());
            sqlBuilder.append("=");
            sqlBuilder.append(bindParameter("id"));
            sqlBuilder.append("</script>");
            return sqlBuilder.toString();

        }));
    }
}

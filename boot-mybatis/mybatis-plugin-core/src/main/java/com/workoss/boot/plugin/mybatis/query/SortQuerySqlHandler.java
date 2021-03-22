package com.workoss.boot.plugin.mybatis.query;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.workoss.boot.plugin.mybatis.MybatisUtil;
import com.workoss.boot.plugin.mybatis.SqlHandler;
import com.workoss.boot.plugin.mybatis.SqlParam;
import com.workoss.boot.plugin.mybatis.context.SqlContext;
import com.workoss.boot.util.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SortQuerySqlHandler implements SqlHandler {

	private static final Logger log = LoggerFactory.getLogger(SortQuerySqlHandler.class);

	private final String ORDER_QUERY_MAIN = "order";

	private final String ORDER_QUERY_BY = "by";

	private final String ORDER_BY_SEPERATE = ",";

	@Override
	public void handler(SqlContext context) {
		BoundSql boundSql = (BoundSql) context.getOutputOrInput("boundSql");
		String sql = boundSql.getSql();
		if (sql.toLowerCase().contains(ORDER_QUERY_MAIN) && sql.toLowerCase().contains(ORDER_QUERY_BY)) {
			log.debug("sql have order by ，ignore orderBy");
			return;
		}
		SqlParam sqlParam = (SqlParam) context.getInput("sqlParam");
		DbType dbType = (DbType) context.getInput("dbType");
		String orderBy = sqlParam.getSortBy();
		if (StringUtils.isBlank(orderBy)) {
			return;
		}
		String newSql = SQLBuilderFactory.createSelectSQLBuilder(sql, dbType).orderBy(orderBy.split(ORDER_BY_SEPERATE))
				.toString();
		MappedStatement mappedStatement = (MappedStatement) context.getInput("mappedStatement");
		boundSql = MybatisUtil.newBoundSql(mappedStatement, newSql, boundSql);
		context.putOutput("boundSql", boundSql);
		context.putOutput("change", true);
	}

}

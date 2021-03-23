package com.workoss.boot.plugin.mybatis;

import com.workoss.boot.plugin.mybatis.context.SqlContext;
import org.apache.ibatis.plugin.Invocation;

public class UpdateSqlActionExecutor implements SqlActionExecutor {

	public static final UpdateSqlActionExecutor INSTANCE = new UpdateSqlActionExecutor();

	private UpdateSqlActionExecutor() {
	}

	@Override
	public Object execute(Invocation invocation, SqlContext context) throws Throwable {
		return invocation.proceed();
	}

}

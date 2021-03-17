package com.workoss.boot.plugin.mybatis;

import com.workoss.boot.plugin.mybatis.context.SqlContext;
import com.workoss.boot.util.context.Context;
import org.apache.ibatis.plugin.Invocation;

public interface SqlActionExecutor {

	Object execute(Invocation invocation, SqlContext context) throws Throwable;

}

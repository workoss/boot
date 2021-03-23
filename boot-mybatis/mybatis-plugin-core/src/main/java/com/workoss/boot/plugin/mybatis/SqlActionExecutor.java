package com.workoss.boot.plugin.mybatis;

import com.workoss.boot.plugin.mybatis.context.SqlContext;
import com.workoss.boot.util.context.Context;
import org.apache.ibatis.plugin.Invocation;

/**
 * sqlInterceptor 根据 SELECT UPDATE 执行器
 *
 * @author workoss
 */
public interface SqlActionExecutor {

	/**
	 * 执行器
	 * @param invocation Interceptor invocation
	 * @param context 上下文
	 * @return List
	 * @throws Throwable 异常
	 */
	Object execute(Invocation invocation, SqlContext context) throws Throwable;

}

package com.workoss.boot.plugin.mybatis;

import com.workoss.boot.plugin.mybatis.context.SqlContext;

@FunctionalInterface
public interface SqlHandler {

	/**
	 * 处理查询参数 上下文 包括
	 * Input(sqlParam,executor,mappedStatement,parameter，rowBounds，resultHandler）
	 * Output(change(是否有改动)，rowBounds,result(pageResult) )
	 * @param context 上下文
	 */
	void handler(SqlContext context);

}

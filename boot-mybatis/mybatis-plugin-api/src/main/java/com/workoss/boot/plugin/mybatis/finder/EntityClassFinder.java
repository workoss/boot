package com.workoss.boot.plugin.mybatis.finder;

import com.workoss.boot.plugin.mybatis.CrudDao;
import com.workoss.boot.plugin.mybatis.provider.ClassTableColumnInfo;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.stream.Stream;

public interface EntityClassFinder {


	/**
	 * 循环匹配
	 *
	 * @param context mybatis context
	 * @return 是否使用
	 */
	boolean match(ProviderContext context);

	/**
	 * 查找表列信息
	 *
	 * @param context
	 * @return
	 */
	Optional<ClassTableColumnInfo> findTableColumnInfo(ProviderContext context);




}

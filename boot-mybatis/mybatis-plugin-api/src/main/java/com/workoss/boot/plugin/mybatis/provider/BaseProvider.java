/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workoss.boot.plugin.mybatis.provider;

import com.workoss.boot.plugin.mybatis.finder.EntityClassFinderFactory;
import com.workoss.boot.plugin.mybatis.util.ProviderUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BaseProvider
 *
 * @author workoss
 */
@SuppressWarnings("unused")
public class BaseProvider implements ProviderMethodResolver {

	private static final Logger log = LoggerFactory.getLogger(BaseProvider.class);

	private static final ConcurrentHashMap<String, String> SQL_MAP = new ConcurrentHashMap<>();

	public CharSequence executeQuery(Map<String, Object> params, ProviderContext context) {
		return new StringJoiner(" ").add("<script>").add("${sql}").add("</script>").toString();
	}

	public CharSequence executeUpdate(Map<String, Object> params, ProviderContext context) {
		return new StringJoiner(" ").add("<script>").add("${sql}").add("</script>").toString();
	}

	public CharSequence dynamicSql(Map<String, Object> params, ProviderContext context) {
		String dbType = ProviderUtil.getDbType(context, params);
		String key = getSqlKey(dbType, context);
		String sql = SQL_MAP.get(key);
		if (sql != null) {
			return sql;
		}
		return EntityClassFinderFactory.getClassFinder(context)
			.map(entityClassFinder -> entityClassFinder.findTableColumnInfo(context))
			.map(classTableColumnInfoOptional -> classTableColumnInfoOptional
				.orElseThrow(() -> new RuntimeException("没有找到tableColumnInfo")))
			.map(classTableColumnInfo -> {
				// 校验 是否需要主键
				boolean checkIdFalse = (params.containsKey("id") || params.containsKey("ids"))
						&& classTableColumnInfo.getIdColumn() == null;
				if (checkIdFalse) {
					throw new RuntimeException(context.getMapperMethod().getName() + " 执行的entity没有id或者设置主键");
				}
				return ProviderUtil.getScript(dbType, context.getMapperMethod().getName(), classTableColumnInfo);
			})
			.map(sqlStr -> {
				SQL_MAP.put(key, sqlStr);
				log.debug("mybatis dao:{} 生成sql:{}", key, sqlStr);
				return sqlStr;
			})
			.orElseThrow(() -> new RuntimeException(key + " 获取sql失败"));
	}

	private String getSqlKey(String dbType, ProviderContext context) {
		return new StringJoiner(".").add(dbType)
			.add(context.getMapperType().getName())
			.add(context.getMapperMethod().getName())
			.toString();
	}

}

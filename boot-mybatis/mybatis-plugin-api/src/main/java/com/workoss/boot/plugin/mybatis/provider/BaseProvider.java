/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
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

import com.workoss.boot.annotation.persistence.Column;
import com.workoss.boot.annotation.persistence.Id;
import com.workoss.boot.annotation.persistence.Table;
import com.workoss.boot.annotation.persistence.Transient;
import com.workoss.boot.plugin.mybatis.CrudDao;
import com.workoss.boot.plugin.mybatis.util.ObjectUtil;
import com.workoss.boot.plugin.mybatis.util.ProviderUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * BaseProvider
 *
 * @author workoss
 */
public class BaseProvider implements ProviderMethodResolver {

	private static final Logger log = LoggerFactory.getLogger(BaseProvider.class);

	private static final ConcurrentHashMap<String, String> SQL_MAP = new ConcurrentHashMap<>();

	public CharSequence executeQuery(Map<String, Object> params, ProviderContext context) {
		return new StringJoiner(" ").add("<script>").add("${sql}").add("</script>").toString();
	}

	public CharSequence executeUpdate(Map<String, Object> params, ProviderContext context) {
		return new StringJoiner(" ").add("<script>").add("${sql}").add("</script>").toString();
	}

	public String executeSql(ProviderContext context, Map<String, Object> params, SqlValidate sqlValidate) {
		String dbType = ProviderUtil.getDbType(context, params);
		if (dbType == null) {
			log.warn("[MYBATIS]没有拦截器放入dbType,默认切换到default/mysql");
			dbType = "default";
		}
		String key = getSqlKey(dbType, context);
		String sql = SQL_MAP.get(key);
		if (sql != null) {
			return sql;
		}
		TableColumnInfo tableColumnInfo = getTableColumnInfo(context);
		sqlValidate.validate(tableColumnInfo);
		sql = ProviderUtil.getScript(dbType, context.getMapperMethod().getName(), tableColumnInfo);
		if (ObjectUtil.isBlank(sql)) {
			throw new RuntimeException(key + " 获取sql失败");
		}
		SQL_MAP.put(key, sql);
		log.debug("mybatis dao:{} 生成sql:{}", key, sql);
		return sql;
	}

	private TableColumnInfo getTableColumnInfo(ProviderContext context) {
		Class clazz = entityType(context);
		TableColumnInfo tableColumnInfo = new TableColumnInfo();
		tableColumnInfo.setTableName(getTableName(clazz));
		Field[] fields = clazz.getDeclaredFields();
		Stream.of(fields).filter(field -> !field.isAnnotationPresent(Transient.class)).forEach(field -> {
			String columnName = ObjectUtil.underscoreName(field.getName());
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof Column) {
					// 自定义列名称
					Column column = (Column) annotation;
					if (!ObjectUtil.isBlank(column.name())) {
						columnName = column.name();
					}
				}
				if (annotation instanceof Id) {
					tableColumnInfo.setIdPropertyName(field.getName());
					tableColumnInfo.setIdColumnName(columnName);
				}
			}
			tableColumnInfo.addColumnName(columnName);
			tableColumnInfo.addPropertyName(field.getName());
			tableColumnInfo.addPropertyType(field.getType());
		});
		return tableColumnInfo;
	}

	private String getTableName(Class clazz) {
		Table table = (Table) clazz.getAnnotation(Table.class);
		if (table != null) {
			if (!ObjectUtil.isBlank(table.name())) {
				return table.name();
			}
		}
		return ObjectUtil.underscoreName(clazz.getSimpleName().replaceAll("Entity", ""));
	}

	private String getSqlKey(String dbType, ProviderContext context) {
		return new StringJoiner(".").add(dbType).add(context.getMapperType().getName())
				.add(context.getMapperMethod().getName()).toString();
	}

	/**
	 * 获取BaseMapper接口中的泛型类型
	 * @param context 上下文
	 * @return 对象
	 */
	protected Class<?> entityType(ProviderContext context) {
		return Stream.of(context.getMapperType().getGenericInterfaces()).filter(ParameterizedType.class::isInstance)
				.map(ParameterizedType.class::cast).filter(type -> type.getRawType() == CrudDao.class).findFirst()
				.map(type -> type.getActualTypeArguments()[0]).filter(Class.class::isInstance).map(Class.class::cast)
				.orElseThrow(() -> new IllegalStateException(
						"未找到BaseMapper的泛型类 " + context.getMapperType().getName() + "."));
	}

}

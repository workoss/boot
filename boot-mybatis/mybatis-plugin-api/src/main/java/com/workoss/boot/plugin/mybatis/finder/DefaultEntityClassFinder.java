/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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
package com.workoss.boot.plugin.mybatis.finder;

import com.workoss.boot.annotation.persistence.Column;
import com.workoss.boot.annotation.persistence.Id;
import com.workoss.boot.annotation.persistence.Table;
import com.workoss.boot.annotation.persistence.Transient;
import com.workoss.boot.plugin.mybatis.provider.ClassColumnInfo;
import com.workoss.boot.plugin.mybatis.provider.ClassTableColumnInfo;
import com.workoss.boot.plugin.mybatis.util.ObjectUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * 默认实体发现
 *
 * @author workoss
 */
class DefaultEntityClassFinder extends GenericEntityClassFinder {

	@Override
	public boolean match(ProviderContext context) {
		return true;
	}

	@Override
	public Optional<ClassTableColumnInfo> findTableColumnInfo(ProviderContext context) {
		return findTableColumnInfo(context, field -> !field.isAnnotationPresent(Transient.class),
				(tableColumnInfo, field) -> {
					String columnName = ObjectUtil.underscoreName(field.getName());
					Annotation[] annotations = field.getAnnotations();
					for (Annotation annotation : annotations) {
						if (annotation instanceof Column column) {
							// 自定义列名称
							if (!ObjectUtil.isBlank(column.name())) {
								columnName = column.name();
							}
						}
						if (annotation instanceof Id) {
							tableColumnInfo.idColumn(new ClassColumnInfo(field.getType(), field.getName(), columnName));
						}
					}
					if (tableColumnInfo.getIdColumn() == null && "id".equalsIgnoreCase(field.getName())) {
						tableColumnInfo.idColumn(new ClassColumnInfo(field.getType(), field.getName(), columnName));
					}
					tableColumnInfo.addColumnInfo(new ClassColumnInfo(field.getType(), field.getName(), columnName));
				});
	}

	@Override
	public String getTableName(Class clazz) {
		Table table = (Table) clazz.getAnnotation(Table.class);
		if (table != null && !ObjectUtil.isBlank(table.name())) {
			return table.name();
		}
		return null;
	}

}

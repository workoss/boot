package com.workoss.boot.plugin.mybatis.finder;

import com.workoss.boot.plugin.mybatis.provider.ClassColumnInfo;
import com.workoss.boot.plugin.mybatis.provider.ClassTableColumnInfo;
import com.workoss.boot.plugin.mybatis.util.ObjectUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.util.Optional;

@SuppressWarnings("ALL")
class JavaxEntityClassFinder extends GenericEntityClassFinder {

	@Override
	public boolean match(ProviderContext context) {
		return true;
	}

	@Override
	public Optional<ClassTableColumnInfo> findTableColumnInfo(ProviderContext context) {
		return findTableColumnInfo(context,
				field -> !field.isAnnotationPresent(Transient.class),
				(tableColumnInfo, field) -> {
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

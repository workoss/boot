package com.workoss.boot.plugin.mybatis.finder;

import com.sun.tools.javac.util.StringUtils;
import com.workoss.boot.plugin.mybatis.CrudDao;
import com.workoss.boot.plugin.mybatis.provider.ClassTableColumnInfo;
import com.workoss.boot.plugin.mybatis.util.ObjectUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class GenericEntityClassFinder implements EntityClassFinder {


	abstract String getTableName(Class clazz);


	Optional<ClassTableColumnInfo> findTableColumnInfo(ProviderContext context, Predicate<Field> filter, BiConsumer<ClassTableColumnInfo, Field> fieldConsumer) {
		ClassTableColumnInfo tableColumnInfo = new ClassTableColumnInfo();
		return findEntityClass(context)
				.map(aClass -> {
					String tableName = getTableName(aClass);
					tableColumnInfo.tableName(ObjectUtil.isBlank(tableName) ? ObjectUtil.underscoreName(aClass.getSimpleName().replaceAll("Entity", "")) : tableName);
					Arrays.stream(aClass.getDeclaredFields()).filter(filter)
							.forEach(field -> {
								fieldConsumer.accept(tableColumnInfo, field);
							});
					return tableColumnInfo;
				});

	}


	/**
	 * 查找当前方法对应的实体类
	 *
	 * @param context mybatis context
	 * @return 实体类
	 */
	Optional<Class<?>> findEntityClass(ProviderContext context) {
		return Stream.of(context.getMapperType().getGenericInterfaces())
				.filter(ParameterizedType.class::isInstance)
				.map(ParameterizedType.class::cast)
				.filter(type -> type.getRawType() == CrudDao.class)
				.findFirst()
				.map(type -> type.getActualTypeArguments()[0])
				.filter(Class.class::isInstance)
				.map(Class.class::cast);
	}

}

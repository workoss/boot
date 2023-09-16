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
package com.workoss.boot.plugin.mybatis.finder;

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

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public abstract class GenericEntityClassFinder implements EntityClassFinder {

    abstract String getTableName(Class<?> clazz);

    Optional<ClassTableColumnInfo> findTableColumnInfo(ProviderContext context, Predicate<Field> filter,
                                                       BiConsumer<ClassTableColumnInfo, Field> fieldConsumer) {
        ClassTableColumnInfo tableColumnInfo = new ClassTableColumnInfo();
        return findEntityClass(context)
                .map(aClass -> {
                    String tableName = getTableName(aClass);
                    tableColumnInfo.tableName(ObjectUtil.isBlank(tableName)
                            ? ObjectUtil.underscoreName(aClass.getSimpleName().replaceAll("Entity", "")) : tableName);
                    Arrays.stream(aClass.getDeclaredFields())
                            .filter(filter)
                            .forEach(field -> fieldConsumer.accept(tableColumnInfo, field));
                    return tableColumnInfo;
                });

    }

    /**
     * 查找当前方法对应的实体类
     *
     * @param context mybatis context
     * @return 实体类
     */
    Optional<Class> findEntityClass(ProviderContext context) {
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

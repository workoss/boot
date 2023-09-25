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
package com.workoss.boot.autoconfigure;

import com.workoss.boot.common.typehandler.AutoEnumTypeHandler;
import com.workoss.boot.plugin.mybatis.DynamicDao;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ExecutorType;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author workoss
 */
@AutoConfiguration
public class DaoConfig {

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        //自定义默认enum 转换器
        return configuration -> {
            //全局映射器启用缓存
//            configuration.setCacheEnabled(true);
            //查询时，关闭关联对象即时加载以提高性能
            configuration.setLazyLoadingEnabled(false);
            //设置关联对象加载的形态，此处为按需加载字段(加载字段由SQL指 定)，不会加载关联表的所有字段，以提高性能
            configuration.setAggressiveLazyLoading(true);
            //对于未知的SQL查询，允许返回不同的结果集以达到通用的效果
//            configuration.setMultipleResultSetsEnabled(true);
            //允许使用列标签代替列名
//            configuration.setUseColumnLabel(true);
            //允许使用自定义的主键值(比如由程序生成的UUID 32位编码作为键值)，数据表的PK生成策略将被覆盖
//            configuration.setUseGeneratedKeys(false);
            //给予被嵌套的resultMap以字段-属性的映射支持 FULL,PARTIAL
//            configuration.setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
            //对于批量更新操作缓存SQL以提高性能 BATCH,SIMPLE
            configuration.setDefaultExecutorType(ExecutorType.BATCH);
            //数据库超过25000秒仍未响应则超
            configuration.setDefaultStatementTimeout(25000);
            //允许在嵌套语句中使用行分界（RowBounds）
            configuration.setSafeResultHandlerEnabled(false);
            //否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN 到经典 Java 属性名 aColumn 的类似映射。
            configuration.setMapUnderscoreToCamelCase(true);
            //MyBatis 利用本地缓存机制（Local Cache）防止循环引用（circular references）和加速重复嵌套查询。默认值为 SESSION，这种情况下会缓存一个会话中执行的所有查询。若设置值为 STATEMENT，本地会话仅用在语句执行上，对相同 SqlSession 的不同调用将不会共享数据。
//            configuration.setLocalCacheScope(LocalCacheScope.SESSION);
            //当没有为参数提供特定的 JDBC 类型时，为空值指定 JDBC 类型。某些驱动需要指定列的 JDBC 类型，多数情况直接用一般类型即可，比如 NULL、VARCHAR 或 OTHER。
//            configuration.setJdbcTypeForNull(JdbcType.OTHER);
            //指定哪个对象的方法触发一次延迟加载。
//            configuration.setLazyLoadTriggerMethods(new HashSet<>(Arrays.asList("equals", "clone", "hashCode", "toString")));
            //枚举自动处理
            configuration.setDefaultEnumTypeHandler(AutoEnumTypeHandler.class);
        };
    }

    @ConditionalOnMissingBean
    @Bean
    public DynamicDao dynamicDao(SqlSessionTemplate sqlSessionTemplate) {
        try {
            return sqlSessionTemplate.getMapper(DynamicDao.class);
        } catch (Exception e) {
            sqlSessionTemplate.getConfiguration().addMapper(DynamicDao.class);
        }
        return sqlSessionTemplate.getMapper(DynamicDao.class);
    }

}

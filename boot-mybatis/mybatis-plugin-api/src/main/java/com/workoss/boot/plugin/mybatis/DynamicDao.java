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
package com.workoss.boot.plugin.mybatis;

import com.workoss.boot.annotation.lang.NonNull;
import com.workoss.boot.plugin.mybatis.provider.BaseProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 执行动态sql
 *
 * @author workoss
 */
public interface DynamicDao {
    /**
     * 执行动态sql 查询
     * SELECT id,listener_id as listenerId,serialized_event as serializedEvent FROM EVENT_PUBLICATION <where> <if test="params.id!=null"> id = #{params.id,jdbcType=VARCHAR} </if> </where>
     *
     * @param sql    sql
     * @param params map动态参数
     * @return map list
     */
    @SelectProvider(type = BaseProvider.class, method = "executeQuery")
    List<Map<String, Object>> executeQuery(@Param("sql") String sql, @Param("params") Map<String, Object> params);

    /**
     * 执行变更sql
     *
     * @param sql    sql
     * @param params map 动态参数
     * @return 变更条数
     */
    @UpdateProvider(type = BaseProvider.class, method = "executeUpdate")
    int executeUpdate(@Param("sql") String sql, Map<String, Object> params);

    /**
     * 执行查询
     *
     * @param sql     sql
     * @param params  入参
     * @param convert 转换器
     * @param <T>     泛型
     * @return list 泛型
     */
    default <T> List<T> executeSelect(@NonNull @Param("sql") String sql, @NonNull T params, @NonNull DynamicDaoConvert<T> convert) {
        Map<String, Object> paramsMap = convert.convertParam(params);
        return Optional.ofNullable(executeQuery(sql, paramsMap))
                .map(convert::convertResult)
                .orElse(Collections.emptyList());
    }

}

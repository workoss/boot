package com.workoss.boot.util.plugin.mybatis;


import java.util.List;
import java.util.Map;

/**
 * @author: workoss
 * @date: 2018-05-26 16:33
 * @version:
 */
public interface BaseDao<T,ID> {

    /**
     * 执行sql
     * @param sql
     * @return
     */
    List<Map<String, Object>> executeNativeSql(String sql);

}

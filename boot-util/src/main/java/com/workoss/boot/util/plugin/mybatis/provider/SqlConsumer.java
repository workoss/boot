package com.workoss.boot.util.plugin.mybatis.provider;

@FunctionalInterface
public interface SqlConsumer {

    String sqlCommand(TableColumnInfo info);
}

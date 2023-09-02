# mybatis-plugin

## 描述

主要是为了配合mybatis 提供 通用mapper+ 排序，分页的功能，依赖少，使用简单；
支持多种数据库的通用mapper，比如oracle等；
也在一定的程度上开放自定义插件；


> 主要有两个模块：

1. mybatis-plugin-api：主要提供给mybatis dao 依赖使用，若只是使用该模块，则只支持mysql通用mapper
2. mybatis-plugin-core：提供mybatis拦截器，默认支持动态排序，分页，可以动态添加插件，提供给service使用


## 加入依赖

1. 全局可以导入
```xml
<dependency>
    <groupId>com.workoss.boot</groupId>
    <artifactId>boot-dependencies</artifactId>
    <version>LATEST</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

2. dal/dao模块添加依赖
```xml
<dependency>
    <groupId>com.workoss.boot</groupId>
    <artifactId>mybatis-plugin-api</artifactId>
    <version>LATEST</version>
</dependency>
```

3. service 模块添加依赖
```xml
<dependency>
    <groupId>com.workoss.boot</groupId>
    <artifactId>mybatis-plugin-api</artifactId>
    <version>LATEST</version>
</dependency>
```

## 具体使用

1. 配置拦截器
```java
@Bean
public SqlInterceptor sqlInterceptor() {
    SqlInterceptor sqlInterceptor = new SqlInterceptor();
    sqlInterceptor.addQueryHandlerAfter(context -> {

    }).addQueryHandlerBefore(context -> {

    });
    return sqlInterceptor;
}
```
> queryHandler 可以具体参考SortQuerySqlHandler,PageQuerySqlHandler

2. dao mapper 
```java
@Mapper
public interface TempDistrictDao extends CrudDao<TempDistrictEntity,String> {
}
```

#### 支持常见的通用mapper方法
>这样常见的 selectById、selectByIds、selectSelective、selectCountSelective、insert、insertSelective、insertBatch、updateById、
deleteById、deleteByIds、deleteSelective

#### 结合拦截器支持方法
> selectPageSelective

具体方法

普通方法示例
1. 单表操作
```java
TempDistrictEntity districtEntity = new TempDistrictEntity();
List<TempDistrictEntity> list =tempDistrictDao.selectSelective(districtEntity);
```

2. 动态分页排序，可以支持是否count
```java
TempDistrictEntity districtEntity = new TempDistrictEntity();
List<TempDistrictEntity> list = SqlHelper.page(1,10,false)
        .sort("id asc")
        .execute(sqlRequest -> tempDistrictDao.selectSelective(districtEntity));
```

3. 动态排序（不分页）
```java
TempDistrictEntity districtEntity = new TempDistrictEntity();
List<TempDistrictEntity> list = SqlHelper.sort("id asc")
        .execute(sqlRequest -> tempDistrictDao.selectSelective(districtEntity));
```

4. 执行完可以对返回结果mapper
```java
List<MtStoreListEntity> storeListEntities =  SqlHelper.page(1,10,true)
                .sort("store_id desc")
                .executeAndMapper(sqlRequest -> tempDistrictDao.selectPageSelective(selectEntity))
                .mapper(tempDistrictEntityMapper::toTargetList);
```
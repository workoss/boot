# boot

## 模块

* boot-parent

项目依赖管理

* **boot-dependencies**

boot模块管理，其他项目可以导入pom使用

* **boot-annotation**

常用的自定义注解，比如@Id,@Table等

* **boot-util**

常用工具类，比如集合工具，Jackson工具类，日期工具类等

* **boot-extension**

插件化依赖的jar，参考了dubbo的SPI机制，也提供了支持spring的

* **mybatis-plugin**

结合原生mybatis，以最小依赖，简单方便的使用通用mapper以及排序分页  
具体使用请参照 [mybatis-plugin使用](./doc/mybatis-plugin.md)

* **boot-storage**

对象存储临时Token生成服务，支持S3协议对象存储客户端  
具体部署服务请参照 [storage-server配置使用](./doc/storage-server.md)  
客户端使用请参照 [storage-client使用](./doc/storage-client.md)


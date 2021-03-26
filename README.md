# boot

[![Maven Central](https://img.shields.io/maven-central/v/com.workoss.boot/boot-dependencies.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.workoss.boot)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.workoss.boot/boot-dependencies?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/com/workoss/boot/)
[![Github issues](https://img.shields.io/github/issues-raw/workoss/boot.svg)](https://github.com/workoss/boot/issues)
[![GitHub release](https://img.shields.io/github/release/workoss/boot.svg)](https://github.com/workoss/boot/releases)
[![javadoc](https://javadoc.io/badge2/com.workoss.boot/boot-util/javadoc.svg)](https://javadoc.io/doc/com.workoss.boot/boot-util)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Github reposize](https://img.shields.io/github/repo-size/workoss/boot)](https://github.com/workoss/boot)

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


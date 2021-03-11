storage 对象存储模块
==================

提供通用的支持S3协议的对象存储api 客户端 ，屏蔽掉接入的存贮服务的差异

主要需要支持

* [x] Aliyun OSS client 上传下载
* [x] HuaweiCloud OBS 上传下载
* [x] storage-client-spring-boot-starter spring-boot starter组件
* [x] 兼容openx版本
* [x] STSToken生成服务

客户端支持测试情况

|客户端|accessKey+sercetKey|stsToken|
|:---|:---:|:---:|
|S3|✅||
|OSS|✅|✅|
|OBS|✅|✅|
|COB|✅||
|QINIU|||

### 项目模块

* storage-server: 对象stsToken授权服务+web签名服务
* storage-client: 对象存储API客户端
* storage-client-lite: 对象存储API客户端 (jdk6 老项目使用 shade 仅依赖slf4j-api)
* storage-client-spring-boot-starter: 对象存储的spring-boot-starter组件，方便spring-boot 项目引入
* openx-storage-client: 对openx项目的支持
* popeye-example/storage-client-example: storage-client 示例
* popeye-example/storage-client-spring-boot-example: storage spring-boot项目使用示例
* popeye-example/openx-storage-client-example: storage openx项目示例

### 开发环境要求

* openJDK 8+
* Maven 3.5.X

### 使用说明

#### web表单上传

##### h5签名服务

> 参数说明

|参数|类型|是否必填|示例|描述|
|:---|:---:|:---:|:---|:---|
|key|string|是|/11/22/demo.text|文件路径（包括文件名称）|
|mimeType|string|否|text/plain|文件类型，可以空，空的时候程序会根据key自动获取|
|successActionStatus|string|否|200|上传返回状态|

```text
POST http://domain/storage/signservice/sign
Content-Type: application/json
{
    "key": "1.png",
    "mimeType":"image/png",
    "successActionStatus":"200"
}
```

> 返回结果

|参数|类型|示例|描述|
|:---|:---:|:---|:---|
|storageType|enum|OSS(阿里云)/OBS(华为云)|对象存储类型|
|accessKey|string| |OSSAccessKeyId(OSS上传参数),AccessKeyId(OBS上传参数)|
|stsToken|string| |ak/sk/securityToken签名 有值 OSS上传参数(x-oss-security-token) OBS上传参数(x-obs-security-token)|
|policy|string| |policy|
|signature|string| |signature|
|key|string|11/demo.conf|文件名称(包括路径)|
|host|string|https://workoss.oss-cn-shenzhen.aliyuncs.com|域名(不是自定义域名)|
|mimeType|string|text/plain|文件类型 contentType;OBS上传(form body content-type),OSS(x-oss-content-type)|
|expire|long|1614238325615|过期时间|
|successActionStatus|string|200|form上传成功返回状态 OSS/OBS上传参数(success_action_status)|

```json
{
  "storageType": "OSS",
  "accessKey": "******accessKey*******",
  "stsToken": "******stsToken******",
  "policy": "******policy*******",
  "signature": "******signature******",
  "key": "demo.conf",
  "host": "https://{bucket}.oss-cn-shenzhen.aliyuncs.com",
  "expire": 1614238325615,
  "mimeType": "text/plain",
  "successActionStatus": null
}
```

##### ak/sk/securityToken 方式  ***推荐***

* oss form 上传
  ![oss_web_sts_upload](./img/oss_web_sts_upload.png)

* obs form 上传
  ![obs_web_sts_upload](img/obs_web_sts_upload.png)

#### spring-boot项目使用 ***推荐***

* 引入依赖

```xml

<dependency>
    <groupId>com.yifengx.popeye</groupId>
    <artifactId>openx-storage-spring-boot-starter</artifactId>
    <version>x.y.z</version>
</dependency>
```

* 配置

```yaml
popeye:
  storage:
    enabled: true
    health: true
    default-client:
      bucket-name: yf-example
      base-path: ehr
      domain: https://yf-test-oss.yifengx.com
      token-url: https://x.x.x.x/storage
```

|配置项|类型|是否必填|说明|
|:---|:---|:---:|:---|
|popeye.storage.enabled|boolean|否|storage-client是否启用|
|popeye.storage.health|boolean|否|是否开启健康检测需要配合spring-boot-starter-actuator使用|
|popeye.storage.default-client-key|string|否|与多客户端配置联合使用，默认default|
|popeye.storage.default-client|object|默认单客户端配置|
|popeye.storage.default-client.storageType|enum|是|客户端类型 OSS,OBS,AWS等|
|popeye.storage.default-client.bucket-name|string|是|客户端桶名称|
|popeye.storage.default-client.endpoint|string|否|客户端endpoint ak/sk 下必填|
|popeye.storage.default-client.access-key|string|否|客户端aceKey ak/sk 下必填|
|popeye.storage.default-client.secret-key|string|否|客户端秘钥 ak/sk 下必填|
|popeye.storage.default-client.token-url|string|否|客户端授权URL ak/sk/securityToken 结合storage-server使用 |
|popeye.storage.default-client.domain|string|否|自定义域名|
|popeye.storage.default-client.base-path|string|否|目录前缀|
|popeye.storage.default-client.client-configs|map| |多客户端配置 key为客户端别称，value为客户端配置，参考上面default-client|

* API使用

```java
@Autowired
private StorageTemplate storageTemplate;
@Test
void testStorage(){
        StorageFileInfo fileInfo=storageTemplate.client().getObject("data/1.png");
        }
```

#### openx项目使用

* 引入依赖

```xml

<dependency>
    <groupId>com.yifengx.popeye</groupId>
    <artifactId>openx-storage-client</artifactId>
    <version>x.y.z</version>
</dependency>
```

* 配置

```xml

<bean id="multiStorageClientConfig" class="com.yifengx.popeye.storage.config.DefaultMultiStorageClientConfig">
    <property name="enabled" value="true"/>
    <property name="defaultClient">
        <bean class="com.yifengx.popeye.storage.config.StorageClientConfig">
            <property name="bucketName" value="yf-example"/>
            <property name="basePath" value="ehr"/>
            <property name="domain" value="https://yf-test-oss.yifengx.com"/>
            <property name="tokenUrl" value="https://x.x.x.x/storage"/>
        </bean>
    </property>
</bean>
<bean id="storageTemplate" class="com.yifengx.popeye.storage.StorageTemplate">
<property name="multiStorageClientConfig" ref="multiStorageClientConfig"/>
</bean>
<bean id="/storage/signService" class="com.yifengx.popeye.storage.openx.impl.StorageSignServiceImpl">
<constructor-arg ref="storageTemplate"/>
</bean>
```

openx需要增加 com.yifengx.popeye.storage.openx.* 签名服务

```xml

<bean class="com.talkyun.openx.RestfulScanner">
    <property name="path" value="com.yifeng.ssm.rest.api.*;com.yifengx.popeye.storage.openx.*"/>
</bean>
```

* API使用

```java
@Autowired
private StorageTemplate storageTemplate;
@Test
void testStorage(){
        StorageFileInfo fileInfo=storageTemplate.client().getObject("data/1.png");
        }
```

* h5签名服务

```text
POST http://domain/storage/signservice/sign
Content-Type: application/json
{
    "key": "1.png",
    "mimeType":"image/png"
}
```

返回

```json
{
  "storageType": "OSS",
  "accessKey": "******accessKey*******",
  "stsToken": "******stsToken******",
  "policy": "******policy*******",
  "signature": "******signature******",
  "key": "demo.conf",
  "host": "https://{bucket}.oss-cn-shenzhen.aliyuncs.com",
  "expire": 1614238325615,
  "mimeType": "text/plain",
  "successActionStatus": null
}
```

#### 普通客户端使用

* 引入依赖

```xml

<dependency>
    <groupId>com.yifengx.popeye</groupId>
    <artifactId>storage-client</artifactId>
    <version>x.y.z</version>
</dependency>
```

* 配置

```java

@Configurable
public class StorageConfig {
    @Bean
    public MultiStorageClientConfig multiStorageClientConfig() {
        DefaultMultiStorageClientConfig multiStorageClientConfig = new DefaultMultiStorageClientConfig();
        StorageClientConfig storageClientConfig = new StorageClientConfig();
        storageClientConfig.setBucketName("yf-example");
        storageClientConfig.setDomain("https://yf-test-oss.yifengx.com");
        storageClientConfig.setTokenUrl("https://x.x.x.x/storage");
        storageClientConfig.setBasePath("ehr");
        Map<String, StorageClientConfig> map = new HashMap<>();
        map.put("yf-example", storageClientConfig);
        multiStorageClientConfig.setClientConfigs(map);
        multiStorageClientConfig.setDefaultClientKey("yf-example");
        return multiStorageClientConfig;
    }

    @Bean
    public StorageTemplate storageTemplate(MultiStorageClientConfig multiStorageClientConfig) {
        StorageTemplate storageTemplate = new StorageTemplate();
        storageTemplate.setMultiStorageClientConfig(multiStorageClientConfig);
        return storageTemplate;
    }
}
```

* API使用

```java
@Autowired
private StorageTemplate storageTemplate;
@Test
void testStorage(){
        StorageFileInfo fileInfo=storageTemplate.client().getObject("data/1.png");
        }
```

#### lite客户端使用

* 引入依赖

```xml

<dependency>
    <groupId>com.yifengx.popeye</groupId>
    <artifactId>storage-client-lite</artifactId>
    <version>x.y.z</version>
</dependency>
```

* 配置及使用

```java
DefaultMultiStorageClientConfig multiStorageClientConfig=new DefaultMultiStorageClientConfig();
        StorageClientConfig example=new StorageClientConfig();
        example.setBucketName("yf-example");
        example.setDomain("https://yf-test-oss.yifengx.com/");
        example.setTokenUrl("https://x.x.x.x/storage");
        example.setBasePath("/ssm/goodsImage/");
        multiStorageClientConfig.setDefaultClient(example);
        StorageTemplate storageTemplate=new StorageTemplate(multiStorageClientConfig);

        StorageFileInfoListing listing=storageTemplate.client("yf-example").listObjects("5e79a51c28a3200001d9e719/CLOUD/major/1002044",null,null,10);
```


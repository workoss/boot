# rule-engine

## 描述

规则引擎    [zen-engine](https://github.com/gorules/zen)  
规则模型    [JSON Decision Model (JDM)](https://gorules.io/docs/rules-engine/json-decision-model)  
editor源码 https://github.com/gorules/jdm-editor   
editor在线 https://editor.gorules.io  

![编辑器示例图片](./img/jdm_editor.png)


## quick start

1. 全局可以导入
```xml
<dependency>
    <groupId>com.workoss.boot</groupId>
    <artifactId>boot-dependencies</artifactId>
    <version>${LATEST}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

2. 添加依赖runtime
```xml
<dependency>
    <groupId>com.workoss.boot</groupId>
    <artifactId>rule-engine</artifactId>
</dependency>
```

3. 使用示例

```java
public class RuleEngineTest {

    @Test
    void test01() throws InterruptedException {
        ZenRuleEngineConfig config = new ZenRuleEngineConfig();
        config.setTrace(false);
        config.setMaxDepth(20);
        ZenRuleEngine engine = new ZenRuleEngine(config);

        String decision = "{\"contentType\":\"application/vnd.gorules.decision\",\"edges\":[{\"id\":\"1dfbc57d-ad1f-4cf8-978a-e43241856fc8\",\"type\":\"edge\",\"sourceId\":\"be0a5c2d-538d-4e50-9843-91b274e1b9d8\",\"targetId\":\"7c07550a-07bc-4ee4-80d6-9ad900e3d6c9\"},{\"id\":\"5e2dc187-3b82-4f21-94f5-9c3fd16e12d4\",\"type\":\"edge\",\"sourceId\":\"7c07550a-07bc-4ee4-80d6-9ad900e3d6c9\",\"targetId\":\"72d52cfc-e866-4c11-bf79-6be9f750e4d7\"}],\"nodes\":[{\"id\":\"be0a5c2d-538d-4e50-9843-91b274e1b9d8\",\"name\":\"Request\",\"type\":\"inputNode\",\"position\":{\"x\":180,\"y\":280}},{\"id\":\"7c07550a-07bc-4ee4-80d6-9ad900e3d6c9\",\"name\":\"Fees\",\"type\":\"decisionTableNode\",\"content\":{\"hitPolicy\":\"first\",\"inputs\":[{\"id\":\"FD4qBBPv2G\",\"name\":\"Cart Total\",\"type\":\"expression\",\"field\":\"cart.total\"},{\"id\":\"DA3Ybo-shA\",\"name\":\"Customer Country\",\"type\":\"expression\",\"field\":\"customer.country\"},{\"id\":\"jrsT5Wg9F8\",\"name\":\"Customer Tier\",\"type\":\"expression\",\"field\":\"customer.tier\"}],\"outputs\":[{\"id\":\"qA7iYc3Wle\",\"name\":\"Fees Flat ($)\",\"type\":\"expression\",\"field\":\"fees.flat\"},{\"id\":\"JuUcECFGe1\",\"name\":\"Fees Percent\",\"type\":\"expression\",\"field\":\"fees.percent\"}],\"rules\":[{\"_id\":\"vCqrZGdWjA\",\"DA3Ybo-shA\":\"\\\"US\\\"\",\"FD4qBBPv2G\":\"> 1000\",\"JuUcECFGe1\":\"2\",\"jrsT5Wg9F8\":\"\\\"gold\\\"\",\"qA7iYc3Wle\":\"\"},{\"_id\":\"CpXx-s78FH\",\"DA3Ybo-shA\":\"\\\"US\\\"\",\"FD4qBBPv2G\":\"> 1000\",\"JuUcECFGe1\":\"3\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"\"},{\"_id\":\"zH-PuRB2aQ\",\"DA3Ybo-shA\":\"\\\"US\\\"\",\"FD4qBBPv2G\":\"\",\"JuUcECFGe1\":\"\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"25\"},{\"_id\":\"HjEp-eQhAP\",\"DA3Ybo-shA\":\"\\\"CA\\\",\\\"MX\\\"\",\"FD4qBBPv2G\":\"> 1000\",\"JuUcECFGe1\":\"5\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"\"},{\"_id\":\"nq40hJ1nXy\",\"DA3Ybo-shA\":\"\\\"CA\\\",\\\"MX\\\"\",\"FD4qBBPv2G\":\"\",\"JuUcECFGe1\":\"\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"50\"},{\"_id\":\"G64ltgrVgV\",\"DA3Ybo-shA\":\"\\\"IE\\\",\\\"UK\\\",\\\"FR\\\",\\\"DE\\\"\",\"FD4qBBPv2G\":\"> 1000\",\"JuUcECFGe1\":\"10\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"\"},{\"_id\":\"PD3oYgtiDa\",\"DA3Ybo-shA\":\"\\\"IE\\\",\\\"UK\\\",\\\"FR\\\",\\\"DE\\\"\",\"FD4qBBPv2G\":\"\",\"JuUcECFGe1\":\"\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"100\"},{\"_id\":\"1BY7iMFoDw\",\"DA3Ybo-shA\":\"\",\"FD4qBBPv2G\":\"> 1000\",\"JuUcECFGe1\":\"15\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"\"},{\"_id\":\"dlwJlbW7ZH\",\"DA3Ybo-shA\":\"\",\"FD4qBBPv2G\":\"\",\"JuUcECFGe1\":\"\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"150\"}]},\"position\":{\"x\":420,\"y\":280}},{\"id\":\"72d52cfc-e866-4c11-bf79-6be9f750e4d7\",\"name\":\"Response\",\"type\":\"outputNode\",\"position\":{\"x\":670,\"y\":280}}]}";
        String input = "{ \"input\": 12 }";
        EvaluateResult result = engine.evaluate(decision.getBytes(StandardCharsets.UTF_8), JsonMapper.parse(input));
        System.out.println(JsonMapper.toJSONString(result));
        boolean validate = engine.validate("多哦少".getBytes(StandardCharsets.UTF_8));
        System.out.println(validate);
    }
} 
```
执行结果
```json
{"performance":"241.714µs","result":{"fees":{"flat":150}}}
```

> 更多请查看[测试代码](../boot-extension/rule-engine/src/test/java/com/workoss/boot/engine/RuleEngineTest.java)


## 具体使用

1. 简化依赖
```xml
<dependency>
    <groupId>com.workoss.boot</groupId>
    <artifactId>rule-engine</artifactId>
    <version>${LATEST}</version>
    <classifier>${os.detected.classifier}</classifier>
</dependency>
```

> os.detected.classifier 可以替换成 os+arch
目前支持
* linux-x86_64
* linux-aarch_64
* windows-x86_64
* osx-x86_64
* osx-aarch_64

也可以pom 中配置，这样os.detected.classifier就是动态值，会根据系统自动选择 
```xml
<extensions>
    <extension>
        <groupId>kr.motd.maven</groupId>
        <artifactId>os-maven-plugin</artifactId>
        <version>1.7.0</version>
    </extension>
</extensions>
```

### 其他使用 

表达式直接使用，可以用来公式计算，也可以使用常见的一些[zen函数](https://gorules.io/docs/rules-engine/expression-language/)

```java
String input = "{\"goods\":{\"price\": 0.2,\"quantity\": "+(i+1)+"}}";
JsonNode result = engine.runExpr(expression, JsonMapper.parse(input));
```

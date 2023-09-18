# 规则引擎

[![rule-engine rust](https://github.com/workoss/boot/actions/workflows/rule-engine.yml/badge.svg)](https://github.com/workoss/boot/actions/workflows/rule-engine.yml)

----

## 快速开始

[rule-engine文档](../../doc/rule-engine.md)

## 构建

### 1. 本机构建

系统环境 macos

1. rust环境
2. docker环境
3. 生成动态链接库

```shell
sudo sh cross.sh
```

4. maven 打包发布

```shell
mvn deploy
```

### 2. github action 构建

具体请查看 
[action](../../.github/workflows/rule-engine.yml)

也可以查看官方文档 https://docs.github.com/zh/actions

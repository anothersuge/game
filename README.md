# 绿萝狼锐评（暂定）

一个供朋友间使用的游戏评价网站

## 功能特性

- 浏览游戏信息
- 发表评价和评分
- 查看朋友的评价
- 进行简单的互动（点赞、评论）

## 技术架构

- 后端：Spring Boot 3 + Java 21
- 数据库：SQLite
- 模板引擎：Thymeleaf

## 配置要求

1. Java 21+
2. Maven 3.6+

## 快速开始

1. 克隆项目
2. 配置DashScope API密钥（重要！）
3. 运行项目

## DashScope API密钥配置

要使用AI生成功能，您需要配置DashScope API密钥：

1. 访问[阿里云DashScope控制台](https://dashscope.console.aliyun.com/)申请API密钥
2. 在`src/main/resources/application.yml`文件中找到以下配置：

```yaml
dashscope:
  api:
    key: your-api-key-here
```

3. 将`your-api-key-here`替换为您从阿里云获取的实际API密钥

## AI生成描述的异步处理

为了提升用户体验，游戏描述的AI生成过程是异步进行的：

1. 当创建新游戏时，系统会先设置默认描述"暂无描述"
2. 然后异步调用AI服务生成游戏描述
3. AI生成完成后，系统会自动更新游戏描述

这种设计确保了用户无需等待AI生成过程完成就能继续操作。

## 运行项目

```bash
mvn spring-boot:run
```

访问 http://localhost:8080 查看应用

## 测试

运行单元测试：

```bash
mvn test
```
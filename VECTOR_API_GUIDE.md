# LangChain4j 向量查询 API 使用指南

## 概述

本项目现已集成向量查询功能，支持语义搜索和简单的 RAG（检索增强生成）。

## 新增 API 接口

### 1. 向量搜索 - `/api/chat/embedding/search`
基于查询文本进行语义搜索。

**请求示例：**
```json
{
  "query": "什么是 LangChain4j？",
  "maxResults": 3,
  "minScore": 0.7
}
```

**参数说明：**
- `query` (必填): 搜索查询文本
- `maxResults` (可选): 最大返回结果数，默认 3
- `minScore` (可选): 最小相似度分数 (0.0-1.0)，默认 0.7

---

### 2. 添加文本到向量存储 - `/api/chat/embedding/add`
将自定义文本添加到向量存储中。

**请求示例：**
```json
{
  "text": "这是我要添加的自定义文本内容..."
}
```

---

### 3. RAG 查询 - `/api/chat/embedding/rag`
结合向量检索和 LLM 生成的增强问答。

**请求示例：**
```json
{
  "query": "解释一下什么是向量嵌入？"
}
```

## 使用 curl 测试

### 测试向量搜索
```bash
curl -X POST http://localhost:8080/api/chat/embedding/search \
  -H "Content-Type: application/json" \
  -d '{"query": "LangChain4j 是什么", "maxResults": 3}'
```

### 测试 RAG 查询
```bash
curl -X POST http://localhost:8080/api/chat/embedding/rag \
  -H "Content-Type: application/json" \
  -d '{"query": "解释一下 RAG 技术"}'
```

### 添加自定义文本
```bash
curl -X POST http://localhost:8080/api/chat/embedding/add \
  -H "Content-Type: application/json" \
  -d '{"text": "Spring Boot 简化了 Spring 应用的开发和部署流程。"}'
```

## 预置示例文档

项目启动时会自动加载以下示例文档到向量存储：

1. LangChain4j 是一个用于 Java 开发的 LLM 应用开发框架...
2. 向量嵌入（Embedding）是将文本转换为数值向量的过程...
3. RAG（检索增强生成）是一种将向量检索与 LLM 生成相结合的技术...
4. LangChain4j 支持多种向量存储...
5. DocumentSplitter 用于将长文档分割成小片段...
6. 火山引擎（Volcengine）是字节跳动推出的云服务平台...
7. Spring Boot 是一个快速开发 Java 应用的框架...
8. OpenAI 的 text-embedding-ada-002 是一个常用的嵌入模型...

## 核心组件

- **EmbeddingService**: 向量服务类，处理文档向量化和检索
- **EmbeddingModel**: 嵌入模型（使用火山引擎/OpenAI）
- **EmbeddingStore**: 向量存储（使用 InMemoryEmbeddingStore）

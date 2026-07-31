# LangChain4j Demo (JDK 8)

基于 Spring Boot 2.x + LangChain4j 的 AI 项目示例，支持 JDK 8。

## 技术栈

- **JDK**: 1.8+
- **Spring Boot**: 2.7.18
- **LangChain4j**: 0.34.0
- **Build Tool**: Maven

## 功能特性

- OpenAI API 集成
- **火山引擎（豆包）模型支持**
- 简单聊天接口
- 多语言翻译
- 创意写作
- 自定义 System Prompt
- 内存向量存储（可选）

## 前置要求

- JDK 8 或更高版本
- Maven 3.6+
- OpenAI API Key 或火山引擎 API Key

## 快速开始

### 配置方式一：使用火山引擎（豆包）模型

本项目已内置火山引擎支持！在 `application.yml` 中配置：

```yaml
langchain4j:
  openai:
    api-key: your-volcengine-api-key
    base-url: https://ark.cn-beijing.volces.com/api/coding/v3
    # 启用自定义火山引擎实现（推荐）
    use-custom-volcengine: true
    chat:
      model: volcengine-plan/doubao-seed-2.0-code
      temperature: 0.7
    embedding:
      model: volcengine-plan/doubao-seed-2.0-code
```

#### 火山引擎配置说明

- `use-custom-volcengine: true` - 使用自定义的火山引擎实现，兼容性更好
- 如果设置为 `false`，则使用标准的 OpenAiChatModel 实现

### 配置方式二：使用 OpenAI 模型

```yaml
langchain4j:
  openai:
    api-key: your-openai-api-key
    base-url: https://api.openai.com
    use-custom-volcengine: false
    chat:
      model: gpt-3.5-turbo
      temperature: 0.7
```

或者通过环境变量设置：

```bash
# Linux/Mac
export OPENAI_API_KEY=your-api-key-here
export OPENAI_BASE_URL=https://ark.cn-beijing.volces.com/api/coding/v3

# Windows
set OPENAI_API_KEY=your-api-key-here
set OPENAI_BASE_URL=https://ark.cn-beijing.volces.com/api/coding/v3
```

### 运行项目

```bash
mvn spring-boot:run
```

### 测试接口

#### 健康检查

```bash
curl http://localhost:8080/api/chat/health
```

#### 简单聊天

```bash
curl -X POST http://localhost:8080/api/chat/simple \
  -H "Content-Type: application/json" \
  -d "{\"message\": \"你好，请介绍一下你自己\"}"
```

#### 翻译

```bash
curl -X POST http://localhost:8080/api/chat/translate \
  -H "Content-Type: application/json" \
  -d "{\"text\": \"你好，世界\", \"language\": \"英语\"}"
```

#### 创意写作

```bash
curl -X POST http://localhost:8080/api/chat/creative \
  -H "Content-Type: application/json" \
  -d "{\"topic\": \"秋天\", \"tone\": \"诗意\"}"
```

#### 自定义 System Prompt

```bash
curl -X POST http://localhost:8080/api/chat/custom \
  -H "Content-Type: application/json" \
  -d "{\"systemPrompt\": \"你是一个专业的诗人。\", \"userMessage\": \"写一首关于月亮的诗\"}"
```

## 项目结构

```
langchain4j-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── ai/
│   │   │               ├── Langchain4jDemoApplication.java
│   │   │               ├── config/
│   │   │               │   ├── LangChain4jConfig.java          # LangChain4j 配置
│   │   │               │   └── LangChain4jProperties.java      # 配置属性
│   │   │               ├── model/
│   │   │               │   └── VolcengineChatModel.java        # 火山引擎自定义实现
│   │   │               ├── controller/
│   │   │               │   └── ChatController.java              # REST 控制器
│   │   │               └── service/
│   │   │                   ├── Assistant.java                   # AI 服务接口
│   │   │                   └── ChatService.java                 # 聊天服务
│   │   └── resources/
│   │       └── application.yml
├── pom.xml
└── README.md
```

## 核心组件说明

### VolcengineChatModel

专门为火山引擎（豆包）模型设计的自定义 ChatLanguageModel 实现。

特点：
- 完全兼容火山引擎 OpenAI 风格的 API
- 支持自定义模型名称格式（如 `volcengine-plan/doubao-seed-2.0-code`）
- 可配置的超时设置
- 更好的错误处理

### Assistant 接口

使用 LangChain4j 的 `@SystemMessage` 和 `@UserMessage` 注解定义 AI 服务接口：

```java
public interface Assistant {
    @SystemMessage("你是一个乐于助人的 AI 助手。")
    String chat(@UserMessage String userMessage);
}
```

### LangChain4jConfig

配置 ChatLanguageModel、EmbeddingModel 和 EmbeddingStore 等 Bean。
根据 `use-custom-volcengine` 配置自动选择使用标准实现还是自定义实现。

## LangChain4j 特点

- **简洁的 API**：通过接口注解定义 AI 服务
- **多提供商支持**：OpenAI、Azure、Anthropic、Hugging Face 等
- **RAG 支持**：内置向量存储和文档加载器
- **内存管理**：对话历史自动管理
- **工具调用**：支持 Function Calling

## 扩展功能

### 添加 RAG (检索增强生成)

项目已包含文档解析和向量存储依赖，可以：
1. 加载文档（PDF、TXT 等）
2. 生成嵌入向量
3. 存储到向量数据库
4. 实现检索增强对话

### 切换其他 AI 提供商

LangChain4j 支持多种 AI 提供商，只需修改依赖和配置即可：
- Azure OpenAI
- Anthropic Claude
- Hugging Face
- Ollama (本地模型)
- Google Gemini

## 故障排除

### 火山引擎模型问题

如果遇到 `UnsupportedModel` 错误：

1. 确保 `use-custom-volcengine` 设置为 `true`
2. 检查 `base-url` 是否正确
3. 确认 API Key 有效且有足够的配额
4. 查看日志中的详细错误信息

### 调试模式

在 `application.yml` 中启用调试日志：

```yaml
logging:
  level:
    com.example.ai: DEBUG
    dev.langchain4j: DEBUG
```

## 参考文档

- [LangChain4j 官方文档](https://docs.langchain4j.dev/)
- [LangChain4j GitHub](https://github.com/langchain4j/langchain4j)
- [Spring Boot 2.x 文档](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/)
- [火山引擎方舟](https://www.volcengine.com/product/ark)

## 常见问题

### Q: 为什么使用 Spring Boot 2.x 而不是 3.x？
A: Spring Boot 3.x 需要 JDK 17+，本项目为了支持 JDK 8 选择了 Spring Boot 2.7.x（最后一个支持 JDK 8 的版本）。

### Q: 如何使用本地模型？
A: 可以集成 Ollama，使用 LangChain4j 的 Ollama 模块。

### Q: 火山引擎和 OpenAI 可以混用吗？
A: 可以，通过修改配置文件中的 `use-custom-volcengine`、`base-url` 和 `model` 即可切换。

## 许可证

MIT License

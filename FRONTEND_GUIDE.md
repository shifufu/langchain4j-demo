# LangChain4j 前端项目使用指南

## 🎉 项目已创建完成！

Vue 前端项目已成功创建并启动，现在你可以开始使用了！

## 📋 项目概览

### 技术栈
- **Vue 3** - 现代化的前端框架
- **Vite** - 快速的构建工具
- **Element Plus** - 美观的 UI 组件库
- **Axios** - HTTP 请求库

### 功能模块
1. 🤖 **简单聊天** - 基础对话功能
2. 🌍 **多语言翻译** - 支持多种语言互译
3. ✨ **创意写作** - 根据主题和风格生成内容
4. ⚙️ **自定义提示词** - 灵活配置 System Prompt
5. 📊 **向量存储** - 添加文本和向量搜索
6. 🔍 **RAG 问答** - 检索增强生成问答

## 🚀 快速开始

### 1. 启动后端服务（必须先启动）

在 `langchain4j-demo` 根目录下：

```bash
mvn spring-boot:run
```

确保后端服务运行在 `http://localhost:8080`

### 2. 访问前端应用

前端已经启动，访问：**http://localhost:5173**

### 3. 开始使用！

- 使用左侧导航栏切换不同功能
- 顶部标签显示后端服务连接状态
- 所有输入框支持动态输入

## 📁 项目结构

```
langchain4j-demo/
├── frontend/                    # 前端项目目录
│   ├── src/
│   │   ├── App.vue             # 主应用组件（所有功能）
│   │   ├── main.js             # 应用入口
│   │   ├── api.js              # API 接口封装
│   │   └── style.css           # 全局样式
│   ├── index.html              # HTML 模板
│   ├── package.json            # 项目依赖
│   └── README.md               # 前端项目说明
├── src/                         # 后端 Java 源码
├── pom.xml                      # Maven 配置
└── FRONTEND_GUIDE.md           # 本文档
```

## 🎯 功能使用说明

### 1. 简单聊天
- 输入消息，点击发送
- AI 会进行基础对话回复

### 2. 多语言翻译
- 输入要翻译的文本
- 指定目标语言（如：英语、日语、法语）
- 点击翻译获取结果

### 3. 创意写作
- 输入主题（如：秋天、梦想）
- 选择风格（如：诗意、幽默、科幻）
- 点击生成获取创作内容

### 4. 自定义提示词
- 输入系统提示词（定义 AI 角色）
- 输入用户消息
- 执行获得定制化回复

### 5. 向量存储
**添加文本：**
- 输入要存储的文本内容
- 点击添加到向量数据库

**搜索：**
- 输入搜索查询
- 调整结果数量和相似度阈值
- 点击搜索获取相关文本

### 6. RAG 问答
- 输入问题（需要先在向量存储中添加文档）
- 系统会先搜索相关内容，再结合 AI 生成回答
- 显示参考资料和相似度

## 🛠️ 开发命令

### 前端开发（在 frontend 目录下）

```bash
# 安装依赖
npm install

# 启动开发服务器（已启动）
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### 后端开发（在项目根目录下）

```bash
# 启动 Spring Boot 应用
mvn spring-boot:run

# 编译项目
mvn clean compile

# 打包项目
mvn clean package
```

## ⚙️ 配置说明

### 修改 API 地址

如果后端服务不在 `http://localhost:8080`，修改 `frontend/src/api.js`：

```javascript
const api = axios.create({
  baseURL: 'http://your-backend-host:port/api',  // 修改此处
  timeout: 60000
})
```

### 修改前端端口

修改 `frontend/vite.config.js`（如果需要新建）：

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000  // 修改端口
  }
})
```

## 🎨 界面预览

- 渐变色背景，现代化设计
- 左侧导航栏，功能清晰
- 卡片式布局，美观易读
- 实时健康状态检测
- 加载状态提示
- 错误消息反馈

## 📝 注意事项

1. **后端必须先启动** - 前端依赖后端 API
2. **向量存储使用前** - 需要先添加文本数据
3. **网络连接** - 确保可以访问火山引擎或 OpenAI API
4. **API Key 配置** - 检查后端 `application.yml` 中的 API 配置

## 🔧 故障排除

### 前端无法访问后端
- 检查后端是否启动在 8080 端口
- 检查防火墙设置
- 查看浏览器控制台的网络请求

### 健康状态显示"服务离线"
- 确认后端服务正在运行
- 检查 CORS 配置（后端已配置 @CrossOrigin）
- 查看后端日志

### API 调用失败
- 检查 API Key 是否配置正确
- 确认网络连接正常
- 查看后端控制台日志
- 检查 API 配额是否充足

## 📚 相关文档

- [后端 README.md](./README.md) - 后端项目详细说明
- [前端 README.md](./frontend/README.md) - 前端项目说明
- [向量 API 指南](./VECTOR_API_GUIDE.md) - 向量存储使用指南

## 🎊 开始使用吧！

1. 确保后端服务已启动
2. 打开浏览器访问 http://localhost:5173
3. 开始探索各种 AI 功能！

祝你使用愉快！🎉

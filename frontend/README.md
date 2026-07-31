# LangChain4j 前端演示

基于 Vue 3 + Element Plus 的交互式前端界面，用于调用 LangChain4j 后端 API。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代前端构建工具
- **Element Plus** - Vue 3 组件库
- **Axios** - HTTP 客户端

## 功能特性

- 🤖 **简单聊天** - 基础对话功能
- 🌍 **多语言翻译** - 支持多种语言互译
- ✨ **创意写作** - 根据主题和风格生成内容
- ⚙️ **自定义提示词** - 灵活配置 System Prompt
- 📊 **向量存储** - 添加文本和向量搜索
- 🔍 **RAG 问答** - 检索增强生成问答

## 快速开始

### 前置要求

- Node.js 16+
- npm 或 yarn
- 后端服务运行在 http://localhost:8080

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:5173 查看应用。

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 项目结构

```
frontend/
├── src/
│   ├── App.vue          # 主应用组件
│   ├── main.js          # 应用入口
│   ├── api.js           # API 接口封装
│   └── style.css        # 全局样式
├── index.html           # HTML 模板
├── package.json         # 项目配置
└── vite.config.js       # Vite 配置
```

## API 配置

API 基础地址默认为 `http://localhost:8080/api`，可在 `src/api.js` 中修改：

```javascript
const api = axios.create({
  baseURL: 'http://localhost:8080/api',  // 修改此处
  timeout: 60000
})
```

## 使用说明

1. 确保后端服务已启动并运行在 8080 端口
2. 启动前端开发服务器
3. 在浏览器中访问应用
4. 使用左侧导航栏切换不同功能
5. 顶部健康状态标签显示后端服务连接状态

## 注意事项

- 后端服务必须先启动才能正常使用
- 向量存储功能需要先添加文本才能进行搜索
- RAG 问答功能依赖向量存储中的数据

## 开发建议

- 使用 Vue DevTools 进行调试
- 查看浏览器控制台了解 API 请求详情
- 检查后端日志排查问题

## License

MIT

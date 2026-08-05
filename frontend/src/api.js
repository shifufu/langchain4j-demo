import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 60000
})

// 健康检查
export const healthCheck = () => api.get('/chat/health')

// 简单聊天
export const simpleChat = (message) => api.post('/chat/simple', { message })

// 流式聊天
export const streamChat = (message, onToken, onComplete, onError) => {
  const url = `http://localhost:8080/api/chat/simple/stream?message=${encodeURIComponent(message)}`
  const eventSource = new EventSource(url)
  let fullText = ''

  eventSource.onmessage = (event) => {
    if (event.data) {
      fullText += event.data
      onToken(event.data)
    }
  }

  eventSource.onerror = (error) => {
    eventSource.close()
    if (fullText) {
      onComplete(fullText)
    } else {
      onError(error)
    }
  }
}

// 翻译
export const translate = (text, language) => api.post('/chat/translate', { text, language })

// 创意写作
export const creative = (topic, tone) => api.post('/chat/creative', { topic, tone })

// 自定义提示词
export const customPrompt = (systemPrompt, userMessage) => api.post('/chat/custom', { systemPrompt, userMessage })

// 向量搜索
export const embeddingSearch = (query, maxResults, minScore) => api.post('/chat/embedding/search', { query, maxResults, minScore })

// 添加文本到向量存储
export const addToEmbeddingStore = (text) => api.post('/chat/embedding/add', { text })

// RAG 查询
export const ragQuery = (query) => api.post('/chat/embedding/rag', { query })

// 音乐推荐
export const recommendMusicByBook = (bookName) => api.post('/chat/music/recommend', { bookName })

// ==================== 新增有趣互动 ====================

// 角色扮演
export const roleplay = (character, message) => api.post('/chat/roleplay', { character, message })

// 故事生成
export const generateStory = (theme, style, elements) => api.post('/chat/story', { theme, style, elements })

// 情绪分析
export const analyzeEmotion = (text) => api.post('/chat/emotion', { text })

// 脑洞生成
export const brainstorm = (topic) => api.post('/chat/brainstorm', { topic })

export default api

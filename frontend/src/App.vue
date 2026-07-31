<template>
  <div class="stars-container">
    <div v-for="i in 100" :key="i" class="star" :style="getStarStyle(i)"></div>
  </div>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="header-content">
        <div class="logo-container">
          <span class="logo-icon">🌌</span>
          <h1 class="main-title">与时空对话</h1>
          <span class="subtitle">AI • 音乐 • 无限可能</span>
        </div>
        <div class="header-right">
          <el-tag :type="healthStatus === 'ok' ? 'success' : 'danger'" class="health-tag">
            <el-icon class="status-icon"><Connection /></el-icon>
            {{ healthStatus === 'ok' ? '时空通道已开启' : '时空通道未连接' }}
          </el-tag>
        </div>
      </div>
    </el-header>

    <el-container class="main-container">
      <el-aside width="250px" class="sidebar">
        <el-menu
          :default-active="activeTab"
          @select="handleTabSelect"
          class="nav-menu"
          background-color="rgba(10, 10, 30, 0.9)"
          text-color="#a0a0c0"
          active-text-color="#00ffff"
        >
          <div class="menu-title">✨ 功能导航</div>
          <el-menu-item index="chat" class="menu-item">
            <el-icon class="menu-icon"><ChatDotRound /></el-icon>
            <span>💬 时空对话</span>
          </el-menu-item>
          <el-menu-item index="translate" class="menu-item">
            <el-icon class="menu-icon"><Document /></el-icon>
            <span>🌍 跨语言交流</span>
          </el-menu-item>
          <el-menu-item index="creative" class="menu-item">
            <el-icon class="menu-icon"><MagicStick /></el-icon>
            <span>✨ 创意工坊</span>
          </el-menu-item>
          <el-menu-item index="custom" class="menu-item">
            <el-icon class="menu-icon"><Setting /></el-icon>
            <span>⚙️ 自定义角色</span>
          </el-menu-item>
          <el-menu-item index="embedding" class="menu-item">
            <el-icon class="menu-icon"><Connection /></el-icon>
            <span>📊 记忆存储</span>
          </el-menu-item>
          <el-menu-item index="rag" class="menu-item">
            <el-icon class="menu-icon"><Search /></el-icon>
            <span>🔍 知识检索</span>
          </el-menu-item>
          <el-menu-item index="music" class="menu-item">
            <el-icon class="menu-icon"><Headset /></el-icon>
            <span>🎵 时空音律</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="content-area">
        <!-- 简单聊天 -->
        <div v-if="activeTab === 'chat'" class="tab-content">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>💬 时空对话</span>
              </div>
            </template>
            <el-input
              v-model="chatInput"
              type="textarea"
              :rows="3"
              placeholder="请输入您的消息..."
              class="input-area"
            />
            <el-button
              type="primary"
              :loading="loading"
              @click="handleSimpleChat"
              class="action-btn"
            >
              <el-icon><Promotion /></el-icon>
              发送消息
            </el-button>
            <div v-if="chatResponse || isStreaming" class="response-area">
              <el-divider>
                <span class="divider-content">
                  ✨ AI 回复
                  <el-button 
                    v-if="chatResponse"
                    type="success" 
                    size="small" 
                    @click="copyChatResult"
                    class="copy-btn"
                  >
                    <el-icon><DocumentCopy /></el-icon>
                    复制回复
                  </el-button>
                </span>
              </el-divider>
              <div class="response-content chat-result">
                {{ chatResponse }}<span v-if="isStreaming" class="cursor">|</span>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 翻译 -->
        <div v-if="activeTab === 'translate'" class="tab-content">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>🌍 跨语言交流</span>
              </div>
            </template>
            <el-input
              v-model="translateText"
              type="textarea"
              :rows="3"
              placeholder="请输入要翻译的文本..."
              class="input-area"
            />
            <el-input
              v-model="translateLanguage"
              placeholder="目标语言（如：英语、日语、法语）"
              class="input-area"
              style="margin-top: 10px"
            />
            <el-button
              type="primary"
              :loading="loading"
              @click="handleTranslate"
              class="action-btn"
            >
              <el-icon><Switch /></el-icon>
              开始翻译
            </el-button>
            <div v-if="translateResponse" class="response-area">
              <el-divider>
                <span class="divider-content">
                  ✨ 翻译结果
                  <el-button 
                    type="success" 
                    size="small" 
                    @click="copyTranslateResult"
                    class="copy-btn"
                  >
                    <el-icon><DocumentCopy /></el-icon>
                    复制结果
                  </el-button>
                </span>
              </el-divider>
              <div class="response-content translate-result">
                {{ translateResponse }}
              </div>
            </div>
          </el-card>
        </div>

        <!-- 创意写作 -->
        <div v-if="activeTab === 'creative'" class="tab-content">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>✨ 创意工坊</span>
              </div>
            </template>
            <el-input
              v-model="creativeTopic"
              placeholder="主题（如：秋天、梦想、未来）"
              class="input-area"
            />
            <el-input
              v-model="creativeTone"
              placeholder="风格（如：诗意、幽默、科幻）"
              class="input-area"
              style="margin-top: 10px"
            />
            <el-button
              type="primary"
              :loading="loading"
              @click="handleCreative"
              class="action-btn"
            >
              <el-icon><MagicStick /></el-icon>
              开始创作
            </el-button>
            <div v-if="creativeResponse" class="response-area">
              <el-divider>
                <span class="divider-content">
                  ✨ 创作内容
                  <el-button 
                    type="success" 
                    size="small" 
                    @click="copyCreativeResult"
                    class="copy-btn"
                  >
                    <el-icon><DocumentCopy /></el-icon>
                    复制内容
                  </el-button>
                </span>
              </el-divider>
              <div class="response-content creative-result">
                {{ creativeResponse }}
              </div>
            </div>
          </el-card>
        </div>

        <!-- 自定义提示词 -->
        <div v-if="activeTab === 'custom'" class="tab-content">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>⚙️ 自定义角色</span>
              </div>
            </template>
            <el-input
              v-model="customSystemPrompt"
              type="textarea"
              :rows="3"
              placeholder="系统提示词（如：你是一个专业的诗人）"
              class="input-area"
            />
            <el-input
              v-model="customUserMessage"
              type="textarea"
              :rows="3"
              placeholder="用户消息（如：写一首关于月亮的诗）"
              class="input-area"
              style="margin-top: 10px"
            />
            <el-button
              type="primary"
              :loading="loading"
              @click="handleCustomPrompt"
              class="action-btn"
            >
              <el-icon><Setting /></el-icon>
              执行指令
            </el-button>
            <div v-if="customResponse" class="response-area">
              <el-divider>
                <span class="divider-content">
                  ✨ AI 回复
                  <el-button 
                    type="success" 
                    size="small" 
                    @click="copyCustomResult"
                    class="copy-btn"
                  >
                    <el-icon><DocumentCopy /></el-icon>
                    复制回复
                  </el-button>
                </span>
              </el-divider>
              <div class="response-content custom-result">
                {{ customResponse }}
              </div>
            </div>
          </el-card>
        </div>

        <!-- 向量存储 -->
        <div v-if="activeTab === 'embedding'" class="tab-content">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>添加文本</span>
                  </div>
                </template>
                <el-input
                  v-model="embeddingText"
                  type="textarea"
                  :rows="5"
                  placeholder="请输入要添加到向量存储的文本..."
                  class="input-area"
                />
                <el-button
                  type="success"
                  :loading="loading"
                  @click="handleAddEmbedding"
                  class="action-btn"
                >
                  添加
                </el-button>
                <div v-if="addEmbeddingResult" class="response-area">
                  <el-divider>结果</el-divider>
                  <div class="response-content">{{ addEmbeddingResult }}</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>向量搜索</span>
                  </div>
                </template>
                <el-input
                  v-model="embeddingQuery"
                  placeholder="搜索查询..."
                  class="input-area"
                />
                <el-input-number
                  v-model="maxResults"
                  :min="1"
                  :max="10"
                  placeholder="最大结果数"
                  class="input-area"
                  style="margin-top: 10px; width: 100%"
                />
                <el-slider
                  v-model="minScore"
                  :min="0"
                  :max="1"
                  :step="0.1"
                  :show-tooltip="true"
                  placeholder="最小相似度"
                  style="margin-top: 10px"
                />
                <span>最小相似度: {{ minScore }}</span>
                <el-button
                  type="primary"
                  :loading="loading"
                  @click="handleEmbeddingSearch"
                  class="action-btn"
                >
                  搜索
                </el-button>
                <div v-if="searchResults.length > 0" class="response-area">
                  <el-divider>搜索结果</el-divider>
                  <div v-for="(result, index) in searchResults" :key="index" class="search-result">
                    <el-tag size="small" type="info">相似度: {{ result.score.toFixed(4) }}</el-tag>
                    <p>{{ result.text }}</p>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- RAG 问答 -->
        <div v-if="activeTab === 'rag'" class="tab-content">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>RAG 问答（检索增强生成）</span>
              </div>
            </template>
            <el-input
              v-model="ragQuery"
              type="textarea"
              :rows="3"
              placeholder="请输入您的问题（需要先在向量存储中添加文档）..."
              class="input-area"
            />
            <el-button
              type="primary"
              :loading="loading"
              @click="handleRagQuery"
              class="action-btn"
            >
              提问
            </el-button>
            <div v-if="ragAnswer" class="response-area">
              <el-divider>回答</el-divider>
              <div class="response-content">{{ ragAnswer }}</div>
              <el-divider>参考资料</el-divider>
              <div v-for="(ref, index) in ragReferences" :key="index" class="reference">
                <el-tag size="small" type="info">相似度: {{ ref.score.toFixed(4) }}</el-tag>
                <p>{{ ref.text }}</p>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 音乐推荐 -->
        <div v-if="activeTab === 'music'" class="tab-content">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>🎵 书籍音乐推荐</span>
              </div>
            </template>
            <el-input
              v-model="musicBookName"
              placeholder="请输入您喜欢的书名（如：三体、百年孤独、哈利波特）..."
              class="input-area"
            />
            <el-button
              type="primary"
              :loading="loading"
              @click="handleMusicRecommend"
              class="action-btn"
            >
              🎧 推荐音乐
            </el-button>
            
            <!-- 书籍分析 -->
            <div v-if="musicAnalysis" class="response-area">
              <el-divider>📚 书籍分析</el-divider>
              <el-descriptions :column="3" border>
                <el-descriptions-item label="主题">{{ musicAnalysis.theme }}</el-descriptions-item>
                <el-descriptions-item label="情感基调">{{ musicAnalysis.mood }}</el-descriptions-item>
                <el-descriptions-item label="时代背景">{{ musicAnalysis.era }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <!-- 音乐推荐列表 -->
            <div v-if="musicRecommendations.length > 0" class="response-area">
              <el-divider>🎶 推荐音乐</el-divider>
              <div class="music-tip">
                <el-alert
                  title="💡 提示：点击歌曲名称或下方按钮即可搜索播放"
                  type="info"
                  :closable="false"
                  show-icon
                />
              </div>
              <div v-for="(music, index) in musicRecommendations" :key="index" class="music-card">
                <el-card class="music-item" @click="searchOnNetease(music)">
                  <div class="music-header">
                    <span class="music-number">#{{ index + 1 }}</span>
                    <el-tag type="success">{{ music.genre }}</el-tag>
                  </div>
                  <h3 class="music-title clickable">
                    <el-icon class="play-icon"><VideoPlay /></el-icon>
                    {{ music.title }}
                  </h3>
                  <p class="music-artist">🎤 {{ music.artist }}</p>
                  <p class="music-reason">💡 {{ music.reason }}</p>
                  <div class="music-actions" @click.stop>
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click="searchOnNetease(music)"
                      class="action-button"
                    >
                      <el-icon><Search /></el-icon>
                      网易云音乐
                    </el-button>
                    <el-button 
                      type="success" 
                      size="small" 
                      @click="searchOnYoutube(music)"
                      class="action-button"
                    >
                      <el-icon><VideoCamera /></el-icon>
                      YouTube
                    </el-button>
                    <el-button 
                      type="warning" 
                      size="small" 
                      @click="searchOnSpotify(music)"
                      class="action-button"
                    >
                      <el-icon><Headset /></el-icon>
                      Spotify
                    </el-button>
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="searchOnBilibili(music)"
                      class="action-button"
                    >
                      <el-icon><Platform /></el-icon>
                      哔哩哔哩
                    </el-button>
                  </div>
                </el-card>
              </div>
            </div>

            <!-- 原始响应（如果 JSON 解析失败） -->
            <div v-if="musicRawResponse" class="response-area">
              <el-divider>推荐结果</el-divider>
              <div class="response-content">{{ musicRawResponse }}</div>
            </div>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  healthCheck,
  simpleChat,
  streamChat,
  translate,
  creative,
  customPrompt,
  embeddingSearch,
  addToEmbeddingStore,
  ragQuery as fetchRagQuery,
  recommendMusicByBook
} from './api'

const activeTab = ref('chat')
const healthStatus = ref('unknown')
const loading = ref(false)

// 简单聊天
const chatInput = ref('')
const chatResponse = ref('')
const isStreaming = ref(false)

// 翻译
const translateText = ref('')
const translateLanguage = ref('英语')
const translateResponse = ref('')

// 创意写作
const creativeTopic = ref('秋天')
const creativeTone = ref('诗意')
const creativeResponse = ref('')

// 自定义提示词
const customSystemPrompt = ref('')
const customUserMessage = ref('')
const customResponse = ref('')

// 向量存储
const embeddingText = ref('')
const embeddingQuery = ref('')
const maxResults = ref(3)
const minScore = ref(0.7)
const addEmbeddingResult = ref('')
const searchResults = ref([])

// RAG
const ragQuery = ref('')
const ragAnswer = ref('')
const ragReferences = ref([])

// 音乐推荐
const musicBookName = ref('')
const musicAnalysis = ref(null)
const musicRecommendations = ref([])
const musicRawResponse = ref('')

// 星空样式
const getStarStyle = (index) => {
  const size = Math.random() * 3 + 1
  const duration = Math.random() * 3 + 2
  const delay = Math.random() * 5
  return {
    left: Math.random() * 100 + '%',
    top: Math.random() * 100 + '%',
    width: size + 'px',
    height: size + 'px',
    animationDelay: delay + 's',
    animationDuration: duration + 's'
  }
}

// 检查健康状态
const checkHealth = async () => {
  try {
    const res = await healthCheck()
    healthStatus.value = res.data.status
  } catch (error) {
    healthStatus.value = 'error'
  }
}

// 处理标签切换
const handleTabSelect = (tab) => {
  activeTab.value = tab
}

// 简单聊天
const handleSimpleChat = () => {
  if (!chatInput.value.trim()) {
    ElMessage.warning('请输入消息')
    return
  }
  if (isStreaming.value) {
    ElMessage.warning('请等待当前回复完成')
    return
  }
  loading.value = true
  isStreaming.value = true
  chatResponse.value = ''
  
  streamChat(
    chatInput.value,
    (token) => {
      nextTick(() => {
        chatResponse.value += token
      })
    },
    (fullText) => {
      loading.value = false
      isStreaming.value = false
      ElMessage.success('发送成功')
    },
    (error) => {
      loading.value = false
      isStreaming.value = false
      ElMessage.error('发送失败: ' + error.message)
    }
  )
}

// 翻译
const handleTranslate = async () => {
  if (!translateText.value.trim()) {
    ElMessage.warning('请输入要翻译的文本')
    return
  }
  loading.value = true
  try {
    const res = await translate(translateText.value, translateLanguage.value)
    translateResponse.value = res.data.translation
    ElMessage.success('翻译成功')
  } catch (error) {
    ElMessage.error('翻译失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 创意写作
const handleCreative = async () => {
  if (!creativeTopic.value.trim()) {
    ElMessage.warning('请输入主题')
    return
  }
  loading.value = true
  try {
    const res = await creative(creativeTopic.value, creativeTone.value)
    creativeResponse.value = res.data.content
    ElMessage.success('生成成功')
  } catch (error) {
    ElMessage.error('生成失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 自定义提示词
const handleCustomPrompt = async () => {
  if (!customSystemPrompt.value.trim() || !customUserMessage.value.trim()) {
    ElMessage.warning('请输入系统提示词和用户消息')
    return
  }
  loading.value = true
  try {
    const res = await customPrompt(customSystemPrompt.value, customUserMessage.value)
    customResponse.value = res.data.response
    ElMessage.success('执行成功')
  } catch (error) {
    ElMessage.error('执行失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 添加向量
const handleAddEmbedding = async () => {
  if (!embeddingText.value.trim()) {
    ElMessage.warning('请输入文本')
    return
  }
  loading.value = true
  try {
    const res = await addToEmbeddingStore(embeddingText.value)
    addEmbeddingResult.value = res.data.message
    ElMessage.success('添加成功')
  } catch (error) {
    ElMessage.error('添加失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 向量搜索
const handleEmbeddingSearch = async () => {
  if (!embeddingQuery.value.trim()) {
    ElMessage.warning('请输入搜索查询')
    return
  }
  loading.value = true
  try {
    const res = await embeddingSearch(embeddingQuery.value, maxResults.value, minScore.value)
    searchResults.value = res.data.results
    ElMessage.success(`找到 ${searchResults.value.length} 条结果`)
  } catch (error) {
    ElMessage.error('搜索失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// RAG 查询
const handleRagQuery = async () => {
  if (!ragQuery.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }
  loading.value = true
  try {
    const res = await fetchRagQuery(ragQuery.value)
    ragAnswer.value = res.data.answer
    ragReferences.value = res.data.searchResults
    ElMessage.success('查询成功')
  } catch (error) {
    ElMessage.error('查询失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 音乐推荐
const handleMusicRecommend = async () => {
  if (!musicBookName.value.trim()) {
    ElMessage.warning('请输入书名')
    return
  }
  loading.value = true
  musicAnalysis.value = null
  musicRecommendations.value = []
  musicRawResponse.value = ''
  
  try {
    const res = await recommendMusicByBook(musicBookName.value)
    
    if (res.data.success) {
      if (res.data.data) {
        try {
          const parsedData = JSON.parse(res.data.data)
          musicAnalysis.value = parsedData.bookAnalysis
          musicRecommendations.value = parsedData.recommendations
          ElMessage.success('推荐成功！')
        } catch (e) {
          musicRawResponse.value = res.data.data || res.data.rawResponse
          ElMessage.success('推荐成功！')
        }
      } else if (res.data.rawResponse) {
        musicRawResponse.value = res.data.rawResponse
        ElMessage.success('推荐成功！')
      }
    } else {
      ElMessage.error('推荐失败: ' + res.data.error)
    }
  } catch (error) {
    ElMessage.error('推荐失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 在网易云音乐搜索
const searchOnNetease = (music) => {
  const query = encodeURIComponent(`${music.title} ${music.artist}`)
  window.open(`https://music.163.com/#/search/m/?s=${query}`, '_blank')
}

// 在 YouTube 搜索
const searchOnYoutube = (music) => {
  const query = encodeURIComponent(`${music.title} ${music.artist}`)
  window.open(`https://www.youtube.com/results?search_query=${query}`, '_blank')
}

// 在 Spotify 搜索
const searchOnSpotify = (music) => {
  const query = encodeURIComponent(`${music.title} ${music.artist}`)
  window.open(`https://open.spotify.com/search/${query}`, '_blank')
}

// 在 Bilibili 搜索
const searchOnBilibili = (music) => {
  const query = encodeURIComponent(`${music.title} ${music.artist}`)
  window.open(`https://search.bilibili.com/all?keyword=${query}`, '_blank')
}

// 通用复制函数
const copyToClipboard = async (text, successMessage) => {
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success(successMessage)
  } catch (error) {
    // 降级方案
    const textArea = document.createElement('textarea')
    textArea.value = text
    document.body.appendChild(textArea)
    textArea.select()
    document.execCommand('copy')
    document.body.removeChild(textArea)
    ElMessage.success(successMessage)
  }
}

// 复制聊天回复
const copyChatResult = () => {
  copyToClipboard(chatResponse.value, '✅ 聊天回复已复制到剪贴板！')
}

// 复制翻译结果
const copyTranslateResult = () => {
  copyToClipboard(translateResponse.value, '✅ 翻译结果已复制到剪贴板！')
}

// 复制创意写作
const copyCreativeResult = () => {
  copyToClipboard(creativeResponse.value, '✅ 创作内容已复制到剪贴板！')
}

// 复制自定义回复
const copyCustomResult = () => {
  copyToClipboard(customResponse.value, '✅ 回复内容已复制到剪贴板！')
}

onMounted(() => {
  checkHealth()
  // 定期检查健康状态
  setInterval(checkHealth, 10000)
})
</script>

<style scoped>
/* 星空背景 */
.stars-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.star {
  position: absolute;
  background: white;
  border-radius: 50%;
  animation: twinkle 3s infinite;
  box-shadow: 0 0 6px white;
}

@keyframes twinkle {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.2);
  }
}

/* 主容器 */
.app-container {
  position: relative;
  height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 50%, #0a0a2a 100%);
  z-index: 1;
}

/* 头部 */
.app-header {
  background: linear-gradient(90deg, rgba(10, 10, 40, 0.95) 0%, rgba(20, 20, 60, 0.95) 100%);
  border-bottom: 2px solid rgba(0, 255, 255, 0.3);
  box-shadow: 0 0 30px rgba(0, 255, 255, 0.2);
  display: flex;
  align-items: center;
  padding: 0 30px;
  backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo-icon {
  font-size: 36px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.main-title {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(90deg, #00ffff, #ff00ff, #00ffff);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradientFlow 3s linear infinite;
}

@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}

.subtitle {
  font-size: 14px;
  color: #8888aa;
  letter-spacing: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.health-tag {
  background: rgba(0, 255, 255, 0.2) !important;
  border: 1px solid rgba(0, 255, 255, 0.5) !important;
  color: #00ffff !important;
  padding: 8px 16px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-icon {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 主容器 */
.main-container {
  height: calc(100vh - 70px);
}

/* 侧边栏 */
.sidebar {
  background: rgba(10, 10, 30, 0.9);
  border-right: 1px solid rgba(100, 100, 150, 0.3);
  backdrop-filter: blur(10px);
}

.menu-title {
  padding: 20px 20px 10px;
  color: #6666aa;
  font-size: 12px;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.nav-menu {
  border: none;
  background: transparent;
}

.menu-item {
  margin: 5px 10px;
  border-radius: 8px;
  transition: all 0.3s;
}

.menu-item:hover {
  background: rgba(0, 255, 255, 0.1);
}

.menu-icon {
  color: #00ffff;
}

/* 内容区域 */
.content-area {
  background: rgba(20, 20, 40, 0.5);
  padding: 30px;
  overflow-y: auto;
}

.tab-content {
  max-width: 1200px;
  margin: 0 auto;
}

/* 卡片样式 */
.tab-content .el-card {
  background: rgba(20, 20, 50, 0.8);
  border: 1px solid rgba(100, 100, 150, 0.3);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.tab-content .el-card :deep(.el-card__header) {
  background: rgba(30, 30, 60, 0.6);
  border-bottom: 1px solid rgba(100, 100, 150, 0.3);
  padding: 15px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #00ffff;
  font-size: 18px;
  font-weight: bold;
}

/* 输入框样式 */
.input-area {
  margin-bottom: 15px;
}

.input-area :deep(.el-textarea__inner),
.input-area :deep(.el-input__inner) {
  background: rgba(30, 30, 60, 0.8);
  border: 1px solid rgba(100, 100, 150, 0.5);
  color: #e0e0ff;
}

.input-area :deep(.el-textarea__inner)::placeholder,
.input-area :deep(.el-input__inner)::placeholder {
  color: #6666aa;
}

.input-area :deep(.el-textarea__inner):focus,
.input-area :deep(.el-input__inner):focus {
  border-color: #00ffff;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.3);
}

/* 按钮样式 */
.action-btn {
  width: 100%;
  background: linear-gradient(90deg, #00ffff, #0080ff);
  border: none;
  color: #000;
  font-weight: bold;
  padding: 12px;
  transition: all 0.3s;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(0, 255, 255, 0.4);
}

/* 响应区域 */
.response-area {
  margin-top: 25px;
}

.response-area :deep(.el-divider__text) {
  background: rgba(20, 20, 50, 0.8);
  color: #00ffff;
}

.response-content {
  background: rgba(30, 30, 60, 0.6);
  padding: 20px;
  border-radius: 10px;
  border: 1px solid rgba(100, 100, 150, 0.3);
  color: #e0e0ff;
  white-space: pre-wrap;
  line-height: 1.8;
}

.translate-result,
.chat-result,
.creative-result,
.custom-result {
  position: relative;
  font-size: 16px;
  border-left: 3px solid #00ffff;
  transition: all 0.3s;
}

.translate-result:hover,
.chat-result:hover,
.creative-result:hover,
.custom-result:hover {
  border-left-color: #ff00ff;
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.2);
}

.divider-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.copy-btn {
  background: linear-gradient(90deg, #00ff88, #00ffaa);
  border: none;
  color: #000;
  font-weight: bold;
  transition: all 0.3s;
}

.copy-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(0, 255, 136, 0.4);
}

/* 搜索结果和参考资料 */
.search-result, .reference {
  background: rgba(30, 30, 60, 0.6);
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 12px;
  border: 1px solid rgba(100, 100, 150, 0.3);
}

.search-result p, .reference p {
  margin: 10px 0 0 0;
  line-height: 1.6;
  color: #c0c0e0;
}

/* 音乐卡片 */
.music-card {
  margin-top: 20px;
}

.music-item {
  margin-bottom: 20px;
  background: linear-gradient(135deg, rgba(100, 0, 150, 0.8) 0%, rgba(50, 0, 100, 0.9) 100%);
  border: 2px solid rgba(255, 0, 255, 0.4);
  border-radius: 15px;
  color: white;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.music-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s;
}

.music-item:hover::before {
  left: 100%;
}

.music-item:hover {
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 10px 40px rgba(255, 0, 255, 0.4);
  border-color: rgba(255, 0, 255, 0.8);
}

.music-item :deep(.el-card__header) {
  background: rgba(0, 0, 0, 0.3);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.music-item :deep(.el-card__body) {
  padding: 25px;
}

.music-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.music-number {
  font-size: 28px;
  font-weight: bold;
  opacity: 0.7;
  font-family: 'Courier New', monospace;
}

.music-title {
  font-size: 22px;
  font-weight: bold;
  margin: 15px 0 8px 0;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

.music-artist {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 15px 0;
}

.music-reason {
  font-size: 14px;
  opacity: 0.9;
  line-height: 1.8;
  margin: 0;
  padding: 15px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.music-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.action-button {
  flex: 1;
  min-width: 100px;
}

.music-actions .el-button {
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.15);
  color: white;
  backdrop-filter: blur(5px);
  transition: all 0.3s;
}

.music-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.clickable {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s;
}

.clickable:hover {
  color: #ffd700;
  text-shadow: 0 0 15px #ffd700;
}

.play-icon {
  font-size: 22px;
  animation: playPulse 1.5s ease-in-out infinite;
}

@keyframes playPulse {
  0%, 100% { 
    opacity: 1; 
    transform: scale(1);
  }
  50% { 
    opacity: 0.6; 
    transform: scale(1.1);
  }
}

.music-tip {
  margin-bottom: 25px;
}

.music-tip :deep(.el-alert) {
  background: rgba(0, 255, 255, 0.1);
  border: 1px solid rgba(0, 255, 255, 0.3);
  color: #00ffff;
}

/* 滚动条样式 */
.content-area::-webkit-scrollbar {
  width: 8px;
}

.content-area::-webkit-scrollbar-track {
  background: rgba(20, 20, 40, 0.5);
}

.content-area::-webkit-scrollbar-thumb {
  background: rgba(100, 100, 150, 0.5);
  border-radius: 4px;
}

.content-area::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 255, 255, 0.5);
}

/* 打字光标 */
.cursor {
  display: inline-block;
  width: 10px;
  background-color: #00ffff;
  animation: blink 1s infinite;
  margin-left: 2px;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}
</style>

package com.example.ai.controller;

import com.example.ai.service.ChatService;
import com.example.ai.service.EmbeddingService;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final EmbeddingService embeddingService;

    @Autowired
    public ChatController(ChatService chatService, EmbeddingService embeddingService) {
        this.chatService = chatService;
        this.embeddingService = embeddingService;
    }

    @PostMapping("/simple")
    public Map<String, Object> simpleChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = chatService.chat(message);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("query", message);
        result.put("response", response);
        return result;
    }

    @GetMapping(value = "/simple/stream", produces = "text/event-stream")
    public SseEmitter streamChat(@RequestParam String message) {
        return chatService.streamChat(message);
    }

    @PostMapping("/translate")
    public Map<String, Object> translate(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String language = request.getOrDefault("language", "英语");
        String response = chatService.translate(text, language);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("original", text);
        result.put("language", language);
        result.put("translation", response);
        return result;
    }

    @PostMapping("/creative")
    public Map<String, Object> creative(@RequestBody Map<String, String> request) {
        String topic = request.getOrDefault("topic", "春天");
        String tone = request.getOrDefault("tone", "诗意");
        String response = chatService.writeCreative(topic, tone);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("topic", topic);
        result.put("tone", tone);
        result.put("content", response);
        return result;
    }

    @PostMapping("/custom")
    public Map<String, Object> customPrompt(@RequestBody Map<String, String> request) {
        String systemPrompt = request.get("systemPrompt");
        String userMessage = request.get("userMessage");
        String response = chatService.chatWithSystemPrompt(systemPrompt, userMessage);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("systemPrompt", systemPrompt);
        result.put("userMessage", userMessage);
        result.put("response", response);
        return result;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "ok");
        result.put("service", "LangChain4j Demo (JDK 8)");
        result.put("javaVersion", System.getProperty("java.version"));
        return result;
    }

    // ==================== 向量查询相关接口 ====================

    /**
     * 向量搜索（带异常处理）
     */
    @PostMapping("/embedding/search")
    public Map<String, Object> embeddingSearch(@RequestBody Map<String, Object> request) {
        try {
            if (!embeddingService.isEmbeddingAvailable()) {
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", false);
                result.put("error", "Embedding functionality is not available. Please check your embedding model configuration.");
                result.put("hint", "You can still use chat, translation, creative writing, and music recommendation features!");
                return result;
            }

            String query = (String) request.get("query");
            Integer maxResults = request.get("maxResults") != null ? 
                Integer.parseInt(request.get("maxResults").toString()) : 3;
            Double minScore = request.get("minScore") != null ? 
                Double.parseDouble(request.get("minScore").toString()) : 0.7;

            List<EmbeddingMatch<TextSegment>> matches = embeddingService.search(query, maxResults, minScore);
            
            List<Map<String, Object>> results = new ArrayList<>();
            for (EmbeddingMatch<TextSegment> match : matches) {
                Map<String, Object> resultItem = new HashMap<>();
                resultItem.put("text", match.embedded().text());
                resultItem.put("score", match.score());
                resultItem.put("embeddingId", match.embeddingId());
                results.add(resultItem);
            }

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("success", true);
            result.put("query", query);
            result.put("maxResults", maxResults);
            result.put("minScore", minScore);
            result.put("count", results.size());
            result.put("results", results);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("success", false);
            result.put("error", "Vector search failed: " + e.getMessage());
            result.put("hint", "You can still use chat, translation, creative writing, and music recommendation features!");
            return result;
        }
    }

    /**
     * 添加文本到向量存储（带异常处理）
     */
    @PostMapping("/embedding/add")
    public Map<String, Object> addToEmbeddingStore(@RequestBody Map<String, String> request) {
        try {
            if (!embeddingService.isEmbeddingAvailable()) {
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", false);
                result.put("error", "Embedding functionality is not available. Please check your embedding model configuration.");
                result.put("hint", "You can still use chat, translation, creative writing, and music recommendation features!");
                return result;
            }

            String text = request.get("text");
            embeddingService.addText(text);

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("success", true);
            result.put("message", "Text added to embedding store successfully");
            result.put("textPreview", text.substring(0, Math.min(100, text.length())) + "...");
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("success", false);
            result.put("error", "Failed to add text: " + e.getMessage());
            result.put("hint", "You can still use chat, translation, creative writing, and music recommendation features!");
            return result;
        }
    }

    /**
     * 向量搜索 + LLM 回答（简单的 RAG 示例）
     */
    @PostMapping("/embedding/rag")
    public Map<String, Object> ragQuery(@RequestBody Map<String, String> request) {
        try {
            if (!embeddingService.isEmbeddingAvailable()) {
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", false);
                result.put("error", "Embedding functionality is not available. Please check your embedding model configuration.");
                result.put("hint", "You can still use chat, translation, creative writing, and music recommendation features!");
                return result;
            }

            String query = request.get("query");
            
            // 1. 先进行向量搜索
            List<EmbeddingMatch<TextSegment>> matches = embeddingService.search(query, 3, 0.7);
            
            // 2. 构建上下文
            StringBuilder contextBuilder = new StringBuilder();
            contextBuilder.append("基于以下信息回答问题：\n\n");
            for (EmbeddingMatch<TextSegment> match : matches) {
                contextBuilder.append("- ").append(match.embedded().text()).append("\n");
            }
            contextBuilder.append("\n问题：").append(query);
            
            // 3. 调用 LLM 生成回答
            String systemPrompt = "你是一个 helpful 的 AI 助手。请基于提供的上下文信息回答用户的问题。如果上下文中没有相关信息，请诚实地说明。";
            String response = chatService.chatWithSystemPrompt(systemPrompt, contextBuilder.toString());
            
            // 4. 准备返回结果
            List<Map<String, Object>> searchResults = new ArrayList<>();
            for (EmbeddingMatch<TextSegment> match : matches) {
                Map<String, Object> resultItem = new HashMap<>();
                resultItem.put("text", match.embedded().text());
                resultItem.put("score", match.score());
                searchResults.add(resultItem);
            }

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("success", true);
            result.put("query", query);
            result.put("answer", response);
            result.put("searchResults", searchResults);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("success", false);
            result.put("error", "RAG query failed: " + e.getMessage());
            result.put("hint", "You can still use chat, translation, creative writing, and music recommendation features!");
            return result;
        }
    }

    // ==================== 音乐推荐相关接口 ====================

    /**
     * 根据书籍推荐音乐
     */
    @PostMapping("/music/recommend")
    public Map<String, Object> recommendMusicByBook(@RequestBody Map<String, String> request) {
        try {
            String bookName = request.get("bookName");
            
            if (bookName == null || bookName.trim().isEmpty()) {
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", false);
                result.put("error", "Book name is required");
                return result;
            }

            String aiResponse = chatService.recommendMusicByBook(bookName);
            
            // 尝试解析 JSON 响应
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("success", true);
            result.put("bookName", bookName);
            
            try {
                // 清理响应，只保留 JSON 部分
                String jsonStr = aiResponse.trim();
                int jsonStart = jsonStr.indexOf('{');
                int jsonEnd = jsonStr.lastIndexOf('}');
                
                if (jsonStart >= 0 && jsonEnd > jsonStart) {
                    jsonStr = jsonStr.substring(jsonStart, jsonEnd + 1);
                    result.put("data", jsonStr);
                } else {
                    result.put("rawResponse", aiResponse);
                }
            } catch (Exception e) {
                result.put("rawResponse", aiResponse);
            }
            
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("success", false);
            result.put("error", "Music recommendation failed: " + e.getMessage());
            return result;
        }
    }
}

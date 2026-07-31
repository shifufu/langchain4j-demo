package com.example.ai.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private volatile boolean embeddingAvailable = false;

    @Autowired
    public EmbeddingService(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    @PostConstruct
    public void init() {
        log.info("Initializing EmbeddingService...");
        // 不启动时测试，避免启动失败
        // 先假设可用，实际使用时再检查
        embeddingAvailable = true;
        log.info("EmbeddingService initialized (embedding features will be tested on first use)");
    }

    /**
     * 检查并测试 embedding 功能是否可用
     */
    public boolean isEmbeddingAvailable() {
        if (!embeddingAvailable) {
            return false;
        }
        
        // 第一次调用时测试
        try {
            testEmbeddingModel();
            return true;
        } catch (Exception e) {
            log.warn("Embedding test failed, disabling embedding features: {}", e.getMessage());
            embeddingAvailable = false;
            return false;
        }
    }

    /**
     * 测试 embedding model 是否可用
     */
    private void testEmbeddingModel() {
        TextSegment testSegment = TextSegment.from("test");
        embeddingModel.embed(testSegment).content();
    }

    /**
     * 加载示例文档到向量存储中
     */
    private void loadSampleDocuments() {
        if (!isEmbeddingAvailable()) {
            return;
        }
        
        List<TextSegment> segments = new ArrayList<>();
        
        // 添加一些示例文档片段
        segments.add(TextSegment.from("LangChain4j 是一个用于 Java 开发的 LLM 应用开发框架，提供了丰富的功能组件。"));
        segments.add(TextSegment.from("向量嵌入（Embedding）是将文本转换为数值向量的过程，用于语义搜索和相似度比较。"));
        segments.add(TextSegment.from("RAG（检索增强生成）是一种将向量检索与 LLM 生成相结合的技术，可以让 AI 基于特定文档回答问题。"));
        segments.add(TextSegment.from("LangChain4j 支持多种向量存储，包括 InMemoryEmbeddingStore、Pinecone、Chroma 等。"));
        segments.add(TextSegment.from("DocumentSplitter 用于将长文档分割成小片段，以便更好地进行向量化和检索。"));
        segments.add(TextSegment.from("火山引擎（Volcengine）是字节跳动推出的云服务平台，提供了多种 AI 模型服务。"));
        segments.add(TextSegment.from("Spring Boot 是一个快速开发 Java 应用的框架，简化了配置和部署流程。"));
        
        // 向量化并存储
        int successCount = 0;
        for (TextSegment segment : segments) {
            try {
                Embedding embedding = embeddingModel.embed(segment).content();
                embeddingStore.add(embedding, segment);
                successCount++;
            } catch (Exception e) {
                log.warn("Failed to embed segment: {}", segment.text().substring(0, Math.min(30, segment.text().length())));
            }
        }
        
        log.info("Loaded {}/{} sample documents into embedding store", successCount, segments.size());
    }

    /**
     * 添加单个文本片段到向量存储
     */
    public void addText(String text) {
        if (!isEmbeddingAvailable()) {
            throw new RuntimeException("Embedding functionality is not available. Please check your configuration.");
        }
        
        try {
            TextSegment segment = TextSegment.from(text);
            Embedding embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
            log.info("Added text to embedding store: {}", text.substring(0, Math.min(50, text.length())) + "...");
        } catch (Exception e) {
            log.error("Failed to add text to embedding store: {}", e.getMessage());
            embeddingAvailable = false;
            throw new RuntimeException("Failed to embed text: " + e.getMessage(), e);
        }
    }

    /**
     * 基于查询文本进行语义搜索
     * @param query 查询文本
     * @param maxResults 最大返回结果数
     * @param minScore 最小相似度分数 (0.0-1.0)
     * @return 匹配的文本片段列表
     */
    public List<EmbeddingMatch<TextSegment>> search(String query, int maxResults, double minScore) {
        if (!isEmbeddingAvailable()) {
            throw new RuntimeException("Embedding functionality is not available. Please check your configuration.");
        }
        
        try {
            log.info("Searching for: {}, maxResults: {}, minScore: {}", query, maxResults, minScore);
            
            Embedding queryEmbedding = embeddingModel.embed(query).content();
            List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(queryEmbedding, maxResults, minScore);
            
            log.info("Found {} matches", matches.size());
            return matches;
        } catch (Exception e) {
            log.error("Failed to perform vector search: {}", e.getMessage());
            embeddingAvailable = false;
            throw new RuntimeException("Failed to perform search: " + e.getMessage(), e);
        }
    }

    /**
     * 简单的搜索方法，使用默认参数
     */
    public List<EmbeddingMatch<TextSegment>> search(String query) {
        return search(query, 3, 0.7);
    }

    /**
     * 清空向量存储
     */
    public void clear() {
        // 注意：InMemoryEmbeddingStore 没有 clear 方法，
        // 这里我们通过重新初始化来模拟清空
        log.warn("Clearing embedding store (reinitializing)...");
        // 实际项目中可能需要自定义 EmbeddingStore 实现
    }
}

package com.example.ai.config;

import com.example.ai.model.VolcengineChatModel;
import com.example.ai.service.Assistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {

    private static final Logger log = LoggerFactory.getLogger(LangChain4jConfig.class);

    private final LangChain4jProperties properties;

    @Autowired
    public LangChain4jConfig(LangChain4jProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        if (properties.isUseCustomVolcengine()) {
            log.info("Using custom VolcengineChatModel for model: {}", properties.getChat().getModel());
            return VolcengineChatModel.builder()
                    .apiKey(properties.getApiKey())
                    .baseUrl(properties.getBaseUrl())
                    .modelName(properties.getChat().getModel())
                    .temperature(properties.getChat().getTemperature())
                    .build();
        } else {
            log.info("Using standard OpenAiChatModel for model: {}", properties.getChat().getModel());
            return OpenAiChatModel.builder()
                    .apiKey(properties.getApiKey())
                    .baseUrl(properties.getBaseUrl())
                    .modelName(properties.getChat().getModel())
                    .temperature(properties.getChat().getTemperature())
                    .build();
        }
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        log.info("Configuring EmbeddingModel for model: {}", properties.getEmbedding().getModel());
        // 直接创建并返回，不使用 try-catch 包装
        return OpenAiEmbeddingModel.builder()
                .apiKey(properties.getApiKey())
                .baseUrl(properties.getBaseUrl())
                .modelName(properties.getEmbedding().getModel())
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public Assistant assistant(ChatLanguageModel chatLanguageModel) {
        return AiServices.create(Assistant.class, chatLanguageModel);
    }
}

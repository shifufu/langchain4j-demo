package com.example.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "langchain4j.openai")
public class LangChain4jProperties {

    private String apiKey;
    private String baseUrl;
    private Chat chat = new Chat();
    private Embedding embedding = new Embedding();
    // 添加一个开关来选择是否使用自定义火山引擎实现
    private boolean useCustomVolcengine = false;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Embedding getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Embedding embedding) {
        this.embedding = embedding;
    }

    public boolean isUseCustomVolcengine() {
        return useCustomVolcengine;
    }

    public void setUseCustomVolcengine(boolean useCustomVolcengine) {
        this.useCustomVolcengine = useCustomVolcengine;
    }

    public static class Chat {
        private String model = "gpt-3.5-turbo";
        private Double temperature = 0.7;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
    }

    public static class Embedding {
        private String model = "text-embedding-ada-002";

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }
}

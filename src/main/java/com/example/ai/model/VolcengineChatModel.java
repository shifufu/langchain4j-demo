package com.example.ai.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class VolcengineChatModel implements ChatLanguageModel {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String apiKey;
    private final String baseUrl;
    private final String modelName;
    private final Double temperature;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public VolcengineChatModel(String apiKey, String baseUrl, String modelName, Double temperature) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.modelName = modelName;
        this.temperature = temperature;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(120))
                .writeTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("messages", convertMessages(messages));
            requestBody.put("temperature", temperature);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // 构建请求
            Request request = new Request.Builder()
                    .url(baseUrl + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(jsonBody, JSON))
                    .build();

            // 执行请求
            try (okhttp3.Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "no error body";
                    throw new RuntimeException("API request failed: " + response.code() + " - " + response.message() + " - " + errorBody);
                }

                String responseBody = response.body().string();
                Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

                // 解析响应
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    String content = (String) message.get("content");
                    return Response.from(new AiMessage(content));
                }

                throw new RuntimeException("No response from model");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to call Volcengine API", e);
        }
    }

    public void streamGenerate(List<ChatMessage> messages, Consumer<String> onToken, Consumer<String> onComplete, Consumer<Throwable> onError) {
        new Thread(() -> {
            try {
                // 构建请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", modelName);
                requestBody.put("messages", convertMessages(messages));
                requestBody.put("temperature", temperature);
                requestBody.put("stream", true);

                String jsonBody = objectMapper.writeValueAsString(requestBody);

                // 构建请求
                Request request = new Request.Builder()
                        .url(baseUrl + "/chat/completions")
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(jsonBody, JSON))
                        .build();

                // 执行请求
                try (okhttp3.Response response = okHttpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "no error body";
                        throw new RuntimeException("API request failed: " + response.code() + " - " + response.message() + " - " + errorBody);
                    }

                    StringBuilder fullResponse = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data: ")) {
                                String data = line.substring(6);
                                if ("[DONE]".equals(data)) {
                                    break;
                                }
                                try {
                                    Map<String, Object> responseMap = objectMapper.readValue(data, Map.class);
                                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                                    if (choices != null && !choices.isEmpty()) {
                                        Map<String, Object> choice = choices.get(0);
                                        Map<String, Object> delta = (Map<String, Object>) choice.get("delta");
                                        if (delta != null && delta.containsKey("content")) {
                                            String content = (String) delta.get("content");
                                            if (content != null) {
                                                fullResponse.append(content);
                                                if (onToken != null) {
                                                    onToken.accept(content);
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    // 忽略解析错误，继续处理
                                }
                            }
                        }
                    }

                    if (onComplete != null) {
                        onComplete.accept(fullResponse.toString());
                    }
                }
            } catch (Exception e) {
                if (onError != null) {
                    onError.accept(e);
                }
            }
        }).start();
    }

    private List<Map<String, String>> convertMessages(List<ChatMessage> messages) {
        List<Map<String, String>> result = new ArrayList<>();
        for (ChatMessage msg : messages) {
            Map<String, String> map = new HashMap<>();
            map.put("role", msg.type().name().toLowerCase());
            map.put("content", msg.text());
            result.add(map);
        }
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String apiKey;
        private String baseUrl;
        private String modelName;
        private Double temperature = 0.7;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public VolcengineChatModel build() {
            return new VolcengineChatModel(apiKey, baseUrl, modelName, temperature);
        }
    }
}

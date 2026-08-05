package com.example.ai.service;

import com.example.ai.model.VolcengineChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final ChatLanguageModel chatLanguageModel;
    private final VolcengineChatModel volcengineChatModel;
    private final Assistant assistant;

    @Autowired
    public ChatService(ChatLanguageModel chatLanguageModel, Assistant assistant) {
        this.chatLanguageModel = chatLanguageModel;
        this.assistant = assistant;
        if (chatLanguageModel instanceof VolcengineChatModel) {
            this.volcengineChatModel = (VolcengineChatModel) chatLanguageModel;
        } else {
            this.volcengineChatModel = null;
        }
    }

    public String chat(String message) {
        return assistant.chat(message);
    }

    public SseEmitter streamChat(String message) {
        SseEmitter emitter = new SseEmitter(60000L);
        
        if (volcengineChatModel != null) {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new SystemMessage("你是一个乐于助人的 AI 助手。请用友好、专业的语气回答问题。"));
            messages.add(new UserMessage(message));
            
            volcengineChatModel.streamGenerate(
                messages,
                token -> {
                    try {
                        emitter.send(SseEmitter.event().data(token));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                },
                fullText -> {
                    emitter.complete();
                },
                error -> {
                    emitter.completeWithError(error);
                }
            );
        } else {
            // 如果不是 VolcengineChatModel，回退到非流式
            try {
                String response = chat(message);
                emitter.send(SseEmitter.event().data(response));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }
        
        return emitter;
    }

    public String translate(String text, String language) {
        return assistant.translate(text, language);
    }

    public String writeCreative(String topic, String tone) {
        return assistant.writeCreative("开始写作", topic, tone);
    }

    public String chatWithSystemPrompt(String systemPrompt, String userMessage) {
        Response<AiMessage> response = chatLanguageModel.generate(
                new SystemMessage(systemPrompt),
                new UserMessage(userMessage)
        );
        return response.content().text();
    }

    public String recommendMusicByBook(String bookName) {
        return assistant.recommendMusicByBook("推荐音乐", bookName);
    }

    // ==================== 新增有趣互动功能 ====================

    public String roleplay(String character, String message) {
        return assistant.roleplay(message, character);
    }

    public String generateStory(String theme, String style, String elements) {
        return assistant.generateStory("开始创作", theme, style, elements);
    }

    public String analyzeEmotion(String text) {
        return assistant.analyzeEmotion(text);
    }

    public String brainstorm(String topic) {
        return assistant.brainstorm("开始脑洞", topic);
    }
}

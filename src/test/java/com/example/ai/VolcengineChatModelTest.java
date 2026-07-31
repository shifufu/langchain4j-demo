package com.example.ai;

import com.example.ai.model.VolcengineChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VolcengineChatModelTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
    public void testVolcengineChatModel() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        String baseUrl = System.getenv().getOrDefault("OPENAI_BASE_URL", "https://ark.cn-beijing.volces.com/api/coding/v3");
        String modelName = System.getenv().getOrDefault("MODEL_NAME", "volcengine-plan/doubao-seed-2.0-code");

        VolcengineChatModel model = VolcengineChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.7)
                .build();

        Response<AiMessage> response = model.generate(
                Collections.singletonList(new UserMessage("你好，请简单介绍一下你自己"))
        );

        assertNotNull(response);
        assertNotNull(response.content());
        assertNotNull(response.content().text());
        assertTrue(response.content().text().length() > 0);

        System.out.println("Response: " + response.content().text());
    }
}

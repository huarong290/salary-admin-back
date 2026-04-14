package com.salary.admin.engine;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiIntegrationTest {

    @Autowired
    private ChatModel chatModel; // Starter 会根据 yml 自动注入 Ollama 模型

    @Autowired
    private EmbeddingModel embeddingModel;
    @Test
    void testOllamaConnection() {
        String response = chatModel.chat("你好，我是 zhaozilong，请确认你的版本。");
        System.out.println("AI 响应: " + response);
        // 如果能打印出内容，说明 SpringBoot 成功通过 SDK 连上了你本地的 Ollama
    }
}
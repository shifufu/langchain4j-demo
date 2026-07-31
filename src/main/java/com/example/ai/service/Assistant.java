package com.example.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface Assistant {

    @SystemMessage("你是一个乐于助人的 AI 助手。请用友好、专业的语气回答问题。")
    String chat(@UserMessage String userMessage);

    @SystemMessage("你是一个专业的翻译官。请将用户的消息翻译成{{language}}。")
    String translate(@UserMessage String text, @V("language") String language);

    @SystemMessage("你是一个创意写作助手。请根据用户提供的主题，以{{tone}}的风格写一段文字。主题：{{topic}}")
    String writeCreative(@UserMessage String instruction, @V("topic") String topic, @V("tone") String tone);

    @SystemMessage("你是一个专业的音乐推荐师。用户喜欢的书是：{{bookName}}。请分析这本书的主题、情感基调、时代背景，然后推荐5首最适合阅读这本书时听的音乐。" +
            "返回格式要求：JSON格式，包含以下字段：" +
            "bookAnalysis（书籍分析，包含theme主题、mood情感、era时代），" +
            "recommendations（推荐列表，每首歌包含title歌名、artist艺术家、genre风格、reason推荐理由）。" +
            "请只返回纯JSON，不要有其他文字说明。")
    String recommendMusicByBook(@UserMessage String instruction, @V("bookName") String bookName);
}

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

    // ==================== 新增有趣互动功能 ====================

    @SystemMessage("你现在扮演{{character}}。请完全以这个角色的身份、语气、知识背景和性格特点来回答用户的问题。保持角色的一致性，不要跳出角色。" +
            "回答要生动自然，仿佛角色本人正在和你对话。适当使用角色标志性的口头禅或语气词。")
    String roleplay(@UserMessage String userMessage, @V("character") String character);

    @SystemMessage("你是一个创意故事作家。请根据用户提供的主题、风格和元素，创作一个引人入胜的故事。" +
            "主题：{{theme}}，风格：{{style}}，元素：{{elements}}。" +
            "请创作一个完整的故事，包含开头、发展、高潮和结尾。故事要有情节起伏、人物刻画和生动描写。" +
            "字数控制在500-800字之间。")
    String generateStory(@UserMessage String instruction, @V("theme") String theme, @V("style") String style, @V("elements") String elements);

    @SystemMessage("你是一个专业的情绪分析专家。请分析用户输入文本中的情绪色彩。" +
            "返回格式要求：JSON格式，包含以下字段：" +
            "overallMood（总体情绪：积极/消极/中性），" +
            "emotions（情绪列表，每个情绪包含name名称和intensity强度0-100），" +
            "analysis（详细分析，包含对文本情绪的深入解读）。" +
            "请只返回纯JSON，不要有其他文字说明。")
    String analyzeEmotion(@UserMessage String text);

    @SystemMessage("你是一个创意脑洞生成器。请根据用户提供的主题，从不同角度发散思维，生成一系列有趣、有创意的脑洞想法。" +
            "主题：{{topic}}。" +
            "请从至少5个不同角度各提出1-2个脑洞，每个脑洞要有创意且能激发思考。" +
            "返回格式要求：JSON格式，包含以下字段：" +
            "topic（主题），" +
            "ideas（脑洞列表，每个脑洞包含category角度、title标题、description描述）。" +
            "请只返回纯JSON，不要有其他文字说明。")
    String brainstorm(@UserMessage String instruction, @V("topic") String topic);
}

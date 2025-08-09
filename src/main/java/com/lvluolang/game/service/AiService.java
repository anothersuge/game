package com.lvluolang.game.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AiService {

    @Value("${dashscope.api.key}")
    private String apiKey;

    public String generateGameDescription(String gameName) throws ApiException, NoApiKeyException, InputRequiredException {
        // 设置API密钥
        Constants.apiKey = apiKey;

        // 创建生成器实例
        Generation gen = new Generation();

        // 构建消息
        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content("请联网搜索，为游戏" + gameName + "生成一段简短的游戏介绍，不超过150字。参考资料优先以steam，游民星空的游戏库ku.gamersky.com，百度百科描述为准，其次是其他网站的信息。如果发现这个游戏名是乱填的，就返回暂无介绍，不要瞎编")
                .build();

        // 构建请求
        GenerationParam request = GenerationParam.builder()
                .model("qwen-plus")
                .messages(Arrays.asList(userMessage))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .enableSearch(true)
                .build();

        // 发送请求并获取结果
        GenerationResult result = gen.call(request);
        
        // 返回生成的文本
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }
}
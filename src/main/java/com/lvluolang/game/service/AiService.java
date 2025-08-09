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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class AiService {

    @Value("${dashscope.api.key}")
    private String apiKey;

    /**
     * 生成游戏描述
     * @param gameName 游戏名称
     * @return 生成的游戏描述
     * @throws ApiException API异常
     * @throws NoApiKeyException 缺少API密钥异常
     * @throws InputRequiredException 输入参数异常
     */
    public String generateGameDescription(String gameName) throws ApiException, NoApiKeyException, InputRequiredException {
        log.info("开始为游戏 '{}' 生成描述", gameName);
        
        // 设置API密钥
        Constants.apiKey = apiKey;

        // 创建生成器实例
        Generation gen = new Generation();

        // 构建消息
        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content("请联网搜索,为游戏" + gameName + "生成一段简短的游戏介绍,不超过150字.如果发现这个游戏名字是乱填的不存在,就返回无介绍,不要瞎编。但是有新闻报道的即将发售的游戏可以写上新闻、测试资讯、预计发售日期等")
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
        
        String description = result.getOutput().getChoices().get(0).getMessage().getContent();
        log.info("成功为游戏 '{}' 生成描述", gameName);
        
        // 返回生成的文本
        return description;
    }
}
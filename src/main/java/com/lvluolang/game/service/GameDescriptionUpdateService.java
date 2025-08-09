package com.lvluolang.game.service;

import com.lvluolang.game.entity.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameDescriptionUpdateService {

    private final GameService gameService;
    private final AiService aiService;

    /**
     * 每小时执行一次，检查并更新游戏描述
     * 对于描述为"暂无描述"或"暂无介绍"的游戏，调用AI服务生成新的描述
     * 应用启动后延迟5分钟开始执行
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 300000)
    @Transactional
    public void updateGameDescriptions() {
        log.info("开始执行游戏描述更新任务");
        
        // 获取描述为"暂无描述"或"暂无介绍"的游戏
        List<Game> gamesWithDefaultDescription = gameService.getGamesWithDefaultDescription();
        
        // 为这些游戏生成并更新描述
        gamesWithDefaultDescription.forEach(this::generateAndUpdateGameDescription);
        
        log.info("游戏描述更新任务执行完成");
    }

    /**
     * 为单个游戏生成并更新描述
     * @param game 需要更新描述的游戏
     */
    private void generateAndUpdateGameDescription(Game game) {
        try {
            log.info("正在为游戏 '{}' 生成新描述", game.getName());
            
            // 调用AI服务生成游戏描述
            String newDescription = aiService.generateGameDescription(game.getName());
            
            // 更新游戏描述
            gameService.updateGameDescription(game.getId(), newDescription);
            
            log.info("成功为游戏 '{}' 更新描述", game.getName());
        } catch (Exception e) {
            log.error("为游戏 '{}' 生成描述时发生错误: {}", game.getName(), e.getMessage(), e);
        }
    }
}
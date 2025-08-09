package com.lvluolang.game.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * 敏感词过滤器
 */
@Component
public class SensitiveWordFilter {
    
    private final Set<String> sensitiveWords = new HashSet<>();
    
    /**
     * 构造函数，加载敏感词
     */
    public SensitiveWordFilter() {
        loadSensitiveWords();
    }
    
    /**
     * 从文件加载敏感词
     */
    private void loadSensitiveWords() {
        try {
            // Load sensitive words from file
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        sensitiveWords.add(line);
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to load sensitive words: " + e.getMessage());
        }
    }
    
    /**
     * 检查文本是否包含敏感词
     * @param text 要检查的文本
     * @return 如果包含敏感词返回true，否则返回false
     */
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        for (String word : sensitiveWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        
        return false;
    }
}
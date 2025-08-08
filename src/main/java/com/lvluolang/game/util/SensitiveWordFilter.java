package com.lvluolang.game.util;

import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class SensitiveWordFilter {
    
    private Set<String> sensitiveWords = new HashSet<>();
    
    public SensitiveWordFilter() {
        loadSensitiveWords();
    }
    
    private void loadSensitiveWords() {
        try {
            ClassPathResource resource = new ClassPathResource("sensitive-words.txt");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    sensitiveWords.add(line);
                }
            }
            
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
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
package com.lvluolang.game.aspect;

import com.lvluolang.game.annotation.SensitiveWordCheck;
import com.lvluolang.game.util.SensitiveWordFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Aspect
@Component
public class SensitiveWordFilterAspect {
    
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;
    
    @Around("@annotation(sensitiveWordCheck)")
    public Object filterSensitiveWords(ProceedingJoinPoint joinPoint, SensitiveWordCheck sensitiveWordCheck) throws Throwable {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        
        // 检查参数中是否包含敏感词
        for (Object arg : args) {
            if (arg instanceof String && sensitiveWordFilter.containsSensitiveWord((String) arg)) {
                // 如果包含敏感词，直接返回"success"，不执行原方法
                return "success";
            } else if (arg instanceof Map) {
                // 处理Map类型的参数
                Map<?, ?> map = (Map<?, ?>) arg;
                for (Object value : map.values()) {
                    if (value instanceof String && sensitiveWordFilter.containsSensitiveWord((String) value)) {
                        // 如果包含敏感词，直接返回"success"，不执行原方法
                        return "success";
                    }
                }
            }
        }
        
        // 如果不包含敏感词，继续执行原方法
        return joinPoint.proceed();
    }
}
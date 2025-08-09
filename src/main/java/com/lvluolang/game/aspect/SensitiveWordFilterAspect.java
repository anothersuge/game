package com.lvluolang.game.aspect;

import com.lvluolang.game.annotation.SensitiveWordCheck;
import com.lvluolang.game.util.SensitiveWordFilter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class SensitiveWordFilterAspect {
    
    private final SensitiveWordFilter sensitiveWordFilter;
    
    /**
     * 过滤方法参数中的敏感词
     * @param joinPoint 切点
     * @param sensitiveWordCheck 敏感词检查注解
     * @return 如果参数中包含敏感词则返回"success"，否则继续执行原方法
     * @throws Throwable 可能抛出的异常
     */
    @Around("@annotation(sensitiveWordCheck)")
    public Object filterSensitiveWords(ProceedingJoinPoint joinPoint, SensitiveWordCheck sensitiveWordCheck) throws Throwable {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        
        // 检查参数中是否包含敏感词
        for (Object arg : args) {
            if (arg instanceof String str && sensitiveWordFilter.containsSensitiveWord(str)) {
                // 如果包含敏感词，直接返回"success"，不执行原方法
                return "success";
            } else if (arg instanceof Map<?, ?> map) {
                // 处理Map类型的参数
                for (Object value : map.values()) {
                    if (value instanceof String strValue && sensitiveWordFilter.containsSensitiveWord(strValue)) {
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
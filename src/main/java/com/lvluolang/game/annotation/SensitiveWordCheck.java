package com.lvluolang.game.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveWordCheck {
    // 可以添加一些配置选项，比如指定要检查的参数索引
    int[] paramIndexes() default {};
}
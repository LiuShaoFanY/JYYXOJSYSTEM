package com.schall.jyyxblackendcommon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于方法级别的角色权限检查注解
 * 该注解用于标记需要进行角色权限检查的方法，以便在执行方法前验证调用者是否具备所需的角色
 *
 * @mustRole 角色列表，表示执行该方法所需的至少一个角色
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    String[] mustRole();



}


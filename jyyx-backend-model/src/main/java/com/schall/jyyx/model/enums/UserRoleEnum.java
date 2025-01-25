package com.schall.jyyx.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色枚举
 */
public enum UserRoleEnum {

    /**
     * 用户角色：用户
     */
    USER("用户", "user"),
    /**
     * 用户角色：超级管理员
     */
    ADMINISTRATOR("超级管理员","administrator"),
    /**
     * 用户角色：老师
     */
    TEACHER("老师","teacher"),
    /**
     * 用户角色：学生
     */
    STUDENT("学生","student"),
    /**
     * 用户角色：被封号
     */
    BAN("被封号", "ban");

    /**
     * 角色对应的文本描述
     */
    private final String text;

    /**
     * 角色对应的值
     */
    private final String value;

    /**
     * 构造方法，初始化角色的文本描述和值
     *
     * @param text 角色的文本描述
     * @param value 角色对应的值
     */
    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return 角色值的列表
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 角色对应的值
     * @return 对应的 UserRoleEnum 枚举实例，如果找不到匹配的则返回 null
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 获取角色对应的值
     *
     * @return 角色对应的值
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取角色的文本描述
     *
     * @return 角色的文本描述
     */
    public String getText() {
        return text;
    }
}

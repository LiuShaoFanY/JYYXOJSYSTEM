package com.schall.jyyx.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 教师实体类
 */
@Data
@TableName("teacher")
public class Teacher {
    private Long id; // 教师ID
    private Long user_id; // 用户ID，关联user表的id
    private String teacherId; // 教工号
    private String title; // 职称
    private String department; // 所属院系

    // 新增字段
    private String userAccount; // 账号
    private String userPassword; // 密码
    private String userName; // 用户昵称
    private String userAvatar; // 用户头像
    private String userProfile; // 用户简介
}
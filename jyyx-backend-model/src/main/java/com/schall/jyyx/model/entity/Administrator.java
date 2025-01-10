package com.schall.jyyx.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 超级管理员实体类
 */
@Data
@TableName("administrator")
public class Administrator {
    private Long id; // 超级管理员ID
    private Long user_id; // 用户ID，关联user表的id
    private String adminId; // 超级管理员编号
    private String department; // 管理部门

    // 新增字段
    private String userAccount; // 账号
    private String userPassword; // 密码
    private String userName; // 用户昵称
    private String userAvatar; // 用户头像
    private String userProfile; // 用户简介
}
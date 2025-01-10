package com.schall.jyyx.model.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学生实体类
 */
@Data
@TableName("student")
public class Student {
    private Long id; // 学生ID
    private Long user_id; // 用户ID，关联user表的id
    private String studentId; // 学号
    private String grade; // 年级
    private String major; // 专业

    // 新增字段
    private String userAccount; // 账号
    private String userPassword; // 密码
    private String userName; // 用户昵称
    private String userAvatar; // 用户头像
    private String userProfile; // 用户简介
}

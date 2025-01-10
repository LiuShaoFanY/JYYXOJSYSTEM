package com.schall.jyyx.model.dto.user;

import lombok.Data;

/**
 * 教师注册请求
 */
@Data
public class TeacherRegisterRequest {
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String teacherId;
    private String title;
    private String department;
}
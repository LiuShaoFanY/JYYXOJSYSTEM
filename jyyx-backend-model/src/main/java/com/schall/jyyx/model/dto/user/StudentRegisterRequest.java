package com.schall.jyyx.model.dto.user;

import lombok.Data;

/**
 * 学生注册请求
 */
@Data
public class StudentRegisterRequest {
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String studentId;
    private String grade;
    private String major;
}
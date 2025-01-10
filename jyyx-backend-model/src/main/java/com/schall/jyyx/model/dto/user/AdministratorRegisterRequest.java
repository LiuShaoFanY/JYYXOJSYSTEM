package com.schall.jyyx.model.dto.user;

import lombok.Data;

/**
 * 超级管理员注册请求
 */
@Data
public class AdministratorRegisterRequest {
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String adminId;
    private String department;
}
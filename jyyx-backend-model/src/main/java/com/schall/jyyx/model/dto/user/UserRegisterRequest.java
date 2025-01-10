package com.schall.jyyx.model.dto.user;
import lombok.Data;

import java.io.Serializable;
/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest {
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String userName; // 添加昵称字段
}
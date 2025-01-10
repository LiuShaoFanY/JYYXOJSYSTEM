package com.schall.jyyx.model.dto.user;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String userAccount;
    private String newPassword;
    private String checkPassword;
}
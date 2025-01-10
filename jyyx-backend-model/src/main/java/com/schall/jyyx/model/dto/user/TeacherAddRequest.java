package com.schall.jyyx.model.dto.user;

import lombok.Data;

@Data
public class TeacherAddRequest {
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String teacherId;
    private String title;
    private String department;
}
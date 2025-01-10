package com.schall.jyyx.model.dto.user;
import lombok.Data;
@Data
public class StudentAddRequest {
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String studentId;
    private String grade;
    private String major;
}
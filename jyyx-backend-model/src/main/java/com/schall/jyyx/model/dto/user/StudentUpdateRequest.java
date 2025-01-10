package com.schall.jyyx.model.dto.user;
import lombok.Data;
@Data
public class StudentUpdateRequest {
    private Long id;
    private String studentId;
    private String grade;
    private String major;
}
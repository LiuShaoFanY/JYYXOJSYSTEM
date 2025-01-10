package com.schall.jyyx.model.dto.user;
import lombok.Data;
@Data
public class TeacherUpdateRequest {
    private Long id;
    private String teacherId;
    private String title;
    private String department;
}
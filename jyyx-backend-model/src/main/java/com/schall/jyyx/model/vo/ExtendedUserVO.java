package com.schall.jyyx.model.vo;

import com.schall.jyyx.model.entity.Teacher;
import com.schall.jyyx.model.entity.Student;
import com.schall.jyyx.model.entity.Administrator;

public class ExtendedUserVO extends UserVO {
    private Teacher teacherInfo;
    private Student studentInfo;
    private Administrator administratorInfo;

    // Getters and Setters
    public Teacher getTeacherInfo() {
        return teacherInfo;
    }

    public void setTeacherInfo(Teacher teacherInfo) {
        this.teacherInfo = teacherInfo;
    }

    public Student getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(Student studentInfo) {
        this.studentInfo = studentInfo;
    }

    public Administrator getAdministratorInfo() {
        return administratorInfo;
    }

    public void setAdministratorInfo(Administrator administratorInfo) {
        this.administratorInfo = administratorInfo;
    }
}
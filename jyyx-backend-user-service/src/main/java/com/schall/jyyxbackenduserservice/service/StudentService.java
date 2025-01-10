package com.schall.jyyxbackenduserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schall.jyyx.model.entity.Student;

public interface StudentService extends IService<Student> {
    /**
     * 创建学生信息
     *
     * @param user_id 用户ID，标识唯一用户
     * @param studentId 学生学号，唯一标识学生
     * @param grade 学生年级
     * @param major 学生专业
     * @return 创建成功的学生信息实体
     */
    Student createStudent(Long user_id, String studentId, String grade, String major);

    /**
     * 根据用户ID获取学生信息
     *
     * @param user_id 用户ID，用于查询对应的学生信息
     * @return 查询到的学生信息实体，如果未找到则返回null
     */
    Student getStudentByUserId(Long user_id);

    Student getByUserId(Long userId);

    /**
     * 根据 ID 更新学生信息
     *
     * @param id        学生 ID
     * @param studentId 学号
     * @param grade     年级
     * @param major     专业
     * @return 是否更新成功
     */
    boolean updateStudentById(Long id, String studentId, String grade, String major);
}
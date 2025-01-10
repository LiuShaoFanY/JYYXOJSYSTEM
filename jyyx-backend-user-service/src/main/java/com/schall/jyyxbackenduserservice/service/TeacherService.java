package com.schall.jyyxbackenduserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schall.jyyx.model.entity.Teacher;

public interface TeacherService extends IService<Teacher> {
    /**
     * 创建教师信息
     *
     * @param user_id     用户ID
     * @param teacherId  教师ID
     * @param title      教师职称
     * @param department 教师所属部门
     * @return 创建的教师对象
     */
    Teacher createTeacher(Long user_id, String teacherId, String title, String department);

    /**
     * 根据用户ID获取教师信息
     *
     * @param user_id 用户ID
     * @return 对应的教师对象，如果不存在则返回null
     */
    Teacher getTeacherByUserId(Long user_id);
    Teacher getByUserId(Long userId);

    /**
     * 根据 ID 更新教师信息
     *
     * @param id         教师 ID
     * @param teacherId  教师编号
     * @param title      职称
     * @param department 部门
     * @return 是否更新成功
     */
    boolean updateTeacherById(Long id, String teacherId, String title, String department);
}
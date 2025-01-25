package com.schall.jyyxbackenduserservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schall.jyyx.model.entity.Teacher;
import com.schall.jyyx.model.entity.User;
import com.schall.jyyx.model.enums.UserRoleEnum;
import com.schall.jyyxbackenduserservice.mapper.TeacherMapper;
import com.schall.jyyxbackenduserservice.service.TeacherService;
import com.schall.jyyxbackenduserservice.service.UserService;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    @Lazy // 使用 @Lazy 注解打破循环依赖
    private UserService userService; // 注入 UserService

    @Override
    public Teacher createTeacher(Long user_id, String teacherId, String title, String department) {
        log.info("创建教师信息: user_id={}, teacherId={}, title={}, department={}", user_id, teacherId, title, department);

        // 校验
        if (user_id == null || teacherId == null || teacherId.isEmpty() ) {
            log.error("参数为空，user_id={}, teacherId={}", user_id, teacherId);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 检查 teacherId 是否已存在
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacherId", teacherId);
        if (this.getOne(queryWrapper) != null) {
            log.error("教工号已存在，teacherId={}", teacherId);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "教工号已存在");
        }

        // 获取用户信息
        User user = userService.getById(user_id);
        if (user == null) {
            log.error("用户不存在，user_id={}", user_id);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 创建教师信息
        Teacher teacher = new Teacher();
        teacher.setUser_id(user_id);
        teacher.setTeacherId(teacherId);
        teacher.setTitle(title);
        teacher.setDepartment(department);
        teacher.setUserAccount(user.getUserAccount()); // 设置 userAccount
        teacher.setUserPassword(user.getUserPassword()); // 设置 userPassword

        // 插入教师信息
        boolean save = this.save(teacher);
        if (!save) {
            log.error("保存教师信息失败: user_id={}, teacherId={}", user_id, teacherId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建教师信息失败");
        }

        log.info("教师信息创建成功: user_id={}, teacherId={}", user_id, teacherId);
        return teacher;
    }
    @Override
    public Teacher getTeacherByUserId(Long user_id) {
        if (user_id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user_id);
        return this.getOne(queryWrapper);
    }

    @Override
    public Teacher getByUserId(Long userId) {
        return teacherMapper.selectByUserId(userId);
    }



    @Override
    public boolean updateById(Teacher teacher) {
        if (teacher == null || teacher.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "教师信息为空或ID为空");
        }
        return super.updateById(teacher);
    }

    @Override
    @Transactional
    public boolean updateTeacherById(Long id, String teacherId, String title, String department) {
        log.info("更新教师信息: id={}, teacherId={}, title={}, department={}", id, teacherId, title, department);

        // 查询用户是否存在且角色为教师
        User user = userService.getById(id);
        if (user == null || !UserRoleEnum.TEACHER.getValue().equals(user.getUserRole())) {
            log.error("用户不存在或不是教师，id={}", id);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在或不是教师");
        }

        // 查询教师是否存在
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        Teacher teacher = this.getOne(queryWrapper);
        if (teacher == null) {
            log.error("教师不存在，id={}", id);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "教师不存在");
        }

        // 更新教师信息
        teacher.setTeacherId(teacherId);
        teacher.setTitle(title);
        teacher.setDepartment(department);

        // 执行更新
        boolean result = this.updateById(teacher);
        if (!result) {
            log.error("教师信息更新失败，id={}", id);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "教师信息更新失败");
        }

        log.info("教师信息更新成功，id={}", id);
        return true;
    }


}
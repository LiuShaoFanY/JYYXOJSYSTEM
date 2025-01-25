package com.schall.jyyxbackenduserservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schall.jyyx.model.entity.Student;
import com.schall.jyyx.model.entity.User;
import com.schall.jyyx.model.enums.UserRoleEnum;
import com.schall.jyyxbackenduserservice.mapper.StudentMapper;
import com.schall.jyyxbackenduserservice.service.StudentService;
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
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Resource
    private StudentMapper studentMapper;
    @Resource
    @Lazy // 使用 @Lazy 注解打破循环依赖
    private UserService userService;



    @Override
    public Student createStudent(Long user_id, String studentId, String grade, String major) {
        log.info("创建学生信息: user_id={}, studentId={}, grade={}, major={}", user_id, studentId, grade, major);

        // 校验
        if (user_id == null || studentId == null || studentId.isEmpty() ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 检查 studentId 是否已存在
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("studentId", studentId);
        if (this.getOne(queryWrapper) != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学号已存在");
        }

        // 获取用户信息
        User user = userService.getById(user_id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 创建学生信息
        Student student = new Student();
        student.setUser_id(user_id);
        student.setStudentId(studentId);
        student.setGrade(grade);
        student.setMajor(major);
        student.setUserAccount(user.getUserAccount()); // 设置 userAccount
        student.setUserPassword(user.getUserPassword()); // 设置 userPassword
        student.setUserName(user.getUserName()); // 设置昵称

        boolean save = this.save(student);
        if (!save) {
            log.error("保存学生信息失败: user_id={}, studentId={}", user_id, studentId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建学生信息失败");
        }

        log.info("学生信息创建成功: user_id={}, studentId={}", user_id, studentId);
        return student;
    }

    @Override
    public Student getStudentByUserId(Long user_id) {
        log.info("Fetching student with user_id: {}", user_id);
        if (user_id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user_id);
        Student student = this.getOne(queryWrapper);
        if (student == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到对应的学生信息");
        }

        // 获取用户的昵称
        User user = userService.getById(user_id);
        if (user != null) {
            student.setUserName(user.getUserName()); // 设置昵称
        }

        return student;
    }
    @Override
    public Student getByUserId(Long userId) {
        return studentMapper.selectByUserId(userId);
    }


    @Override
    public boolean updateById(Student student) {
        if (student == null || student.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生信息为空或ID为空");
        }
        return super.updateById(student);
    }

    @Override
    @Transactional
    public boolean updateStudentById(Long id, String studentId, String grade, String major) {
        log.info("更新学生信息: id={}, studentId={}, grade={}, major={}", id, studentId, grade, major);

        // 参数校验
        if (id == null || studentId == null || grade == null || major == null) {
            log.error("参数不能为空: id={}, studentId={}, grade={}, major={}", id, studentId, grade, major);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        // 查询用户是否存在且角色为学生
        User user = userService.getById(id);
        if (user == null || !UserRoleEnum.STUDENT.getValue().equals(user.getUserRole())) {
            log.error("用户不存在或不是学生，id={}", id);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在或不是学生");
        }

        // 查询学生是否存在
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        Student student = this.getOne(queryWrapper);
        if (student == null) {
            log.error("学生不存在，id={}", id);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学生不存在");
        }

        // 更新学生信息
        student.setStudentId(studentId);
        student.setGrade(grade);
        student.setMajor(major);

        // 执行更新
        boolean result = this.updateById(student);
        if (!result) {
            log.error("学生信息更新失败，id={}", id);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "学生信息更新失败");
        }

        log.info("学生信息更新成功，id={}", id);
        return true;
    }

}
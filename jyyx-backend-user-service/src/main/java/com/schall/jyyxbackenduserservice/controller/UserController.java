package com.schall.jyyxbackenduserservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schall.jyyx.model.dto.user.*;
import com.schall.jyyx.model.entity.*;
import com.schall.jyyx.model.vo.ExtendedUserVO;
import com.schall.jyyx.model.vo.LoginUserVO;
import com.schall.jyyx.model.vo.UserVO;
import com.schall.jyyxbackenduserservice.mapper.TeacherMapper;
import com.schall.jyyxbackenduserservice.service.*;
import com.schall.jyyxblackendcommon.annotation.AuthCheck;
import com.schall.jyyxblackendcommon.common.BaseResponse;
import com.schall.jyyxblackendcommon.common.DeleteRequest;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.common.ResultUtils;
import com.schall.jyyxblackendcommon.constant.UserConstant;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import com.schall.jyyxblackendcommon.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private TeacherService teacherService;

    @Resource
    private StudentService studentService;
    @Resource
    private AdministratorService administratorService;
    @Resource
    private TeacherMapper teacherMapper;


    @Resource
    private AnnouncementService announcementService; // 注入公告服务
    // region 教师相关接口

    /**
     * 根据 user_id 获取教师信息
     *
     * @param user_id  用户ID
     * @param request HTTP 请求对象
     * @return 教师信息
     */
    @GetMapping("/teacher/{user_id}")
    public BaseResponse<Teacher> getTeacherByUserId(@PathVariable Long user_id, HttpServletRequest request) {
        // 参数校验
        if (user_id == null || user_id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }

        // 调用 userService 获取教师信息
        Teacher teacher = userService.getTeacherByUserId(user_id);

        // 检查教师信息是否存在
        if (teacher == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "教师信息不存在");
        }

        // 返回成功结果
        return ResultUtils.success(teacher);
    }

    /**
     * 教师注册
     *
     * @param request 教师注册请求
     * @return 注册成功的用户 ID
     */
    @PostMapping("/teacher/register")
    public BaseResponse<Long> teacherRegister(@RequestBody TeacherRegisterRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = request.getUserAccount();
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();
        String teacherId = request.getTeacherId();
        String title = request.getTitle();
        String department = request.getDepartment();

        // 调用教师注册服务
        long result = userService.teacherRegister(userAccount, userPassword, checkPassword,
                teacherId, title, department);
        return ResultUtils.success(result);
    }

    @PostMapping("/teacher/login")
    public BaseResponse<LoginUserVO> teacherLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.teacherLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    // endregion

    // region 学生相关接口

    /**
     * 学生注册
     *
     * @param request 学生注册请求
     * @return 注册成功的用户 ID
     */
    @PostMapping("/student/register")
    public BaseResponse<Long> studentRegister(@RequestBody StudentRegisterRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = request.getUserAccount();
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();
        String studentId = request.getStudentId();
        String grade = request.getGrade();
        String major = request.getMajor();

        // 调用学生注册服务
        long result = userService.studentRegister(userAccount, userPassword, checkPassword,
                studentId, grade, major);
        return ResultUtils.success(result);
    }

    @GetMapping("/student/{user_id}")
    public BaseResponse<Student> getStudentByUserId(@PathVariable Long user_id, HttpServletRequest request) {
        // 参数校验
        if (user_id == null || user_id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }

        // 调用 userService 获取学生信息
        Student student = userService.getStudentByUserId(user_id);

        // 检查学生信息是否存在
        if (student == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学生信息不存在");
        }

        // 返回成功结果
        return ResultUtils.success(student);
    }

    @PostMapping("/student/login")
    public BaseResponse<LoginUserVO> studentLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.studentLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }



    // endregion

    // region 管理员相关接口

    /**
     * 管理员注册
     *
     * @param request 管理员注册请求
     * @return 注册成功的用户 ID
     */
    @PostMapping("/administrator/register")
    public BaseResponse<Long> administratorRegister(@RequestBody AdministratorRegisterRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = request.getUserAccount();
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();
        String adminId = request.getAdminId();
        String department = request.getDepartment();

        // 调用管理员注册服务
        long result = userService.administratorRegister(userAccount, userPassword, checkPassword, adminId, department);
        return ResultUtils.success(result);
    }

    /**
     * 管理员登录
     *
     * @param
     * @param
     * @param request      HTTP 请求
     * @return 登录用户信息
     */
    @PostMapping("/administrator/login")
    public BaseResponse<LoginUserVO> administratorLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 检查请求参数是否为空
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 获取账号和密码
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();



        // 检查账号和密码是否为空
        if (userAccount == null || userPassword == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }

        // 调用 userService 进行超级管理员登录
        LoginUserVO loginUserVO = userService.administratorLogin(userAccount, userPassword,request);


        // 返回成功结果
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 根据 user_id 获取管理员信息
     *
     * @param user_id  用户ID
     * @param request HTTP 请求对象
     * @return 管理员信息
     */
    @GetMapping("/administrator/{user_id}")
    public BaseResponse<Administrator> getAdministratorByUserId(@PathVariable Long user_id, HttpServletRequest request) {
        // 参数校验
        if (user_id == null || user_id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }

        // 调用 userService 获取管理员信息
        Administrator administrator = userService.getAdministratorByUserId(user_id);

        // 检查管理员信息是否存在
        if (administrator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "管理员信息不存在");
        }

        // 返回成功结果
        return ResultUtils.success(administrator);
    }


    // endregion

    // region 用户相关接口

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 注册成功的用户 ID
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String userName = userRegisterRequest.getUserName();
        long result = userService.userRegister(userAccount, userPassword, checkPassword, userName);
        return ResultUtils.success(result);
    }
    /**
     * 用户登录
     *
     * @param
     * @param request          HTTP 请求
     * @return 登录用户信息
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestParam String userAccount, @RequestParam String userPassword, HttpServletRequest request) {
        if (userAccount == null || userPassword == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }
    /**
     * 用户注销
     *
     * @param request HTTP 请求
     * @return 是否注销成功
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request HTTP 请求
     * @return 当前登录用户信息
     */

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }
        LoginUserVO loginUserVO = userService.getLoginUserVO(user);
        return ResultUtils.success(loginUserVO);
    }

    // endregion

    // region 增删改查接口

    /**
     * 创建用户
     *
     * @param userAddRequest 用户添加请求
     * @param request        HTTP 请求
     * @return 创建的用户 ID
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        // 检查用户添加请求是否为空
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建一个新的用户对象
        User user = new User();
        // 将用户添加请求中的属性复制到用户对象中
        BeanUtils.copyProperties(userAddRequest, user);
        // 保存用户对象
        boolean result = userService.save(user);
        // 如果保存失败，抛出异常
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回成功响应，包含用户 ID
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户（仅管理员或教师权限）
     *
     * @param deleteRequest 删除请求
     * @param request       HTTP 请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = {UserConstant.ADMINISTRATOR_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest 用户更新请求
     * @param request           HTTP 请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = {UserConstant.ADMINISTRATOR_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 ID 获取用户（仅管理员）
     *
     * @param id      用户 ID
     * @param request HTTP 请求
     * @return 用户信息
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 ID 获取用户包装类
     *
     * @param id      用户 ID
     * @param request HTTP 请求
     * @return 用户包装类信息
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        BaseResponse<User> response = getUserById(id, request);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest 用户查询请求
     * @param request          HTTP 请求
     * @return 用户分页列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                   HttpServletRequest request) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户包装类列表
     *
     * @param userQueryRequest 用户查询请求
     * @param request          HTTP 请求
     * @return 用户包装类分页列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                       HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest 用户更新请求
     * @param request             HTTP 请求
     * @return 是否更新成功
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
                                              HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    /**
     * 重置密码接口
     *
     * @param resetPasswordRequest 重置密码请求
     * @return 是否重置成功
     */
    @PostMapping("/resetPassword")
    public BaseResponse<Boolean> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        if (resetPasswordRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = resetPasswordRequest.getUserAccount();
        String newPassword = resetPasswordRequest.getNewPassword();
        String checkPassword = resetPasswordRequest.getCheckPassword();

        // 调用 UserService 的 resetPassword 方法
        boolean result = userService.resetPassword(userAccount, newPassword, checkPassword);
        return ResultUtils.success(result);
    }


    // region 超级管理员对教师和学生的增删改查接口

    /**
     * 超级管理员添加教师
     *
     * @param teacherAddRequest 教师添加请求
     * @param request           HTTP 请求
     * @return 添加的教师 ID
     */
    @PostMapping("/administrator/addTeacher")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    public BaseResponse<Long> administratorAddTeacher(@RequestBody TeacherAddRequest teacherAddRequest, HttpServletRequest request) {
        if (teacherAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.teacherRegister(teacherAddRequest.getUserAccount(), teacherAddRequest.getUserPassword(),
                teacherAddRequest.getCheckPassword(), teacherAddRequest.getTeacherId(), teacherAddRequest.getTitle(),
                teacherAddRequest.getDepartment());
        return ResultUtils.success(result);
    }

    @PostMapping("/administrator/deleteTeacher")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    @Transactional // 添加事务注解
    public BaseResponse<Boolean> administratorDeleteTeacher(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("尝试删除用户，ID: {}", deleteRequest.getId());
        // 检查用户是否存在
        User user = userService.getById(deleteRequest.getId());
        if (user == null) {
            log.error("用户不存在，ID: {}", deleteRequest.getId());
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        // 删除用户
        boolean userResult = userService.removeById(deleteRequest.getId());
        if (!userResult) {
            log.error("删除用户失败，ID: {}", deleteRequest.getId());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除用户失败");
        }
        log.info("用户删除成功，ID: {}", deleteRequest.getId());
        // 删除关联的教师信息
        QueryWrapper<Teacher> teacherQueryWrapper = new QueryWrapper<>();
        teacherQueryWrapper.eq("user_id", deleteRequest.getId());
        boolean teacherResult = teacherService.remove(teacherQueryWrapper);
        if (!teacherResult) {
            log.error("删除教师信息失败，ID: {}", deleteRequest.getId());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除教师信息失败");
        }
        log.info("教师信息删除成功，ID: {}", deleteRequest.getId());
        return ResultUtils.success(true);
    }

    /**
     * 超级管理员更新教师信息
     *
     * @param teacherUpdateRequest 教师更新请求
     * @param request              HTTP 请求
     * @return 是否更新成功
     */
    @PostMapping("/administrator/updateTeacher")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    public BaseResponse<Boolean> administratorUpdateTeacher(@RequestBody TeacherUpdateRequest teacherUpdateRequest,
                                                            HttpServletRequest request) {
        if (teacherUpdateRequest == null || teacherUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean result = teacherService.updateTeacherById(
                teacherUpdateRequest.getId(),
                teacherUpdateRequest.getTeacherId(),
                teacherUpdateRequest.getTitle(),
                teacherUpdateRequest.getDepartment()
        );

        return ResultUtils.success(result);
    }

    /**
     * 超级管理员获取教师信息
     *
     * @param id      教师 ID
     * @param request HTTP 请求
     * @return 教师信息
     */
    @GetMapping("/administrator/getTeacher")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    public BaseResponse<Teacher> administratorGetTeacherById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Teacher teacher = teacherService.getById(id);
        ThrowUtils.throwIf(teacher == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(teacher);
    }

    /**
     * 超级管理员添加学生
     *
     * @param studentAddRequest 学生添加请求
     * @param request           HTTP 请求
     * @return 添加的学生 ID
     */
    @PostMapping("/administrator/addStudent")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    public BaseResponse<Long> administratorAddStudent(@RequestBody StudentAddRequest studentAddRequest, HttpServletRequest request) {
        if (studentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.studentRegister(studentAddRequest.getUserAccount(), studentAddRequest.getUserPassword(),
                studentAddRequest.getCheckPassword(), studentAddRequest.getStudentId(), studentAddRequest.getGrade(),
                studentAddRequest.getMajor());
        return ResultUtils.success(result);
    }

    /**
     * 超级管理员删除学生
     *
     * @param deleteRequest 删除请求
     * @param request       HTTP 请求
     * @return 是否删除成功
     */
    @PostMapping("/administrator/deleteStudent")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    @Transactional // 添加事务注解
    public BaseResponse<Boolean> administratorDeleteStudent(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            log.error("删除学生失败：参数无效，deleteRequest={}", deleteRequest);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("开始删除学生，用户ID: {}", deleteRequest.getId());

        // 检查用户是否存在
        User user = userService.getById(deleteRequest.getId());
        if (user == null) {
            log.error("删除学生失败：用户不存在，用户ID: {}", deleteRequest.getId());
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        log.info("找到用户，用户ID: {}", deleteRequest.getId());

        // 删除用户
        boolean userResult = userService.removeById(deleteRequest.getId());
        if (!userResult) {
            log.error("删除学生失败：删除用户失败，用户ID: {}", deleteRequest.getId());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除用户失败");
        }
        log.info("用户删除成功，用户ID: {}", deleteRequest.getId());

        // 删除关联的学生信息
        QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
        studentQueryWrapper.eq("user_id", deleteRequest.getId());
        boolean studentResult = studentService.remove(studentQueryWrapper);
        if (!studentResult) {
            log.error("删除学生失败：删除学生信息失败，用户ID: {}", deleteRequest.getId());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除学生信息失败");
        }
        log.info("学生信息删除成功，用户ID: {}", deleteRequest.getId());

        return ResultUtils.success(true);
    }

    /**
     * 超级管理员更新学生信息
     *
     * @param studentUpdateRequest 学生更新请求
     * @param request              HTTP 请求
     * @return 是否更新成功
     */
    @PostMapping("/administrator/updateStudent")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    public BaseResponse<Boolean> administratorUpdateStudent(@RequestBody StudentUpdateRequest studentUpdateRequest,
                                                            HttpServletRequest request) {
        if (studentUpdateRequest == null || studentUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean result = studentService.updateStudentById(
                studentUpdateRequest.getId(),
                studentUpdateRequest.getStudentId(),
                studentUpdateRequest.getGrade(),
                studentUpdateRequest.getMajor()
        );

        return ResultUtils.success(result);
    }

    /**
     * 超级管理员获取学生信息
     *
     * @param id      学生 ID
     * @param request HTTP 请求
     * @return 学生信息
     */
    @GetMapping("/administrator/getStudent")
    @AuthCheck(mustRole = UserConstant.ADMINISTRATOR_ROLE)
    public BaseResponse<Student> administratorGetStudentById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Student student = studentService.getById(id);
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(student);
    }

    @GetMapping("/administrator/getAllUsers")
    @AuthCheck(mustRole = "administrator")
    public BaseResponse<List<ExtendedUserVO>> getAllUsers(HttpServletRequest request) {
        // 获取所有用户
        List<User> users = userService.list();

        // 转换为 ExtendedUserVO 列表
        List<ExtendedUserVO> userVOs = users.stream()
                .map(user -> {
                    ExtendedUserVO userVO = new ExtendedUserVO();
                    BeanUtils.copyProperties(user, userVO); // 复制 User 的基本信息到 ExtendedUserVO

                    // 根据用户角色补充详细信息
                    if ("teacher".equals(user.getUserRole())) {
                        Teacher teacher = teacherService.getByUserId(user.getId());
                        userVO.setTeacherInfo(teacher);
                    } else if ("student".equals(user.getUserRole())) {
                        Student student = studentService.getByUserId(user.getId());
                        userVO.setStudentInfo(student);
                    } else if ("administrator".equals(user.getUserRole())) {
                        Administrator administrator = administratorService.getByUserId(user.getId());
                        userVO.setAdministratorInfo(administrator);
                    }
                    return userVO;
                })
                .collect(Collectors.toList());

        return ResultUtils.success(userVOs);
    }



    // ==================== 公告相关接口 ====================

    /**
     * 添加公告
     *
     * @param announcement 公告信息
     * @return 是否添加成功
     */
    @PostMapping("/announcement/add")
    @AuthCheck(mustRole = {UserConstant.ADMINISTRATOR_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Boolean> addAnnouncement(@RequestBody Announcement announcement) {
        // 检查公告对象是否为空
        if (announcement == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用服务层方法创建公告
        boolean result = announcementService.createAnnouncement(announcement);
        // 返回公告创建结果
        return ResultUtils.success(result);
    }

    /**
     * 更新公告
     *
     * @param announcement 公告信息
     * @return 是否更新成功
     */
    @PostMapping("/announcement/update")
    @AuthCheck(mustRole = {UserConstant.ADMINISTRATOR_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Boolean> updateAnnouncement(@RequestBody Announcement announcement) {
        if (announcement == null || announcement.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = announcementService.updateAnnouncement(announcement);
        return ResultUtils.success(result);
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 是否删除成功
     */
    @DeleteMapping("/announcement/delete/{id}")
    @AuthCheck(mustRole = {UserConstant.ADMINISTRATOR_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Boolean> deleteAnnouncement(@PathVariable Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = announcementService.deleteAnnouncement(id);
        return ResultUtils.success(result);
    }
    /**
     * 获取公告列表
     *
     * @return 公告列表
     */
    @GetMapping("/announcement/list")
    public BaseResponse<List<Announcement>> listAnnouncements() {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        return ResultUtils.success(announcements);
    }



// endregion

}
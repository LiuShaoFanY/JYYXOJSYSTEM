package com.schall.jyyxbackenduserservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schall.jyyx.model.dto.user.UserQueryRequest;
import com.schall.jyyx.model.entity.*;
import com.schall.jyyx.model.enums.UserRoleEnum;
import com.schall.jyyx.model.vo.LoginUserVO;
import com.schall.jyyx.model.vo.UserVO;
import com.schall.jyyxbackenduserservice.mapper.FeedbackAndGuidanceMapper;
import com.schall.jyyxbackenduserservice.mapper.PersonalizedFeedbackMapper;
import com.schall.jyyxbackenduserservice.mapper.TeachingGuidanceMapper;
import com.schall.jyyxbackenduserservice.mapper.UserMapper;
import com.schall.jyyxbackenduserservice.service.AdministratorService;
import com.schall.jyyxbackenduserservice.service.StudentService;
import com.schall.jyyxbackenduserservice.service.TeacherService;
import com.schall.jyyxbackenduserservice.service.UserService;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.constant.CommonConstant;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import com.schall.jyyxblackendcommon.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.schall.jyyxblackendcommon.constant.UserConstant.USER_LOGIN_STATE;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    @Lazy
    private TeacherService teacherService;

    @Resource
    @Lazy
    private StudentService studentService;

    @Resource
    @Lazy
    private AdministratorService administratorService;
    @Resource
    private PersonalizedFeedbackMapper personalizedFeedbackMapper;

    @Resource
    private TeachingGuidanceMapper teachingGuidanceMapper;

    @Resource
    private FeedbackAndGuidanceMapper feedbackAndGuidanceMapper; // 注入 FeedbackAndGuidanceMapper
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "schall";

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (userAccount != null && userPassword != null) {
            String encryptPassword = DigestUtils.md5DigestAsHex(("schall" + userPassword).getBytes());
            QueryWrapper<User> queryWrapper = new QueryWrapper();
            queryWrapper.eq("userAccount", userAccount);
            queryWrapper.eq("userPassword", encryptPassword);
            User user = (User)this.getOne(queryWrapper);
            if (user == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
            } else {
                LoginUserVO loginUserVO = new LoginUserVO();
                loginUserVO.setUser_id(user.getId()); // 设置 user_id
                loginUserVO.setUserName(user.getUserName());
                loginUserVO.setUserRole(user.getUserRole());
                request.getSession().setAttribute(USER_LOGIN_STATE, user);
                return loginUserVO;
            }
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }
    }

    @Override
    public LoginUserVO teacherLogin(String userAccount, String userPassword, HttpServletRequest request) {
        LoginUserVO loginUserVO = userLogin(userAccount, userPassword, request);

        // 检查用户角色是否为教师
        if (!UserRoleEnum.TEACHER.getValue().equals(loginUserVO.getUserRole())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "教师");
        }

        return loginUserVO;
    }

    @Override
    public LoginUserVO studentLogin(String userAccount, String userPassword, HttpServletRequest request) {
        LoginUserVO loginUserVO = userLogin(userAccount, userPassword, request);

        // 检查用户角色是否为学生
        if (!UserRoleEnum.STUDENT.getValue().equals(loginUserVO.getUserRole())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生");
        }

        return loginUserVO;
    }

    @Override
    public LoginUserVO administratorLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 调用通用的用户登录方法
        LoginUserVO loginUserVO = userLogin(userAccount, userPassword, request);

        // 检查用户角色是否为管理员
        if (!UserRoleEnum.ADMINISTRATOR.getValue().equals(loginUserVO.getUserRole())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不是超级管理员");
        }

        // 获取登录用户信息
        User user = getLoginUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 设置 user_id
        loginUserVO.setUser_id(user.getId());

        // 进一步验证管理员信息
        Administrator administrator = administratorService.getAdministratorByUserId(user.getId());
        if (administrator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员信息不存在");
        }

        // 将管理员信息设置到登录用户VO中
        loginUserVO.setDepartment(administrator.getDepartment()); // 设置管理部门
        loginUserVO.setUserAccount(administrator.getUserAccount()); // 设置账号
        loginUserVO.setUserName(administrator.getUserName()); // 设置用户昵称
        System.out.println("管理员信息：" + administrator);

        return loginUserVO;
    }


    /**
     * 获取当前登录的用户信息
     *
     * 此方法用于从HTTP请求的会话中获取当前登录的用户信息如果会话中没有用户信息，
     * 则抛出一个商业异常，表明用户未登录
     *
     * @param request HttpServletRequest对象，用于获取会话中的用户信息
     * @return User对象，表示当前登录的用户
     * @throws BusinessException 如果会话中没有用户信息，抛出此异常
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 从 session 中获取用户对象
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            // 如果用户对象为null，表示用户未登录，抛出异常
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }
        return user;
    }

    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User)userObj;
        if (currentUser != null && currentUser.getId() != null) {
            long user_id = currentUser.getId();
            return (User)this.getById(user_id);
        } else {
            return null;
        }
    }

    @Override
    public boolean isAdministrator(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdministrator(user);
    }

    @Override
    public boolean isAdministrator(User user) {
        return user != null && UserRoleEnum.ADMINISTRATOR.getValue().equals(user.getUserRole());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        loginUserVO.setUserName(user.getUserName()); // 填充昵称
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (userList == null || userList.isEmpty()) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String userName) {
        log.info("注册用户: userAccount={}", userAccount);
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (StringUtils.isBlank(userName)) {
            userName = userAccount; // 如果 userName 为空，使用 userAccount 作为默认值
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        // 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(userName);
        user.setUserRole(UserRoleEnum.USER.getValue()); // 默认角色为用户
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }
    @Override
    public long teacherRegister(String userAccount, String userPassword, String checkPassword,
                                String teacherId, String title, String department) {
        log.info("教师注册: userAccount={}, teacherId={}, title={}, department={}", userAccount, teacherId, title, department);

        // 校验参数
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, teacherId, title, department)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 调用基本的 userRegister 方法
        String userName = userAccount; // 如果 userName 为空，可以使用 userAccount 作为默认值
        long user_id = userRegister(userAccount, userPassword, checkPassword, userName);
        log.info("用户注册成功，user_id={}", user_id);

        // 获取用户信息
        User user = this.getById(user_id);
        if (user == null) {
            log.error("用户不存在，user_id={}", user_id);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 设置用户角色为教师
        user.setUserRole(UserRoleEnum.TEACHER.getValue());
        this.updateById(user); // 更新用户角色
        log.info("用户角色更新成功，user_id={}", user_id);

        // 创建教师信息
        Teacher teacher = teacherService.createTeacher(user_id, teacherId, title, department);
        if (teacher == null) {
            log.error("创建教师信息失败，user_id={}, teacherId={}", user_id, teacherId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建教师信息失败");
        }
        log.info("教师信息创建成功，user_id={}, teacherId={}", user_id, teacherId);

        return user_id;
    }

    @Override
    public long studentRegister(String userAccount, String userPassword, String checkPassword,
                                String studentId, String grade, String major) {
        // 调用基本的 userRegister 方法
        long user_id = userRegister(userAccount, userPassword, checkPassword, userAccount); // 传递 userName

        // 获取用户信息
        User user = this.getById(user_id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 设置用户角色为学生
        user.setUserRole(UserRoleEnum.STUDENT.getValue());
        this.updateById(user); // 更新用户角色

        // 创建学生信息
        Student student = studentService.createStudent(user_id, studentId, grade, major); // 调用 createStudent 方法

        // 返回用户ID
        return user_id;
    }

    @Override
    public long administratorRegister(String userAccount, String userPassword, String checkPassword,
                                      String adminId, String department) {
        // 调用基本的 userRegister 方法
        long user_id = userRegister(userAccount, userPassword, checkPassword, userAccount); // 传递 userName

        // 获取用户信息
        User user = this.getById(user_id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 设置用户角色为管理员
        user.setUserRole(UserRoleEnum.ADMINISTRATOR.getValue());
        this.updateById(user); // 更新用户角色

        // 创建管理员信息
        Administrator administrator = administratorService.createAdministrator(user_id, adminId, department); // 调用 createAdministrator 方法

        // 返回用户ID
        return user_id;
    }
    @Override
    public boolean resetPassword(String userAccount, String newPassword, String checkPassword) {
        // 校验参数
        if (StringUtils.isAnyBlank(userAccount, newPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (!newPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 加密新密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes());

        // 更新密码
        user.setUserPassword(encryptPassword);
        boolean updateResult = this.updateById(user);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "密码重置失败");
        }

        return true;
    }
    @Override
    public Teacher getTeacherByUserId(Long user_id) {
        log.info("获取教师信息: user_id={}", user_id);
        return teacherService.getTeacherByUserId(user_id);
    }

    @Override
    public Student getStudentByUserId(Long user_id) {
        // 调用 studentService 获取学生信息
        return studentService.getStudentByUserId(user_id);
    }


    @Override
    public Administrator getAdministratorByUserId(Long user_id) {
        log.info("获取管理员信息: user_id={}", user_id);
        return administratorService.getAdministratorByUserId(user_id);
    }

    @Override
    public boolean updateFeedbackAndGuidance(long questionSubmitId, String personalizedFeedback, String teachingGuidance) {
        try {
            // 创建 FeedbackAndGuidance 对象
            FeedbackAndGuidance feedbackAndGuidance = new FeedbackAndGuidance();
            feedbackAndGuidance.setQuestionSubmitId(questionSubmitId);
            feedbackAndGuidance.setPersonalizedFeedback(personalizedFeedback);
            feedbackAndGuidance.setTeachingGuidance(teachingGuidance);

            // 插入数据库
            int result = feedbackAndGuidanceMapper.insert(feedbackAndGuidance);
            return result > 0; // 返回操作结果
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反馈和指导存储失败");
        }
    }

    @Override
    public boolean savePersonalizedFeedback(long questionSubmitId, String personalizedFeedback) {
        try {
            // 创建 PersonalizedFeedback 对象
            PersonalizedFeedback feedback = new PersonalizedFeedback();
            feedback.setQuestionSubmitId(questionSubmitId);
            feedback.setPersonalizedFeedback(personalizedFeedback);

            // 插入数据库
            int result = personalizedFeedbackMapper.insert(feedback);
            return result > 0; // 返回操作结果
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "个性化学习反馈存储失败");
        }
    }

    @Override
    public boolean saveTeachingGuidance(long questionSubmitId, String teachingGuidance) {
        try {
            // 创建 TeachingGuidance 对象
            TeachingGuidance guidance = new TeachingGuidance();
            guidance.setQuestionSubmitId(questionSubmitId);
            guidance.setTeachingGuidance(teachingGuidance);

            // 插入数据库
            int result = teachingGuidanceMapper.insert(guidance);
            return result > 0; // 返回操作结果
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "教学指导存储失败");
        }
    }
}
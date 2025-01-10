package com.schall.jyyxbackenduserservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schall.jyyx.model.dto.user.UserQueryRequest;
import com.schall.jyyx.model.entity.Administrator;
import com.schall.jyyx.model.entity.Student;
import com.schall.jyyx.model.entity.Teacher;
import com.schall.jyyx.model.entity.User;
import com.schall.jyyx.model.vo.LoginUserVO;
import com.schall.jyyx.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      HTTP 请求
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 教师登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      HTTP 请求
     * @return 脱敏后的教师信息
     */
    LoginUserVO teacherLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 学生登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      HTTP 请求
     * @return 脱敏后的学生信息
     */
    LoginUserVO studentLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 管理员登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      HTTP 请求
     * @return 脱敏后的管理员信息
     */
    LoginUserVO administratorLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request HTTP 请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request HTTP 请求
     * @return 当前登录用户
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request HTTP 请求
     * @return 是否为管理员
     */
    boolean isAdministrator(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user 用户对象
     * @return 是否为管理员
     */
    boolean isAdministrator(User user);

    /**
     * 用户注销
     *
     * @param request HTTP 请求
     * @return 是否注销成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @param user 用户对象
     * @return 脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户对象
     * @return 脱敏后的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息列表
     *
     * @param userList 用户列表
     * @return 脱敏后的用户信息列表
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 用户查询请求
     * @return 查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param userName      用户昵称
     * @return 新用户 ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String userName);

    /**
     * 教师注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param teacherId     教师编号
     * @param title         教师职称
     * @param department    教师部门
     * @return 新用户 ID
     */
    long teacherRegister(String userAccount, String userPassword, String checkPassword,
                         String teacherId, String title, String department);

    /**
     * 学生注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param studentId     学生编号
     * @param grade         学生年级
     * @param major         学生专业
     * @return 新用户 ID
     */
    long studentRegister(String userAccount, String userPassword, String checkPassword,
                         String studentId, String grade, String major);

    /**
     * 管理员注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param adminId       管理员编号
     * @param department    管理部门
     * @return 新用户 ID
     */
    long administratorRegister(String userAccount, String userPassword, String checkPassword,
                               String adminId, String department);

    /**
     * 根据用户 ID 获取教师信息
     *
     * @param user_id 用户 ID
     * @return 教师信息
     */
    Teacher getTeacherByUserId(Long user_id);

    /**
     * 根据用户 ID 获取学生信息
     *
     * @param user_id 用户 ID
     * @return 学生信息
     */
    Student getStudentByUserId(Long user_id);

    /**
     * 根据用户 ID 获取管理员信息
     *
     * @param user_id 用户 ID
     * @return 管理员信息
     */
    Administrator getAdministratorByUserId(Long user_id);

    /**
     * 重置密码
     *
     * @param userAccount   用户账号
     * @param newPassword   新密码
     * @param checkPassword 确认密码
     * @return 是否重置成功
     */
    boolean resetPassword(String userAccount, String newPassword, String checkPassword);

    /**
     * 更新个性化反馈和教学指导
     *
     * @param questionSubmitId     提交的题目 ID
     * @param personalizedFeedback 个性化学习反馈
     * @param teachingGuidance     教学指导建议
     * @return 是否更新成功
     */
    boolean updateFeedbackAndGuidance(long questionSubmitId, String personalizedFeedback, String teachingGuidance);
    boolean savePersonalizedFeedback(long questionSubmitId, String personalizedFeedback);
    boolean saveTeachingGuidance(long questionSubmitId, String teachingGuidance);


}
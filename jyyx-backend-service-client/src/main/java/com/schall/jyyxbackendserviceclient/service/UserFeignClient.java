package com.schall.jyyxbackendserviceclient.service;

import com.schall.jyyx.model.entity.User;
import com.schall.jyyx.model.enums.UserRoleEnum;
import com.schall.jyyx.model.vo.UserVO;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

/**
 * 用户服务Feign客户端接口
 * 用于通过Feign调用用户服务的内部接口
 */
@FeignClient(name = "jyyx-backend-user-service", path = "/api/user/inner")
public interface UserFeignClient {

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("user_id") long userId);

    /**
     * 根据用户ID列表批量获取用户信息
     *
     * @param idList 用户ID列表
     * @return 用户信息列表
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);

    /**
     * 获取当前登录用户
     *
     * @param request HTTP请求
     * @return 当前登录用户
     * @throws BusinessException 如果用户未登录
     */
    default User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute("user_login");
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 判断用户是否为管理员
     *
     * @param user 用户信息
     * @return 是否为管理员
     */
    default boolean isAdministrator(User user) {
        return user != null && UserRoleEnum.ADMINISTRATOR.getValue().equals(user.getUserRole());
    }

    /**
     * 判断用户是否为教师
     *
     * @param user 用户信息
     * @return 是否为教师
     */
    default boolean isTeacher(User user) {
        return user != null && UserRoleEnum.TEACHER.getValue().equals(user.getUserRole());
    }

    /**
     * 将用户实体转换为用户视图对象
     *
     * @param user 用户实体
     * @return 用户视图对象
     */
    default UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 更新个性化反馈和教学指导
     *
     * @param questionSubmitId     题目提交ID
     * @param personalizedFeedback 个性化反馈
     * @param teachingGuidance     教学指导
     * @return 是否更新成功
     */
    @PostMapping("/updateFeedbackAndGuidance")
    boolean updateFeedbackAndGuidance(@RequestParam("questionSubmitId") long questionSubmitId,
                                      @RequestParam("personalizedFeedback") String personalizedFeedback,
                                      @RequestParam("teachingGuidance") String teachingGuidance);

    /**
     * 保存个性化反馈
     *
     * @param questionSubmitId     题目提交ID
     * @param personalizedFeedback 个性化反馈
     * @return 是否保存成功
     */
    @PostMapping("/savePersonalizedFeedback")
    boolean savePersonalizedFeedback(@RequestParam("questionSubmitId") long questionSubmitId,
                                     @RequestParam("personalizedFeedback") String personalizedFeedback);

    /**
     * 保存教学指导
     *
     * @param questionSubmitId 题目提交ID
     * @param teachingGuidance 教学指导
     * @return 是否保存成功
     */
    @PostMapping("/saveTeachingGuidance")
    boolean saveTeachingGuidance(@RequestParam("questionSubmitId") long questionSubmitId,
                                 @RequestParam("teachingGuidance") String teachingGuidance);
}
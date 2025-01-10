package com.schall.jyyxbackenduserservice.controller.inner;

import com.schall.jyyx.model.entity.User;
import com.schall.jyyxbackendserviceclient.service.UserFeignClient;
import com.schall.jyyxbackenduserservice.service.UserService;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * TODO 该类是内部调用，不涉及用户信息，只返回用户id
 */

@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;


    /**
     * TODO 根据 id 获取用户
     * @param user_id
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("user_id") long user_id){
        return userService.getById(user_id);

    }

    /**
     * TODO 根据 id 获取用户列表
     * @param idList
     * @return
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList){
        return userService.listByIds(idList);

    }
    @Override
    @PostMapping("/updateFeedbackAndGuidance")
    public boolean updateFeedbackAndGuidance(@RequestParam("questionSubmitId") long questionSubmitId,
                                             @RequestParam("personalizedFeedback") String personalizedFeedback,
                                             @RequestParam("teachingGuidance") String teachingGuidance) {
        try {
            boolean result = userService.updateFeedbackAndGuidance(questionSubmitId, personalizedFeedback, teachingGuidance);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反馈和指导存储失败");
            }
            return true;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反馈和指导存储异常");
        }
    }

    @Override
    @PostMapping("/savePersonalizedFeedback")
    public boolean savePersonalizedFeedback(@RequestParam("questionSubmitId") long questionSubmitId,
                                            @RequestParam("personalizedFeedback") String personalizedFeedback) {
        try {
            boolean result = userService.savePersonalizedFeedback(questionSubmitId, personalizedFeedback);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "个性化学习反馈存储失败");
            }
            return true;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "个性化学习反馈存储异常");
        }
    }

    @Override
    @PostMapping("/saveTeachingGuidance")
    public boolean saveTeachingGuidance(@RequestParam("questionSubmitId") long questionSubmitId,
                                        @RequestParam("teachingGuidance") String teachingGuidance) {
        try {
            boolean result = userService.saveTeachingGuidance(questionSubmitId, teachingGuidance);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "教学指导存储失败");
            }
            return true;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "教学指导存储异常");
        }
    }

}

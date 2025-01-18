

package com.schall.jyyxbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schall.jyyx.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.schall.jyyx.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.schall.jyyx.model.entity.Question;
import com.schall.jyyx.model.entity.QuestionSubmit;
import com.schall.jyyx.model.entity.User;
import com.schall.jyyx.model.enums.QuestionSubmitLanguageEnum;
import com.schall.jyyx.model.enums.QuestionSubmitStatusEnum;
import com.schall.jyyx.model.vo.QuestionSubmitVO;
import com.schall.jyyxbackendquestionservice.mapper.QuestionSubmitMapper;
import com.schall.jyyxbackendquestionservice.rabbitmq.MyMessageProducer;
import com.schall.jyyxbackendquestionservice.service.QuestionService;
import com.schall.jyyxbackendquestionservice.service.QuestionSubmitService;
import com.schall.jyyxbackendserviceclient.service.JudgeFeignClient;
import com.schall.jyyxbackendserviceclient.service.UserFeignClient;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import com.schall.jyyxblackendcommon.utils.SqlUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class QuestionSubmitServiceImpl
        extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    private static final Logger log = LoggerFactory.getLogger(QuestionSubmitServiceImpl.class);

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    @Resource
    private MyMessageProducer myMessageProducer;

    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        if (questionSubmitAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        if (questionId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目ID为空");
        }
        Question question = this.questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUser_id(loginUser.getId());
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        if (questionSubmitId == null) {
            log.error("questionSubmitId为空，保存失败，questionSubmit: {}", questionSubmit);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "questionSubmitId为空");
        }
        this.myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
        CompletableFuture.runAsync(() -> {
            try {
                this.judgeFeignClient.doJudge(questionSubmitId);
            } catch (Exception e) {
                log.error("评测服务调用失败，questionSubmitId: {}", questionSubmitId, e);
            }
        });
        this.questionService.updateSubmitNum(questionId, 1);
        return questionSubmitId;
    }

    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long user_id = questionSubmitQueryRequest.getUser_id();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(user_id), "user_id", user_id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        if (questionSubmit == null || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        long user_id = loginUser.getId();
        if (user_id != questionSubmit.getUser_id() && !this.userFeignClient.isAdministrator(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        if (questionSubmitPage == null || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> this.getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    @Override
    public boolean updateAnalysisData(long questionSubmitId, String analysisData) {
        return this.questionSubmitMapper.updateAnalysisData(questionSubmitId, analysisData);
    }

    @Override
    public String getAnalysisDataById(long questionSubmitId) {
        return this.questionSubmitMapper.getAnalysisDataById(questionSubmitId);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean updateFeedbackAndGuidance(long questionSubmitId, String personalizedFeedback, String teachingGuidance) {
        try {
            log.info("准备更新个性化反馈和教学指导，questionSubmitId: {}, personalizedFeedback: {}, teachingGuidance: {}", questionSubmitId, personalizedFeedback, teachingGuidance);
            boolean updateResult = this.questionSubmitMapper.updateFeedbackAndGuidance(questionSubmitId, personalizedFeedback, teachingGuidance);
            if (!updateResult) {
                log.error("更新个性化反馈和教学指导失败，questionSubmitId: {}", questionSubmitId);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新个性化反馈和教学指导失败");
            }
            log.info("更新个性化反馈和教学指导成功，questionSubmitId: {}", questionSubmitId);
            return true;
        } catch (Exception e) {
            log.error("更新个性化反馈和教学指导失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新个性化反馈和教学指导失败");
        }
    }

    @Override
    public String getPersonalizedFeedback(long questionSubmitId) {
        log.info("准备获取个性化反馈，questionSubmitId: {}", questionSubmitId);
        try {
            String personalizedFeedback = this.questionSubmitMapper.getPersonalizedFeedback(questionSubmitId);
            if (personalizedFeedback == null) {
                log.warn("未找到个性化反馈，questionSubmitId: {}", questionSubmitId);
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到个性化反馈");
            }
            log.info("成功获取个性化反馈，questionSubmitId: {}, feedback: {}", questionSubmitId, personalizedFeedback);
            return personalizedFeedback;
        } catch (Exception e) {
            log.error("获取个性化反馈失败，questionSubmitId: {}", questionSubmitId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取个性化反馈失败");
        }
    }

    @Override
    public String getTeachingGuidance(long questionSubmitId) {
        log.info("准备获取教学指导，questionSubmitId: {}", questionSubmitId);
        try {
            String teachingGuidance = this.questionSubmitMapper.getTeachingGuidance(questionSubmitId);
            if (teachingGuidance == null) {
                log.warn("未找到教学指导，questionSubmitId: {}", questionSubmitId);
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到教学指导");
            }
            log.info("成功获取教学指导，questionSubmitId: {}, guidance: {}", questionSubmitId, teachingGuidance);
            return teachingGuidance;
        } catch (Exception e) {
            log.error("获取教学指导失败，questionSubmitId: {}", questionSubmitId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取教学指导失败");
        }
    }


}
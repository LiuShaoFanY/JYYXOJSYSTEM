package com.schall.jyyxbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.schall.jyyx.model.dto.Ranking.RankingQueryRequest;
import com.schall.jyyx.model.dto.question.QuestionQueryRequest;
import com.schall.jyyx.model.entity.Question;
import com.schall.jyyx.model.entity.QuestionSubmit;
import com.schall.jyyx.model.entity.Ranking;
import com.schall.jyyx.model.entity.User;
import com.schall.jyyx.model.enums.QuestionSubmitStatusEnum;
import com.schall.jyyx.model.vo.QuestionVO;
import com.schall.jyyx.model.vo.UserVO;
import com.schall.jyyxbackendquestionservice.mapper.QuestionMapper;
import com.schall.jyyxbackendquestionservice.mapper.QuestionSubmitMapper;
import com.schall.jyyxbackendquestionservice.service.QuestionService;
import com.schall.jyyxbackendserviceclient.service.UserFeignClient;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.constant.CommonConstant;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import com.schall.jyyxblackendcommon.exception.ThrowUtils;
import com.schall.jyyxblackendcommon.utils.SqlUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    private static final Gson GSON = new Gson();
    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    public QuestionServiceImpl() {
    }

    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        } else {
            String title = question.getTitle();
            String content = question.getContent();
            String tags = question.getTags();
            String answer = question.getAnswer();
            String judgeCase = question.getJudgeCase();
            String judgeConfig = question.getJudgeConfig();
            if (add) {
                ThrowUtils.throwIf(StringUtils.isAnyBlank(new CharSequence[]{title, content, tags}), ErrorCode.PARAMS_ERROR);
            }

            if (StringUtils.isNotBlank(title) && title.length() > 80) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
            } else if (StringUtils.isNotBlank(content) && answer.length() > 8192) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
            } else if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "评测用例过长");
            } else if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "评测配置过长");
            }
        }
    }

    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUser_id();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        try {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            long questionId = question.getId();
            // 1. 关联查询用户信息
            Long userId = question.getUser_id();
            User user = null;
            if (userId != null && userId > 0) {
                try {
                    user = userFeignClient.getById(userId);
                } catch (Exception e) {
                    log.error("Failed to get user by id: " + userId, e);
                    // 可以选择继续执行，或者抛出异常
                }
            }
            UserVO userVO = null;
            try {
                userVO = userFeignClient.getUserVO(user);
            } catch (Exception e) {
                log.error("Failed to get UserVO for user: " + user, e);
                // 可以选择继续执行，或者抛出异常
            }
            questionVO.setUserVO(userVO);
            return questionVO;
        } catch (Exception e) {
            log.error("Error in getQuestionVO", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取问题详情失败");
        }
    }

    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        } else {
            Set<Long> user_idSet = (Set)questionList.stream().map(Question::getUser_id).collect(Collectors.toSet());
            Map<Long, List<User>> user_idUserListMap = (Map)this.userFeignClient.listByIds(user_idSet).stream().collect(Collectors.groupingBy(User::getId));
            List<QuestionVO> questionVOList = (List)questionList.stream().map((question) -> {
                QuestionVO questionVO = QuestionVO.objToVo(question);
                questionVO.setSubmitNum(question.getSubmitNum());
                questionVO.setAcceptedNum(question.getAcceptedNum());
                Long user_id = question.getUser_id();
                User user = null;
                if (user_idUserListMap.containsKey(user_id)) {
                    user = (User)((List)user_idUserListMap.get(user_id)).get(0);
                }

                questionVO.setUserVO(this.userFeignClient.getUserVO(user));
                return questionVO;
            }).collect(Collectors.toList());
            questionVOPage.setRecords(questionVOList);
            return questionVOPage;
        }
    }

    public boolean updateAcceptedNum(long questionId, int increment) {
        Question question = (Question)this.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        } else {
            question.setAcceptedNum(question.getAcceptedNum() + increment);
            return this.updateById(question);
        }
    }

    public boolean updateSubmitNum(long questionId, int increment) {
        Question question = (Question)this.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        } else {
            question.setSubmitNum(question.getSubmitNum() + increment);
            return this.updateById(question);
        }
    }

    public boolean updateFeedbackAndGuidance(long questionSubmitId, String personalizedFeedback, String teachingGuidance) {
        try {
            return true;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反馈和指导存储失败");
        }
    }

    @Override
    public Page<Ranking> listRankingByPage(RankingQueryRequest rankingQueryRequest) {
        long current = rankingQueryRequest.getCurrent();
        long size = rankingQueryRequest.getPageSize();
        String sortField = rankingQueryRequest.getSortField();
        String sortOrder = rankingQueryRequest.getSortOrder();

        // 查询所有用户的通过次数
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_id, COUNT(*) as acceptedNum")
                .eq("status", QuestionSubmitStatusEnum.SUCCEED.getValue())
                .groupBy("user_id")
                .orderBy(true, "desc".equals(sortOrder), sortField);

        // 分页查询
        Page<QuestionSubmit> questionSubmitPage = questionSubmitMapper.selectPage(new Page<>(current, size), queryWrapper);

        // 转换为 Ranking 对象
        List<Ranking> rankingList = questionSubmitPage.getRecords().stream().map(questionSubmit -> {
            Ranking ranking = new Ranking();
            ranking.setUserId(questionSubmit.getUser_id());

            // 获取用户信息
            User user = userFeignClient.getById(questionSubmit.getUser_id());
            if (user != null) {
                ranking.setUserName(user.getUserName());
            }

            ranking.setAcceptedNum(questionSubmit.getAcceptedNum()); // 使用 acceptedNum 字段
            return ranking;
        }).collect(Collectors.toList());

        // 构建分页结果
        Page<Ranking> rankingPage = new Page<>(current, size, questionSubmitPage.getTotal());
        rankingPage.setRecords(rankingList);
        return rankingPage;
    }
}

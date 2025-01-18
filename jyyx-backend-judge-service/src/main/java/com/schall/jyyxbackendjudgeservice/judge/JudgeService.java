package com.schall.jyyxbackendjudgeservice.judge;
import com.schall.jyyx.model.entity.QuestionSubmit;

/**
 * 评测服务
 */
public interface JudgeService {
    /**
     * 评测
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);


}

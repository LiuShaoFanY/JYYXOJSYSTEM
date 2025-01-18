package com.schall.jyyxbackendjudgeservice.judge.strategy;
import com.schall.jyyx.model.codesandbox.JudgeInfo;

/**
 * 评测策略
 */
public interface  JudgeStrategy {
    /**
     * TODO 执行评测
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}

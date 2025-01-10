package com.schall.jyyxbackendjudgeservice.judge.strategy;
import com.schall.jyyx.model.codesandbox.JudgeInfo;

/**
 * 判题策略
 */
public interface  JudgeStrategy {
    /**
     * TODO 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}

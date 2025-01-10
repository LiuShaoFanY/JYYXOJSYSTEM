package com.schall.jyyxbackendjudgeservice.judge;
import com.schall.jyyx.model.codesandbox.JudgeInfo;
import com.schall.jyyxbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.schall.jyyxbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.schall.jyyxbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.schall.jyyxbackendjudgeservice.judge.strategy.JudgeContext;

import com.schall.jyyx.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;
/**
 * TODO 判题管理（简化调用）
 */
@Service
public class JudgeManger {
    /**
     * TODO 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language))
        {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);

    }
}

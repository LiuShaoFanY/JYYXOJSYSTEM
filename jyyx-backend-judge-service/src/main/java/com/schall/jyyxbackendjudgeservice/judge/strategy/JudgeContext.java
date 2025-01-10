package com.schall.jyyxbackendjudgeservice.judge.strategy;
import com.schall.jyyx.model.codesandbox.JudgeInfo;
import com.schall.jyyx.model.dto.question.JudgeCase;
import com.schall.jyyx.model.entity.Question;
import com.schall.jyyx.model.entity.QuestionSubmit;
import lombok.Data;
import java.util.List;

/**
 * TODO 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}

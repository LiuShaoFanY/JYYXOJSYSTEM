package com.schall.jyyxbackendjudgeservice.judge.codesandbox.impl;
import com.schall.jyyxbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.schall.jyyx.model.codesandbox.ExecuteCodeRequest;
import com.schall.jyyx.model.codesandbox.ExecuteCodeResponse;
import com.schall.jyyx.model.codesandbox.JudgeInfo;
import com.schall.jyyx.model.enums.JudgeInfoMessageEnum;
import com.schall.jyyx.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * TODO 示例代码沙箱（仅为了跑通业务流程）
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {

    /**
     * TODO 执行代码
     * @param executeCodeRequset
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequset){
        List<String> inputList = executeCodeRequset.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

}

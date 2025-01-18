//package com.schall.jyyxbackendjudgeservice.judge.strategy;
//import cn.hutool.json.JSONUtil;
//import com.schall.jyyx.model.codesandbox.JudgeInfo;
//import com.schall.jyyx.model.dto.question.JudgeCase;
//import com.schall.jyyx.model.dto.question.JudgeConfig;
//import com.schall.jyyx.model.entity.Question;
//import com.schall.jyyx.model.enums.JudgeInfoMessageEnum;
//
//import java.util.List;
///**
// * TODO 默认评测策略
// */
//public class DefaultJudgeStrategy implements com.schall.jyyxbackendjudgeservice.judge.strategy.JudgeStrategy{
//    /**
//     * TODO 执行评测
//     * @param judgeContext
//     * @return
//     */
//    @Override
//    public JudgeInfo doJudge(com.schall.jyyxbackendjudgeservice.judge.strategy.JudgeContext judgeContext) {
//
//        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
//
//        Long memory = judgeInfo.getMemory();
//        Long time = judgeInfo.getTime();
//
//        List<String> inputList = judgeContext.getInputList();
//        List<String> outputList = judgeContext.getOutputList();
//        Question question = judgeContext.getQuestion();
//        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
//        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
//        JudgeInfo judgeInfoResponse = new JudgeInfo();
//        judgeInfo.setMemory(memory);
//        judgeInfo.setTime(time);
//
//
//        //todo 先判断沙箱执行的结果输出数量是否和预期输出数量相等
//        if (outputList.size() != inputList.size()) {
//            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
//            judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
//            return judgeInfoResponse;
//
//        }
//        //todo 依次判断每一项输出和预期输出是否相等
//        for (int i = 0; i < judgeCaseList.size(); i++) {
//            JudgeCase judgeCase = judgeCaseList.get(i);
//            if (!judgeCase.getOutput().equals(outputList.get(i))) {
//                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
//                judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
//                return judgeInfoResponse;
//            }
//        }
//        //todo 判断题目限制
//
//        String judgeConfigStr = question.getJudgeConfig();
//        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
//        Long needMemoryLimit = judgeConfig.getMemoryLimit();
//        Long needTimeLimit = judgeConfig.getTimeLimit();
//        if (memory > needMemoryLimit) {
//            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
//            judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
//            return judgeInfoResponse;
//        }
//        if (time > needTimeLimit) {
//            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
//            judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
//            return judgeInfoResponse;
//        }
//        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
//        return judgeInfoResponse;
//    }
//}

package com.schall.jyyxbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.schall.jyyx.model.codesandbox.JudgeInfo;
import com.schall.jyyx.model.dto.question.JudgeCase;
import com.schall.jyyx.model.dto.question.JudgeConfig;
import com.schall.jyyx.model.entity.Question;
import com.schall.jyyx.model.enums.JudgeInfoMessageEnum;

import java.util.List;

public class DefaultJudgeStrategy implements JudgeStrategy {

    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();

        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);

        // 判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        // 判断每一项输出和预期输出是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }

        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);

        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (memory > needMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        if (time > needTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
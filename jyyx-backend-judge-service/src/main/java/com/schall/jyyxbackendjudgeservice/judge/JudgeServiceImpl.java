package com.schall.jyyxbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.schall.jyyxbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.schall.jyyxbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.schall.jyyxbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.schall.jyyxbackendjudgeservice.judge.strategy.JudgeContext;
import com.schall.jyyx.model.codesandbox.ExecuteCodeRequest;
import com.schall.jyyx.model.codesandbox.ExecuteCodeResponse;
import com.schall.jyyx.model.codesandbox.JudgeInfo;
import com.schall.jyyx.model.dto.question.JudgeCase;
import com.schall.jyyx.model.entity.Question;
import com.schall.jyyx.model.entity.QuestionSubmit;
import com.schall.jyyx.model.enums.QuestionSubmitStatusEnum;
import com.schall.jyyxbackendserviceclient.service.DeepSeekChatClient;
import com.schall.jyyxbackendserviceclient.service.QuestionFeignClient;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManger judgeManger;

    @Value("${codesandbox.type:remote}")
    private String type;
    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        log.info("开始评测，questionSubmitId: {}", questionSubmitId);

        // 1. 获取提交信息和题目
        if (questionFeignClient == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "questionFeignClient 未初始化");
        }
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        if (questionId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目ID为空");
        }
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 2. 检查提交状态
        if (questionSubmit.getStatus() == null || !questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在评测中");
        }

        // 3. 更新提交状态为“评测中”
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        // 4. 调用沙箱执行代码
        if (type == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "沙箱类型未配置");
        }
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        if (codeSandbox == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "沙箱初始化失败");
        }
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        log.info("codeSandbox 类名: {}", codeSandbox.getClass().getName());

        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();

        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        if (judgeCaseStr == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评测用例为空");
        }
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        if (judgeCaseList == null || judgeCaseList.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评测用例列表为空");
        }
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        log.info("executeCodeRequest: {}", executeCodeRequest);

        // 参数检查
        if (language == null || code == null || inputList == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER_ERROR, "参数无效");
        }

        try {
            log.info("开始执行代码");
            ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
            log.info("代码执行完毕，executeCodeResponse: {}", executeCodeResponse);

            List<String> outputList = executeCodeResponse.getOutputList();
            if (outputList == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码执行结果为空");
            }

            // 5. 设置评测状态和信息
            JudgeContext judgeContext = new JudgeContext();
            judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
            judgeContext.setInputList(inputList);
            judgeContext.setOutputList(outputList);
            judgeContext.setJudgeCaseList(judgeCaseList);
            judgeContext.setQuestion(question);
            judgeContext.setQuestionSubmit(questionSubmit);

            JudgeInfo judgeInfo = judgeManger.doJudge(judgeContext);

            // 6. 修改数据库中的评测结果
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }



            // 7. 调用智能分析服务
            DeepSeekChatClient deepSeekChatClient = new DeepSeekChatClient();
            String analysisResult = deepSeekChatClient.sendCodeAnalysisRequest(code);
            log.info("代码分析结果: {}", analysisResult);

            // 8. 反馈分析结果到控制台
            System.out.println("代码分析结果: " + analysisResult);

            // 9. 将分析结果存储到数据库
            update = questionFeignClient.updateAnalysisData(questionSubmitId, analysisResult);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "分析结果存储错误");
            }

            // 10. 提供个性化的学习反馈
            String personalizedFeedback = deepSeekChatClient.providePersonalizedFeedback(code);
            log.info("个性化学习反馈: {}", personalizedFeedback);
            System.out.println("个性化学习反馈: " + personalizedFeedback);

            // 11. 提供教学指导建议
            String teachingGuidance = deepSeekChatClient.provideTeachingGuidance(code);
            log.info("教学指导建议: {}", teachingGuidance);
            System.out.println("教学指导建议: " + teachingGuidance);

            // 12. 检查参数合法性
            if (personalizedFeedback == null || personalizedFeedback.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "个性化反馈不能为空");
            }
            if (teachingGuidance == null || teachingGuidance.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "教学指导不能为空");
            }

            // 13. 将个性化反馈和教学指导存储到数据库
            log.info("准备存储个性化反馈和教学指导，questionSubmitId: {}, personalizedFeedback: {}, teachingGuidance: {}",
                    questionSubmitId, personalizedFeedback, teachingGuidance);
            update = questionFeignClient.updateFeedbackAndGuidance(questionSubmitId, personalizedFeedback, teachingGuidance);
            if (update) {
                log.info("个性化反馈和教学指导存储成功，questionSubmitId: {}", questionSubmitId);
            } else {
                log.error("个性化反馈和教学指导存储失败，questionSubmitId: {}", questionSubmitId);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反馈和指导存储错误");
            }

            // 14. 返回评测结果
            QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionSubmitId);
            return questionSubmitResult;
        } catch (BusinessException e) {
            log.error("业务异常: ", e);
            throw e;
        } catch (Exception e) {
            log.error("执行代码时发生错误", e);
            // 更新提交状态为“失败”
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行代码时发生错误");
        }
    }

//    @Override
//    public QuestionSubmit doJudge(long questionSubmitId) {
//        log.info("开始评测，questionSubmitId: {}", questionSubmitId);
//
//        // 1. 获取提交信息和题目
//        if (questionFeignClient == null) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "questionFeignClient 未初始化");
//        }
//        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
//        if (questionSubmit == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
//        }
//        Long questionId = questionSubmit.getQuestionId();
//        if (questionId == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目ID为空");
//        }
//        Question question = questionFeignClient.getQuestionById(questionId);
//        if (question == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
//        }
//
//        // 2. 检查提交状态
//        if (questionSubmit.getStatus() == null || !questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
//            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在评测中");
//        }
//
//        // 3. 更新提交状态为“评测中”
//        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
//        questionSubmitUpdate.setId(questionSubmitId);
//        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
//        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
//        if (!update) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
//        }
//
//        // 4. 调用沙箱执行代码
//        if (type == null) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "沙箱类型未配置");
//        }
//        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
//        if (codeSandbox == null) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "沙箱初始化失败");
//        }
//        codeSandbox = new CodeSandboxProxy(codeSandbox);
//        log.info("codeSandbox 类名: {}", codeSandbox.getClass().getName());
//
//        String language = questionSubmit.getLanguage();
//        String code = questionSubmit.getCode();
//
//        // 获取输入用例
//        String judgeCaseStr = question.getJudgeCase();
//        if (judgeCaseStr == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评测用例为空");
//        }
//        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
//        if (judgeCaseList == null || judgeCaseList.isEmpty()) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评测用例列表为空");
//        }
//        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
//
//        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
//                .code(code)
//                .language(language)
//                .inputList(inputList)
//                .build();
//        log.info("executeCodeRequest: {}", executeCodeRequest);
//
//        // 参数检查
//        if (language == null || code == null || inputList == null) {
//            throw new BusinessException(ErrorCode.INVALID_PARAMETER_ERROR, "参数无效");
//        }
//
//        try {
//            log.info("开始执行代码");
//            ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
//            log.info("代码执行完毕，executeCodeResponse: {}", executeCodeResponse);
//
//            List<String> outputList = executeCodeResponse.getOutputList();
//            if (outputList == null) {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码执行结果为空");
//            }
//
//            // 5. 设置评测状态和信息
//            JudgeContext judgeContext = new JudgeContext();
//            judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
//            judgeContext.setInputList(inputList);
//            judgeContext.setOutputList(outputList);
//            judgeContext.setJudgeCaseList(judgeCaseList);
//            judgeContext.setQuestion(question);
//            judgeContext.setQuestionSubmit(questionSubmit);
//
//            JudgeInfo judgeInfo = judgeManger.doJudge(judgeContext);
//
//            // 6. 修改数据库中的评测结果
//            questionSubmitUpdate = new QuestionSubmit();
//            questionSubmitUpdate.setId(questionSubmitId);
//            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
//            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
//            update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
//            if (!update) {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
//            }
//
//            // 7. 调用智能分析服务
//            DeepSeekChatClient deepSeekChatClient = new DeepSeekChatClient();
//            String analysisResult = deepSeekChatClient.sendCodeAnalysisRequest(code);
//            log.info("代码分析结果: {}", analysisResult);
//
//            // 8. 反馈分析结果到控制台
//            System.out.println("代码分析结果: " + analysisResult);
//
//            // 9. 将分析结果存储到数据库
//            update = questionFeignClient.updateAnalysisData(questionSubmitId, analysisResult);
//            if (!update) {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "分析结果存储错误");
//            }
//
//            // 10. 提供个性化的学习反馈
//            String personalizedFeedback = deepSeekChatClient.providePersonalizedFeedback(code);
//            log.info("个性化学习反馈: {}", personalizedFeedback);
//            System.out.println("个性化学习反馈: " + personalizedFeedback);
//
//            // 11. 提供教学指导建议
//            String teachingGuidance = deepSeekChatClient.provideTeachingGuidance(code);
//            log.info("教学指导建议: {}", teachingGuidance);
//            System.out.println("教学指导建议: " + teachingGuidance);
//
//            // 12. 检查参数合法性
//            if (personalizedFeedback == null || personalizedFeedback.isEmpty()) {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR, "个性化反馈不能为空");
//            }
//            if (teachingGuidance == null || teachingGuidance.isEmpty()) {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR, "教学指导不能为空");
//            }
//
//            // 13. 将个性化反馈和教学指导存储到数据库
//            log.info("准备存储个性化反馈和教学指导，questionSubmitId: {}, personalizedFeedback: {}, teachingGuidance: {}",
//                    questionSubmitId, personalizedFeedback, teachingGuidance);
//            update = questionFeignClient.updateFeedbackAndGuidance(questionSubmitId, personalizedFeedback, teachingGuidance);
//            if (update) {
//                log.info("个性化反馈和教学指导存储成功，questionSubmitId: {}", questionSubmitId);
//            } else {
//                log.error("个性化反馈和教学指导存储失败，questionSubmitId: {}", questionSubmitId);
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反馈和指导存储错误");
//            }
//
//            // 14. 返回评测结果
//            QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionSubmitId);
//            return questionSubmitResult;
//        } catch (BusinessException e) {
//            log.error("业务异常: ", e);
//            throw e;
//        } catch (Exception e) {
//            log.error("执行代码时发生错误", e);
//            // 更新提交状态为“失败”
//            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
//            questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行代码时发生错误");
//        }
//    }
}
package com.schall.jyyxbackendjudgeservice.controller.inner;
import com.schall.jyyx.model.entity.QuestionSubmit;
import com.schall.jyyxbackendjudgeservice.judge.JudgeService;
import com.schall.jyyxbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
/**
 * TODO 该类是内部调用，不涉及用户信息，只返回用户id
 */
/**
 * TODO 控制器类，处理内部判断相关的HTTP请求
 * TODO 实现了JudgeFeignClient接口，用于服务间调用
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {



    /**
     * TODO 注入JudgeService服务，用于处理判断逻辑
     */
    @Resource
    private JudgeService judgeService;

    /**
     * TODO 处理GET请求的doJudge方法，根据问题提交ID进行判断
     *
     * @param questionSubmitId TODO 问题提交的ID，用于标识特定的问题提交记录
     * @return TODO 返回判断后的QuestionSubmit对象，包含判断结果
     */
    @Override
    @GetMapping("/do")
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }
    /**
     * TODO 处理GET请求的getAnalysisResult方法，根据问题提交ID获取分析结果
     *
     * @param questionSubmitId TODO 问题提交的ID，用于标识特定的问题提交记录
     * @return TODO 返回分析结果字符串
     */
//    @GetMapping("/analysis")
//    public String getAnalysisResult(@RequestParam("questionSubmitId") long questionSubmitId) {
//        return judgeService.getAnalysisResult(questionSubmitId);
//    }

}


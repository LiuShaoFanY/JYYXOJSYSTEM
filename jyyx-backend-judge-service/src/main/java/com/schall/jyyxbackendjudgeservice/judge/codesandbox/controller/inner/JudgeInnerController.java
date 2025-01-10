package com.schall.jyyxbackendjudgeservice.judge.codesandbox.controller.inner;

import com.schall.jyyxbackendjudgeservice.judge.JudgeService;
import com.schall.jyyx.model.entity.QuestionSubmit;
import com.schall.jyyxbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
/**
 * TODO 该类是内部调用，不涉及用户信息，只返回用户id
 */
@RestController("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;
    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    @PostMapping("/do")
    @Override
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId){
        return judgeService.doJudge(questionSubmitId);
    }
}

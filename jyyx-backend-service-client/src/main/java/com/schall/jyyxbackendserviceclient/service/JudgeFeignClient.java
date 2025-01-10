package com.schall.jyyxbackendserviceclient.service;

import com.schall.jyyx.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * 判题服务
 */
@FeignClient(name = "jyyx-backend-judge-service" , path = "/api/judge/inner")
public interface JudgeFeignClient {
    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
//@PostMapping("/do")
//QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);

    @GetMapping("/do")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);
}

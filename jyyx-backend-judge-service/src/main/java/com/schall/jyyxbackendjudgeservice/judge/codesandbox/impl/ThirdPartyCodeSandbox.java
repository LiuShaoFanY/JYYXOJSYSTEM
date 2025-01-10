package com.schall.jyyxbackendjudgeservice.judge.codesandbox.impl;

import com.schall.jyyxbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.schall.jyyx.model.codesandbox.ExecuteCodeRequest;
import com.schall.jyyx.model.codesandbox.ExecuteCodeResponse;



/**
 * TODO 第三方代码沙箱(调用网上现成的代码沙箱)
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {

    /**
     * TODO 执行代码
     * @param executeCodeRequset
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequset){
        System.out.println("第三方代码沙箱");
        return null;
    }

}

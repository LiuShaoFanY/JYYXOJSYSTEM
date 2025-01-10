    package com.schall.jyyxbackendjudgeservice.judge.codesandbox;

    import com.schall.jyyx.model.codesandbox.ExecuteCodeRequest;
    import com.schall.jyyx.model.codesandbox.ExecuteCodeResponse;

    /**
     * TODO 代码沙箱接口定义
     */
    public interface CodeSandbox {

        /**
         * TODO 执行代码
         * @param executeCodeRequset
         * @return
         */
        ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequset);

    }

package com.schall.jyyxbackendjudgeservice.judge.codesandbox.impl;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.schall.jyyxbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.schall.jyyx.model.codesandbox.ExecuteCodeRequest;
import com.schall.jyyx.model.codesandbox.ExecuteCodeResponse;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO 远程代码沙箱(实际调用接口的沙箱)
 */
public class RemoteCodeSandbox implements CodeSandbox {
    //TODO 完成鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    /**
     * TODO 执行代码
     * @param executeCodeRequset
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequset){
        System.out.println("远程代码沙箱");
        String url = "http://localhost:8090/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequset);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER,AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"executeCode remoteSandbox error,message = "+ responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }

}

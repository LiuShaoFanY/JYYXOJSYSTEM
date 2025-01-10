package com.schall.jyyxblackendcommon.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应类，用于封装API响应数据
 * 该类实现了Serializable接口，以支持响应对象的序列化和反序列化
 *
 * @param <T> 响应数据的类型，允许响应携带任意类型的业务数据
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}

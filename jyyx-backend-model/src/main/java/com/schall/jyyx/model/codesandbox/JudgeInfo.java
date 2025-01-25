package com.schall.jyyx.model.codesandbox;

import lombok.Data;

/**
 * 评测信息
 */
@Data
public class JudgeInfo {


    
    /**
     * 程序执行信息(ms)
     */
    private String message;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 消耗时间(kb)
     */
    private Long time;

}

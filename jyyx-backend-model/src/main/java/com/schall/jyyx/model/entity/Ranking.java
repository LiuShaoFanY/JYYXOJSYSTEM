package com.schall.jyyx.model.entity;

import lombok.Data;

@Data
public class Ranking {
    private Long userId;       // 用户ID
    private String userName;   // 用户名
    private Integer acceptedNum; // 通过评测的次数
}
package com.schall.jyyx.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id; // 公告ID
    private String title; // 公告标题
    private String content; // 公告内容
    private Long creatorId; // 创建者ID
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
}
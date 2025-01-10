package com.schall.jyyx.model.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 */

@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L; // 添加 serialVersionUID

    private Long id; // 用户ID
    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String userName;
    private String userRole; // 用户角色：user/teacher/student
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    private Integer isDelete; // 是否删除
}
//@TableName(value = "user")
//@Data
//public class User implements Serializable {
//
//    /**
//     * id
//     */
//    @TableId(type = IdType.ASSIGN_ID)
//    private Long id;
//
//
//    /**
//     * 用户账号
//     */
//    private String userAccount;
//
//    /**
//     * 用户密码
//     */
//    private String userPassword;
//
//    /**
//     * 开放平台id
//     */
////    private String unionId;
//
//
//    /**
//     * 用户昵称
//     */
//    private String userName;
//
//    /**
//     * 用户头像
//     */
//    private String userAvatar;
//
//    /**
//     * 用户简介
//     */
//    private String userProfile;
//
//    /**
//     * 用户角色：user/admin/ban
//     */
//    private String userRole;
//
//    /**
//     * 创建时间
//     */
//    private Date createTime;
//
//    /**
//     * 更新时间
//     */
//    private Date updateTime;
//
//    /**
//     * 是否删除
//     */
//    private Long user_id; // 用户唯一标识
//    /**
//     *
//     */
//    private String roleType; // 角色类型
//    @TableLogic
//    private Integer isDelete;
//
//    @TableField(exist = false)
//    private static final long serialVersionUID = 1L;
//
//
//}
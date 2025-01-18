//package com.schall.jyyx.model.vo;
//
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.Date;
//
///**
// * 已登录用户视图（脱敏）
// **/
//@Data
//public class LoginUserVO implements Serializable {
//
//    /**
//     * 用户 id
//     */
//    private Long id;
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
//    private String title; // 教师职称
//    private String grade; // 学生年级
//    private String major; // 学生专业
//
//    private static final long serialVersionUID = 1L;
//}
package com.schall.jyyx.model.vo;

import lombok.Data;
import lombok.Setter;

@Data
public class LoginUserVO {
    private Long id;
    private Long user_id;
    private String userAccount;
    private String userName;
    private String userRole;
    private String title; // 教师职称

    // 添加 setDepartment 方法
    @Setter
    private String department; // 教师所属部门
    private String grade; // 学生年级
    private String major; // 学生专业

}
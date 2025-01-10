package com.schall.jyyxbackenduserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schall.jyyx.model.entity.Administrator;

public interface AdministratorService extends IService<Administrator> {
    /**
     * 创建管理员信息
     *
     * @param user_id 用户ID，标识唯一用户
     * @param adminId 管理员编号，唯一标识管理员
     * @param department 管理部门
     * @return 创建成功的管理员信息实体
     */
    Administrator createAdministrator(Long user_id, String adminId, String department);

    /**
     * 根据用户ID获取管理员信息
     *
     * @param user_id 用户ID，用于查询对应的管理员信息
     * @return 查询到的管理员信息实体，如果未找到则返回null
     */
    Administrator getAdministratorByUserId(Long user_id);

    Administrator getByUserId(Long userId);
}
package com.schall.jyyxbackenduserservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schall.jyyx.model.entity.Administrator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Administrator 的 Mapper 接口
 */
@Mapper
public interface AdministratorMapper extends BaseMapper<Administrator> {
    // 可以在这里添加自定义的 SQL 方法
    /**
     * 根据用户 ID 查询管理员信息
     *
     * @param userId 用户 ID
     * @return 管理员信息
     */
    @Select("SELECT * FROM administrator WHERE user_id = #{userId}")
    Administrator selectByUserId(@Param("userId") Long userId);
}
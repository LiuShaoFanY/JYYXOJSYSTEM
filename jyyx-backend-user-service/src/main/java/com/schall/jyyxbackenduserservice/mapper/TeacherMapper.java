package com.schall.jyyxbackenduserservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schall.jyyx.model.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 教师数据库操作
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    /**
     * 根据用户 ID 查询教师信息
     *
     * @param userId 用户 ID
     * @return 教师信息
     */
    @Select("SELECT * FROM teacher WHERE user_id = #{userId}")
    Teacher selectByUserId(@Param("userId") Long userId);

    /**
     * 根据 ID 更新教师信息
     *
     * @param id         教师 ID
     * @param teacherId  教师编号
     * @param title      职称
     * @param department 部门
     * @return 更新结果
     */
    @Update("UPDATE teacher SET teacherId = #{teacherId}, title = #{title}, department = #{department} WHERE id = #{id}")
    int updateTeacherById(@Param("id") Long id, @Param("teacherId") String teacherId, @Param("title") String title, @Param("department") String department);
}
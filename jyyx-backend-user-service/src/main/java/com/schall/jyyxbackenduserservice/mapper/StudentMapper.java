package com.schall.jyyxbackenduserservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schall.jyyx.model.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 学生数据库操作
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    /**
     * 根据用户 ID 查询学生信息
     *
     * @param userId 用户 ID
     * @return 学生信息
     */
    @Select("SELECT * FROM student WHERE user_id = #{userId}")
    Student selectByUserId(@Param("userId") Long userId);


    /**
     * 根据 ID 更新学生信息
     *
     * @param id        学生 ID
     * @param studentId 学号
     * @param grade     年级
     * @param major     专业
     * @return 更新结果
     */
    @Update("UPDATE student SET studentId = #{studentId}, grade = #{grade}, major = #{major} WHERE id = #{id}")
    int updateStudentById(@Param("id") Long id, @Param("studentId") String studentId, @Param("grade") String grade, @Param("major") String major);
}

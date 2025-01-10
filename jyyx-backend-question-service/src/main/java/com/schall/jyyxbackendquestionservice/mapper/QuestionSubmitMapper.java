package com.schall.jyyxbackendquestionservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schall.jyyx.model.entity.QuestionSubmit;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
* @author Alter
* @description 针对表【question_submit(题目提交)】的数据库操作Mapper
* @createDate 2024-10-15 13:44:41
* @Entity generator.domain.QuestionSubmit
*/
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {
    /**
     * 更新题目提交的智能分析结果
     *
     * @param questionSubmitId 题目提交ID
     * @param analysisData     智能分析结果
     * @return 是否更新成功
     */
    @Update("UPDATE question_submit SET analysisData = #{analysisData} WHERE id = #{questionSubmitId}")
    boolean updateAnalysisData(@Param("questionSubmitId") long questionSubmitId, @Param("analysisData") String analysisData);

    /**
     * 根据题目提交ID获取智能分析结果
     *
     * @param questionSubmitId 题目提交ID
     * @return 智能分析结果
     */
    @Select("SELECT analysisData FROM question_submit WHERE id = #{questionSubmitId}")
    String getAnalysisDataById(@Param("questionSubmitId") long questionSubmitId);

    /**
     * 更新个性化反馈和教学指导
     *
     * @param questionSubmitId      题目提交ID
     * @param personalizedFeedback  个性化反馈
     * @param teachingGuidance      教学指导
     * @return 是否更新成功
     */
    @Update("UPDATE question_submit SET personalizedFeedback = #{personalizedFeedback}, teachingGuidance = #{teachingGuidance} WHERE id = #{questionSubmitId}")
    boolean updateFeedbackAndGuidance(@Param("questionSubmitId") long questionSubmitId,
                                      @Param("personalizedFeedback") String personalizedFeedback,
                                      @Param("teachingGuidance") String teachingGuidance);

    @Select("SELECT personalizedFeedback FROM question_submit WHERE id = #{questionSubmitId}")
    String getPersonalizedFeedback(@Param("questionSubmitId") long questionSubmitId);

    @Select("SELECT teachingGuidance FROM question_submit WHERE id = #{questionSubmitId}")
    String getTeachingGuidance(@Param("questionSubmitId") long questionSubmitId);

}





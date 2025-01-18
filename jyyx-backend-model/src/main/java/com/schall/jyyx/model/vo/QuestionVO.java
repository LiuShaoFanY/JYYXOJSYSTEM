package com.schall.jyyx.model.vo;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.schall.jyyx.model.dto.question.JudgeConfig;
import com.schall.jyyx.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类
 *  todo 定义OV类：作用是专门前端返回对象，可以节约网络传输大小，或者过滤字段(脱敏)，保证安全性
 * @TableName question
 */
@TableName(value ="question")
@Data
public class QuestionVO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID) //题目id，每创建一次自动自己序号，但是容易被爬虫爬， todo 需要修改为ASSIGN_ID
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

// todo 如果题目答案类删除可以避免数据泄露
    /**
     * 题目答案
     */
    // private String answer;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    // todo 不允许返回给前端显示答案，防止if else
    /**
     * 评测用例(json 数组)
     */
    //private String judgeCase;

    /**
     * 评测配置(json 对象)
     */
    private JudgeConfig judgeConfig;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long user_id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 创建题目人的信息
     */
    private UserVO userVO;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();

        if (tagList != null) {

            question.setTags(JSONUtil.toJsonStr(tagList));

        }
        JudgeConfig vojudgeConfig= questionVO.getJudgeConfig();
        if (vojudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(vojudgeConfig));
        }
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setSubmitNum(question.getSubmitNum());
        questionVO.setAcceptedNum(question.getAcceptedNum());
        List<String> tagList = JSONUtil.toList(question.getTags(),String.class);
        questionVO.setTags(tagList);
        String judgeConfigStr = question.getJudgeConfig();
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr, JudgeConfig.class));
        return questionVO;
    }
    
    private static final long serialVersionUID = 1L;
}
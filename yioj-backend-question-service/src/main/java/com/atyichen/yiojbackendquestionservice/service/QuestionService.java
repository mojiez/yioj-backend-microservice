package com.atyichen.yiojbackendquestionservice.service;

import com.atyichen.yiojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.atyichen.yiojbackendmodel.model.entity.Question;
import com.atyichen.yiojbackendmodel.model.vo.QuestionVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface QuestionService extends IService<Question> {
    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);
}

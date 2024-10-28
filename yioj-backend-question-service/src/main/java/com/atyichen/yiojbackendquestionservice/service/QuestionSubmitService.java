package com.atyichen.yiojbackendquestionservice.service;

import com.atyichen.yiojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.atyichen.yiojbackendmodel.model.entity.QuestionSubmit;
import com.atyichen.yiojbackendmodel.model.entity.User;
import com.atyichen.yiojbackendmodel.model.vo.QuestionSubmitVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    QuestionSubmitVO doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    @Transactional(rollbackFor = Exception.class)
    QuestionSubmitVO doQuestionSubmitInner(long userId, QuestionSubmitAddRequest questionSubmitAddRequest);
}

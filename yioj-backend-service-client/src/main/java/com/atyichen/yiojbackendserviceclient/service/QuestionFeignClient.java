package com.atyichen.yiojbackendserviceclient.service;

import com.atyichen.yiojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.atyichen.yiojbackendmodel.model.entity.Question;
import com.atyichen.yiojbackendmodel.model.entity.QuestionSubmit;
import com.atyichen.yiojbackendmodel.model.vo.QuestionVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
@FeignClient(name = "yioj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {
    @GetMapping("/get/question/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);
    @GetMapping("/get/question_submit/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);
    @PostMapping("/update/question_submit")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);
}

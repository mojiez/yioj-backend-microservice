package com.atyichen.yiojbackendquestionservice.controller;


import com.atyichen.yiojbackendmodel.model.entity.Question;
import com.atyichen.yiojbackendmodel.model.entity.QuestionSubmit;

import com.atyichen.yiojbackendquestionservice.service.QuestionService;
import com.atyichen.yiojbackendquestionservice.service.QuestionSubmitService;
import com.atyichen.yiojbackendserviceclient.service.QuestionFeignClient;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {
//    @GetMapping("/get/question/id")
//    Question getQuestionById(@RequestParam("questionId") long questionId);
//    @GetMapping("/get/question_submit/id")
//    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);
//    @PostMapping("/update/question_submit")
//    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/get/question/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    @Override
    @GetMapping("/get/question_submit/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @Override
    @PostMapping("/update/question_submit")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }
}

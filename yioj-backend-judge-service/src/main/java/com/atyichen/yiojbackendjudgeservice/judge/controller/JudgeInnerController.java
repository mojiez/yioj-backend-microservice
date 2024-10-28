package com.atyichen.yiojbackendjudgeservice.judge.controller;

import com.atyichen.yiojbackendjudgeservice.judge.JudgeService;
import com.atyichen.yiojbackendmodel.model.vo.QuestionSubmitVO;
import com.atyichen.yiojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {
    @Resource
    private JudgeService judgeService;
    @Override
    @GetMapping("/dojudge")
    public QuestionSubmitVO doJudge(@RequestParam("questionSubmitId") long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }
}

package com.atyichen.yiojbackendserviceclient.service;


import com.atyichen.yiojbackendmodel.model.vo.QuestionSubmitVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 判题服务业务流程：
 * 传入题目的提交id，获取对应的提交信息（代码、编程语言等）
 * 调用沙箱， 获取到执行结果
 * 根据沙箱的执行结果， 设置题目的判题状态和信息
 */
@FeignClient(name = "yioj-backend-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {
    @GetMapping("/dojudge")
    QuestionSubmitVO doJudge(@RequestParam("questionSubmitId") long questionSubmitId);
}

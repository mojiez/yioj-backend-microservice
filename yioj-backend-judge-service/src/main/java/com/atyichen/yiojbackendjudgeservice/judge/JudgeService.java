package com.atyichen.yiojbackendjudgeservice.judge;


import com.atyichen.yiojbackendmodel.model.vo.QuestionSubmitVO;

/**
 * 判题服务业务流程：
 * 传入题目的提交id，获取对应的提交信息（代码、编程语言等）
 * 调用沙箱， 获取到执行结果
 * 根据沙箱的执行结果， 设置题目的判题状态和信息
 */
public interface JudgeService {
    QuestionSubmitVO doJudge(long questionSubmitId);
}

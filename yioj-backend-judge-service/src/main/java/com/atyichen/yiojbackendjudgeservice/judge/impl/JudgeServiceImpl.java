package com.atyichen.yiojbackendjudgeservice.judge.impl;

import cn.hutool.json.JSONUtil;
import com.atyichen.yiojbackendcommon.common.ErrorCode;
import com.atyichen.yiojbackendcommon.exception.BusinessException;
import com.atyichen.yiojbackendjudgeservice.judge.JudgeService;
import com.atyichen.yiojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.atyichen.yiojbackendjudgeservice.judge.codesandbox.factory.CodeBoxFactory;
import com.atyichen.yiojbackendjudgeservice.judge.codesandbox.proxy.CodeSandBoxProxy;
import com.atyichen.yiojbackendjudgeservice.judge.strategy.JudgeContext;
import com.atyichen.yiojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.atyichen.yiojbackendjudgeservice.judge.strategy.JudgeStrategyFactory;
import com.atyichen.yiojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.atyichen.yiojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.atyichen.yiojbackendmodel.model.codesandbox.JudgeInfo;
import com.atyichen.yiojbackendmodel.model.entity.Question;
import com.atyichen.yiojbackendmodel.model.entity.QuestionSubmit;
import com.atyichen.yiojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.atyichen.yiojbackendmodel.model.vo.JudgeCase;
import com.atyichen.yiojbackendmodel.model.vo.JudgeConfig;
import com.atyichen.yiojbackendmodel.model.vo.QuestionSubmitVO;
import com.atyichen.yiojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionFeignClient questionFeignClient;

    // 使用set方法注入 先曝光后构造 避免循环依赖
//    @Autowired
//    public void setQuestionSubmitService(QuestionSubmitService questionSubmitService) {
//        this.questionSubmitService = questionSubmitService;
//    }

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmitVO doJudge(long questionSubmitId) {
        // 1. 传入题目的提交id 获取到对应的题目、提交信息（代码、编程语言等）
        // 2. 如果题目的提交状态不为等待中， 就不用重复执行了
        // 3. 更改题目的提交状态为判题中（这样如果用户重复提交（代码一样 语言一样。。），那么就不重复执行, 因为第二步直接return了）
        // todo 3. 代码沙箱是个耗时操作,(消息队列？？？)
        // 4. 调用沙箱， 获取到执行结果
        // 5. 根据沙箱的执行结果， 设置题目的判题状态和信息

        // 1
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);


        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目提交信息不存在");
        }
        long id = questionSubmit.getId();
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeInfo = questionSubmit.getJudgeInfo();
        Integer status = questionSubmit.getStatus();
        long questionId = questionSubmit.getQuestionId();
        long userId = questionSubmit.getUserId();

        // 初始化沙箱
        CodeSandBox codeSandBox = CodeBoxFactory.newInstance(type);
        CodeSandBoxProxy codeSandBoxProxy = new CodeSandBoxProxy(codeSandBox);
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
//        executeCodeRequest.setInputList(Lists.newArrayList());
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage(language);
        // inputList怎么搞？    =>  有judgeCase
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2. 如果不为等待状态 就不重复执行  防止多次提交
        if (!questionSubmit.getStatus().equals(Integer.parseInt(QuestionSubmitStatusEnum.WATTING.getValue()))) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已经在判题");
        }
        // 3. 更改题目的状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(Integer.parseInt(QuestionSubmitStatusEnum.RUNNING.getValue()));
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目状态为Running失败");
        }

        // 4. 调用 代理代码沙箱
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        JudgeConfig judgeConfigBean = JSONUtil.toBean(judgeConfig, JudgeConfig.class);

        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        List<String> outputList = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        executeCodeRequest.setInputList(inputList);

        ExecuteCodeResponse executeCodeResponse = codeSandBoxProxy.executeCode(executeCodeRequest);

        List<String> boxOutputList = executeCodeResponse.getOutputList();
        String boxMessage = executeCodeResponse.getMessage();
        int boxStatus = executeCodeResponse.getStatus();
        JudgeInfo boxJudgeInfo = executeCodeResponse.getJudgeInfo();

        // 4.5 更新题目的判题信息 judgeInfo 更新判题状态
        // 更新数据库的判题结果 这个SUCCEED是判题服务结束， 无关答案对错
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(boxJudgeInfo));
        questionSubmitUpdate.setStatus(boxStatus);
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目提交状态为 判题结束 失败");
        }

        // 5. 对比执行结果 这里使用策略模式 得到返回结果
        // 比如原本设定的timeLimit是针对C++的， 执行java可能要额外花10s， 因此需要不同的判题策略（question里面定义的判题标准是针对C++的）
        // 先通过代码沙箱得到 实际的执行结果， 然后针对不同的情况(比如语言)，使用不同的判题策略， 最终得到返回结果，

        // todo status封装成Enum // 1 表示成功 0表示失败
        // 如何切换策略? 定义一个JudgeManager， 根据传入的关键词 返回策略（工厂）
        QuestionSubmitVO result = new QuestionSubmitVO();
        result.setLanguage(language);
        result.setCode(code);
        result.setJudgeInfo(boxJudgeInfo);
//        result.setStatus(0);
        result.setQuestionId(questionId);
        result.setUserId(userId);

        JudgeStrategy judgeStrategy = JudgeStrategyFactory.getStrategy(language);
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(boxJudgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(boxOutputList);
        judgeContext.setQuestion(question);
        judgeContext.setStatus(boxStatus);

        QuestionSubmitVO questionSubmitVO = judgeStrategy.doJudge(judgeContext, judgeConfigBean, outputList);
        questionSubmitVO.setLanguage(language);
        questionSubmitVO.setCode(code);
        questionSubmitVO.setQuestionId(questionId);
        questionSubmitVO.setUserId(userId);

        return questionSubmitVO;
    }
}

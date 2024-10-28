package com.atyichen.yiojbackendjudgeservice.judge.strategy;


import com.atyichen.yiojbackendmodel.model.codesandbox.JudgeInfo;
import com.atyichen.yiojbackendmodel.model.entity.Question;
import com.atyichen.yiojbackendmodel.model.vo.JudgeConfig;
import com.atyichen.yiojbackendmodel.model.vo.QuestionSubmitVO;

import java.util.List;

/**
 * 默认的判题策略
 */
public class DefaultJudgeStrategy implements JudgeStrategy {
    @Override
    public QuestionSubmitVO doJudge(JudgeContext judgeContext, JudgeConfig needJudgeConfig, List<String> needOutputList) {
        // 对比沙箱运行的结果 和 预期的结果的区别
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        int status = judgeContext.getStatus();

        QuestionSubmitVO questionSubmitVOResult = new QuestionSubmitVO();
        questionSubmitVOResult.setJudgeInfo(new JudgeInfo());
        questionSubmitVOResult.setStatus(0);

        if (status == 0) {
            questionSubmitVOResult.setStatus(0);
            return questionSubmitVOResult;
        }

        if (judgeInfo.getMemory() > needJudgeConfig.getMemoryLimit() || judgeInfo.getTime() > needJudgeConfig.getTimeLimit()) {
            questionSubmitVOResult.setStatus(0);
            return questionSubmitVOResult;
        }
        if (outputList.size() != needOutputList.size()) {
            questionSubmitVOResult.setStatus(0);
            return questionSubmitVOResult;
        }
        int flag = 0;
        for (int i = 0; i < needOutputList.size(); i++) {
            String s = needOutputList.get(i);
            if (!s.equals(outputList.get(i))) {
                flag = 1;
                break;
            }
        }
        if (flag == 1) {
            questionSubmitVOResult.setStatus(0);
            return questionSubmitVOResult;
        }

        questionSubmitVOResult.setStatus(1);
        judgeInfo.setMessage("答案正确");
        questionSubmitVOResult.setJudgeInfo(judgeInfo);
        return questionSubmitVOResult;
    }
}

package com.atyichen.yiojbackendjudgeservice.judge.strategy;


import com.atyichen.yiojbackendmodel.model.vo.JudgeConfig;
import com.atyichen.yiojbackendmodel.model.vo.QuestionSubmitVO;

import java.util.List;

/**
 * 根据策略
 * 针对不同的语言 使用不同的判题标准
 */

//public class JudgeInfo {
//    /**
//     * 程序执行信息
//     */
//    private String message;
//    /**
//     * 消耗内存
//     */
//    private Long memory;
//    /**
//     * 消耗时间
//     */
//    private Long time;
//}
public interface JudgeStrategy {
    /**
     * 得到判题标准的接口
     * 就是得到沙箱的判题结果以后， 根据不同的条件（比如java， 就稍微放长一点时间）
     * @param judgeContext
     * @return
     */
    QuestionSubmitVO doJudge(JudgeContext judgeContext, JudgeConfig needJudgeConfig, List<String> needOutputList);
}

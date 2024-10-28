package com.atyichen.yiojbackendjudgeservice.judge.strategy;

import com.atyichen.yiojbackendmodel.model.codesandbox.JudgeInfo;
import com.atyichen.yiojbackendmodel.model.entity.Question;
import lombok.Data;

import java.util.List;

/**
 * 上下文 用于定义在策略中传递的参数
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;
    private List<String> inputList;
    private List<String> outputList;
    private Question question;
    /**
     * 执行状态
     */
    private Integer status;
}

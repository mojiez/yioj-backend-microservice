package com.atyichen.yiojbackendjudgeservice.judge.codesandbox.impl;


import com.atyichen.yiojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.atyichen.yiojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.atyichen.yiojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.atyichen.yiojbackendmodel.model.codesandbox.JudgeInfo;

import java.util.List;

public class ExampleCodeSandbox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();

//        System.out.println("run ExampleCodeSandbox");
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("这是模拟代码沙箱");
        executeCodeResponse.setStatus(0);
        executeCodeResponse.setJudgeInfo(new JudgeInfo("moni",100L,100L));

        return executeCodeResponse;
    }
}

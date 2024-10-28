package com.atyichen.yiojbackendjudgeservice.judge.codesandbox.impl;


import com.atyichen.yiojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.atyichen.yiojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.atyichen.yiojbackendmodel.model.codesandbox.ExecuteCodeResponse;

public class ThirdPartyCodeSandbox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("run ThirdPartyCodeSandbox");
        return null;
    }
}

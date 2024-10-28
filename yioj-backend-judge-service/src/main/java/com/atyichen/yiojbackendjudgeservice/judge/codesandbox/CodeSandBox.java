package com.atyichen.yiojbackendjudgeservice.judge.codesandbox;


import com.atyichen.yiojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.atyichen.yiojbackendmodel.model.codesandbox.ExecuteCodeResponse;

public interface CodeSandBox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}

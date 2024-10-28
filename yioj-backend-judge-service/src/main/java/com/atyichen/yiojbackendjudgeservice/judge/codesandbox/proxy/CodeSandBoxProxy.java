package com.atyichen.yiojbackendjudgeservice.judge.codesandbox.proxy;


import com.atyichen.yiojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.atyichen.yiojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.atyichen.yiojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeSandBoxProxy implements CodeSandBox {
    private CodeSandBox codeSandBox;

    public CodeSandBoxProxy(CodeSandBox codeSandBox) {
        this.codeSandBox = codeSandBox;
    }

    /**
     * 使用代理模式增强原有的沙箱的功能 （AOP也是通过代理模式来实现的）
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱执行前");
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        log.info("代码沙箱执行后");
        return executeCodeResponse;
    }
}

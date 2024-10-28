package com.atyichen.yiojbackendjudgeservice.judge.codesandbox.factory;

import com.atyichen.yiojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.atyichen.yiojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.atyichen.yiojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.atyichen.yiojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import org.springframework.stereotype.Component;

@Component
public class CodeBoxFactory {
    public static CodeSandBox newInstance(String type) {
        switch (type) {
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}

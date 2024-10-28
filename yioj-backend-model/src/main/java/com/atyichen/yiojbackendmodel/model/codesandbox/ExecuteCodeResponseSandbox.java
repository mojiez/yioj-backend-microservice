package com.atyichen.yiojbackendmodel.model.codesandbox;

import lombok.Data;

@Data
public class ExecuteCodeResponseSandbox {
    private String outputList;
    /**
     * 接口信息
     */
    private String message;
    /**
     * 执行状态
     */
    private String status;
    /**
     * 判题信息
     */
    private String judgeInfo;
}

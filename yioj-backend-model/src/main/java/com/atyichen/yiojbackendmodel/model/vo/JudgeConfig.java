package com.atyichen.yiojbackendmodel.model.vo;

import lombok.Data;

@Data
public class JudgeConfig {
    private Long memoryLimit;
    private Long timeLimit;
    private Long stackLimit;
}

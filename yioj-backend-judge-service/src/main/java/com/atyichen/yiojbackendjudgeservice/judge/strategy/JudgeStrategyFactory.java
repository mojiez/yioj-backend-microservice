package com.atyichen.yiojbackendjudgeservice.judge.strategy;

public class JudgeStrategyFactory {
    public static JudgeStrategy getStrategy(String language) {
        if (language.equals("java")) {
            return new JavaLanguageJudgeStrategy();
        }
        return new DefaultJudgeStrategy();
    }
}

package com.atyichen.yiojbackendmodel.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum QuestionSubmitStatusEnum {
    WATTING("等待中", "0"),
    RUNNING("判题中","1"),
    SUCCEED("成功","2"),
    FAILED("失败","3")
    ;
    private final String text;
    private final String value;

    QuestionSubmitStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        // values方法可以将枚举类转变为一个枚举类型的数组
        return Arrays.stream(values()).map(item -> {
            return item.value;
        }).collect(Collectors.toList());
    }

    public static QuestionSubmitStatusEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionSubmitStatusEnum anEnum : QuestionSubmitStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}

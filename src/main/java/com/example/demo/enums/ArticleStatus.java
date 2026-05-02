package com.example.demo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleStatus {
    DRAFT(0, "草稿"),
    PUBLISH(1, "发布");

    @EnumValue
    private final Integer code;
    private final String desc;

    public static ArticleStatus fromCode(Integer code) {
        for (ArticleStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("非法文章状态: " + code);
    }
}

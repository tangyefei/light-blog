package com.example.demo.japaflow.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class FavoriteAddVo {
    @NotBlank(message = "itemType 不能为空")
    private String itemType;

    @NotBlank(message = "itemId 不能为空")
    private String itemId;

    @NotNull(message = "snapshot 不能为空")
    private Map<String, Object> snapshot;
}

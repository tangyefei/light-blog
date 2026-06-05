package com.example.demo.japaflow.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class WrongBookAddVo {
    @NotBlank(message = "itemType 不能为空")
    private String itemType;

    @NotBlank(message = "itemId 不能为空")
    private String itemId;

    private Map<String, Object> wrongDetail;
}

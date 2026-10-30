package com.example.products_inventory.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 商品注文リクエストDTO (画面からの入力を受け取る)
 */
@Getter
@Setter
@NoArgsConstructor // フレームワークのJSONデパース用に必要
public class ProductOrderRequest {

    @NotBlank(message = "カラーは必須です。")
    private String colorCode; // 例: "RED", "BLUE"

    @NotBlank(message = "サイズは必須です。")
    private String sizeCode;  // 例: "M", "L"

    @NotNull(message = "数量は必須です。")
    @Min(value = 1, message = "数量は1以上で指定してください。")
    private Integer quantity;
}

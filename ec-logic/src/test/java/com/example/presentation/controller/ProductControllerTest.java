package com.example.presentation.controller;

import com.example.application.usecase.products.ProductOrderUseCase;
import com.example.application.usecase.products.ProductQueryUseCase;
import com.example.presentation.dto.ProductDetailResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given; // MockitoはBDDスタイルが推奨されます

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductOrderUseCase productOrderUseCase;

    @MockBean
    private ProductQueryUseCase productQueryUseCase;

    @Test
    void 商品詳細のURLにアクセスしたとき_商品が存在すればステータス200と商品情報が返ってくること() throws Exception {
        // Arrange (準備)
        String targetId = "1"; // もしIDがString型なら "1" にしてください
        ProductDetailResponse dummyResponse = new ProductDetailResponse(targetId, "テスト商品", "1500", null);

        // ★ベストプラクティス: モックの挙動を明確に定義する (givenを使用)
        given(productQueryUseCase.getProductDetail(targetId)).willReturn(dummyResponse);

        // Act (実行) ＆ Assert (検証)
        mockMvc.perform(get("/api/products/{id}", targetId)) // ★正しいプレースホルダーの書き方
                .andExpect(status().isOk())                  // ① ステータスが200であることを検証
                .andExpect(jsonPath("$.productId").value(targetId)) // ② 中身のJSONのIDが一致するか検証
                .andExpect(jsonPath("$.productName").value("テスト商品")); // ③ 中身のJSONの名前が一致するか検証
    }

    @Test
    void 商品注文のURLにPOSTしたとき_正常なリクエストであればステータス200が返りユースケースが実行されること() throws Exception {
        // Arrange (準備)
        String targetId = "product-123";

        // 💡 送信するJSON文字列を組み立てる
        String requestBody = """
            {
                "colorCode": "RED",
                "sizeCode": "M",
                "quantity": 2
            }
            """;

        // Act (実行) ＆ Assert (検証)
        mockMvc.perform(post("/api/products/{id}/order", targetId) // プレースホルダーでIDを指定
                        .contentType(MediaType.APPLICATION_JSON)   // JSON送信の宣言
                        .content(requestBody))                     // ボディをセット
                .andExpect(status().isOk());                       // ① ステータス200を検証

        // ★ベストプラクティス: コントローラーがユースケースのメソッドを「正しい引数で1回だけ呼び出したか」を徹底検証
        then(productOrderUseCase)
                .should(times(1))
                .execute(targetId, "RED", "M", 2);
    }

    @Test
    void 商品注文のURLにPOSTしたとき_バリデーションエラーがあればステータス400が返ること() throws Exception {
        // Arrange (準備)
        String targetId = "product-123";

        // 💡 不正なJSON（例: 注文数量がマイナスなど、@Validに引っかかる想定のボディ）
        String invalidRequestBody = """
            {
                "colorCode": "",
                "sizeCode": "M",
                "quantity": -1
            }
            """;

        // Act (実行) ＆ Assert (検証)
        mockMvc.perform(post("/api/products/{id}/order", targetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest()); // ② @Validによる400 Bad Requestを検証
    }
}

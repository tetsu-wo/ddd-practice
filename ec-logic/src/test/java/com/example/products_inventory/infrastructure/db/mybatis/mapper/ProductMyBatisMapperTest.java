package com.example.products_inventory.infrastructure.db.mybatis.mapper;

import com.example.products_inventory.infrastructure.db.jpa.entity.SkuJpaEntity;
import com.example.products_inventory.presentation.dto.ProductDetailResponse;
import com.example.products_inventory.infrastructure.db.mybatis.mapper.ProductMyBatisMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@MybatisTest
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "mybatis.mapper-locations=classpath:mapper/ProductMyBatisMapper.xml") // ←これを追加// H2などのインメモリDBを使用
@DisplayName("ProductMyBatisMapperの連動テスト")
class ProductMyBatisMapperTest {

//    @Autowired
//    private Flyway flyway;
//
//    @BeforeEach
//    void setUp() {
//        flyway.clean();
//        flyway.migrate();
//    }

    @Autowired
    private ProductMyBatisMapper productMyBatisMapper;

    @Nested
    @DisplayName("findProductDetailById メソッドのテスト")
    class FindProductDetailById {

        @Test
//        @Sql(statements = {
//                "INSERT INTO products (id, name, price) VALUES ('prod-001', 'テスト商品', 1500)"
//        })
        @DisplayName("存在する商品IDを指定した場合、該当する商品詳細が取得できること")
        void shouldReturnProductDetail_WhenProductExists() {
            // Arrange
            String targetProductId = "prod-uuid-0001";

            // Act
            ProductDetailResponse response = productMyBatisMapper.findProductDetailById(targetProductId);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.productId()).isEqualTo(targetProductId);
            assertThat(response.productName()).isEqualTo("プレミアムTシャツ");
        }

        @Test
        @DisplayName("存在しない商品IDを指定した場合、nullが返却されること")
        void shouldReturnNull_WhenProductDoesNotExist() {
            // Act
            ProductDetailResponse response = productMyBatisMapper.findProductDetailById("non-existent-id");

            // Assert
            assertThat(response).isNull();
        }
    }

    @Nested
    @DisplayName("insertSkuBatch メソッドのテスト")
    class InsertSkuBatch {

        @Test
        @DisplayName("SKUリストを渡した場合、一括で正常にインサートできること")
        void shouldInsertSkusInBatch() {
            // Arrange
            SkuJpaEntity sku1 = new SkuJpaEntity("sku-001", "prod-001", "Red", 100);
            SkuJpaEntity sku2 = new SkuJpaEntity("sku-002", "prod-001", "Blue", 500);
            List<SkuJpaEntity> skus = List.of(sku1, sku2);

            // Act
            productMyBatisMapper.insertSkuBatch(skus);

            // Assert
            // 実際にインサートされたかをselectTest等、または検証用SQLで確認（ここでは仮にselectTest等を利用するか、Mapperを追加して検証）
            int totalCount = productMyBatisMapper.selectTest();
            assertThat(totalCount).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("selectTest メソッドのテスト")
    class SelectTest {

        @Test
//        @Sql(statements = {
//                "INSERT INTO test_table (id) VALUES (1)",
//                "INSERT INTO test_table (id) VALUES (2)"
//        })
        @DisplayName("テスト用レコードの総数が正しく取得できること")
        void shouldReturnCorrectCount() {
            // Act
            int count = productMyBatisMapper.selectTest();

            // Assert
            assertThat(count).isEqualTo(1);
        }
    }
}

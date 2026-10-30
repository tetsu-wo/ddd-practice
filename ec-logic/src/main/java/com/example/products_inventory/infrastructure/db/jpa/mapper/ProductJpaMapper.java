package com.example.products_inventory.infrastructure.db.jpa.mapper;

import com.example.products_inventory.domain.model.*;
import com.example.products_inventory.infrastructure.db.jpa.entity.ProductJpaEntity;
import com.example.products_inventory.infrastructure.db.jpa.entity.SkuJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductJpaMapper {

    public ProductJpaEntity toEntity(Product product) {
        if (product == null) return null;

        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(product.getProductIdentity().id());
        entity.setName(product.getProductIdentity().name());
        entity.setSalesStartPeriod(product.getProductIdentity().salesStartPeriod());
        entity.setSalesEndPeriod(product.getProductIdentity().salesEndPeriod());
        entity.setStatus(product.getStatus());

        SkuJpaEntity skuJpaEntity = new SkuJpaEntity();
        List<SkuJpaEntity> skus = product.getSkus().stream()
                .map(skurecord -> {
                    skuJpaEntity.setId(skurecord.getSkuidentity().id());
                    skuJpaEntity.setColor(skurecord.getSkuidentity().color().value());
                    skuJpaEntity.setSize(skurecord.getSkuidentity().size().value());
                    return skuJpaEntity;
                })
                .collect(Collectors.toList());
        entity.setSkus(skus);
        return entity;
    }

    public static Product toDomain(ProductJpaEntity entity) {
        if (entity == null) return null;
        // 💡 List<Entity> を List<Record> に Stream で変換

        ProductIdentity productIdentity = toProductIdentity(entity);

        List<SKU> skus = entity.getSkus().stream()
                .map(skuEntity -> toSkuDomain(entity.getId(), skuEntity))
                .collect(Collectors.toList());
        String status = entity.getStatus();

        return Product.reconstruct(productIdentity, skus, status);
    }
        private static SKU toSkuDomain(String productId, SkuJpaEntity skuEntity) {
            if (skuEntity == null) return null;

            SkuIdentity skuIdentity = new SkuIdentity(
                    productId,
                    new Color(skuEntity.getColor()),
                    new Size(skuEntity.getSize())
            );
            return new SKU(skuIdentity, skuEntity.getStockQuantity());
        }

        private static ProductIdentity toProductIdentity (ProductJpaEntity entity){
            return new ProductIdentity(
                    entity.getId(),
                    entity.getName(),
                    entity.getSalesStartPeriod(),
                    entity.getSalesEndPeriod()
            );
        }

}


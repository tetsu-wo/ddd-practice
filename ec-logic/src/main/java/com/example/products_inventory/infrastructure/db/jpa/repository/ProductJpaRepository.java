package com.example.products_inventory.infrastructure.db.jpa.repository;

import com.example.products_inventory.infrastructure.db.jpa.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, String> {
}

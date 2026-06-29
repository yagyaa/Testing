package com.javatechie.redis.respository;

import com.javatechie.redis.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDao extends JpaRepository<Product, Integer> {
}

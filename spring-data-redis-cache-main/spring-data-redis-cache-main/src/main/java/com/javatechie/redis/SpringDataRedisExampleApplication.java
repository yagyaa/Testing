package com.javatechie.redis;

import com.javatechie.redis.entity.Product;
import com.javatechie.redis.respository.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/product")
@EnableCaching
public class SpringDataRedisExampleApplication {

    @Autowired
    private ProductDao dao;

    @PostMapping
    @Caching(evict = {
            @CacheEvict(value = "Product", key = "#product.id"),
            @CacheEvict(value = "Products", allEntries = true)
    })
    public Product save(@RequestBody Product product) {
        return dao.save(product);
    }

    @GetMapping
    @Cacheable(value = "Products")
    public List<Product> getAllProducts() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    @Cacheable(key = "#id", value = "Product", unless = "#result.price > 1000")
    public Product findProduct(@PathVariable int id) {
        System.out.println("called findProduct() from MySQL");
        return dao.findById(id).orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    @DeleteMapping("/{id}")
    @Caching(evict = {
            @CacheEvict(key = "#id", value = "Product"),
            @CacheEvict(value = "Products", allEntries = true)
    })
    public String remove(@PathVariable int id) {
        dao.deleteById(id);
        return "product removed !!";
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringDataRedisExampleApplication.class, args);
    }

}

package com.example.simplezakka.controller;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

 // 検索（JSON）
    @GetMapping("/sort")
    public ResponseEntity<List<ProductListItem>> sortProducts(@RequestParam(required = false) String keyword) {
        List<ProductListItem> products = productService.getSortedProducts(keyword);
        return ResponseEntity.ok(products);
    }

    // 全商品取得（JSON）
    @GetMapping
    public ResponseEntity<List<ProductListItem>> getAllProducts() {
        List<ProductListItem> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    // 検索（JSON）
    @GetMapping("/search")
    public ResponseEntity<List<ProductListItem>> searchProducts(@RequestParam(required = false) String keyword) {
        List<ProductListItem> products = productService.searchProductsByName(keyword);
        return ResponseEntity.ok(products);
    }

    // IDで商品詳細取得（JSON）
    @GetMapping("/{productId:[0-9]+}")
    public ResponseEntity<ProductDetail> getProductById(@PathVariable Integer productId) {
        ProductDetail product = productService.findProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
}

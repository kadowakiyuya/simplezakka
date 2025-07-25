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

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 商品一覧の検索、フィルタリング、ソートを全てこのエンドポイントで処理
    // 指定なしで全商品取得
    @GetMapping 
    public ResponseEntity<List<ProductListItem>> getFilteredAndSortedProducts(
            @RequestParam(required = false) String keyword,  // キーワード検索
            @RequestParam(required = false) String category, // カテゴリー検索
            @RequestParam(required = false, defaultValue = "new") String sort) { // sort
        List<ProductListItem> products = productService.getFilteredAndSortedProducts(keyword, category, sort);
        return ResponseEntity.ok(products);
    }

    // IDで商品詳細取得（JSON）
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetail> getProductById(@PathVariable Integer productId) {
        ProductDetail product = productService.findProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
}
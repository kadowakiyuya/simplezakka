package com.example.simplezakka.controller;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.simplezakka.dto.product.ProductCategory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // --- 商品名検索エンドポイント ---
    // このエンドポイントは、パス変数を含むエンドポイントよりも物理的に上に配置することが推奨されますが、
    // パス変数に正規表現を追加することで、より確実にルーティングを制御できます。
    @GetMapping("/products")
    public ResponseEntity<List<ProductListItem>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) { 

        // ProductServiceの新しい統合された検索メソッドを呼び出します
        List<ProductListItem> products = productService.searchAndFilterProducts(keyword, category);
        return ResponseEntity.ok(products);

    }

   

    // --- IDによる商品詳細取得エンドポイント ---
    @GetMapping("/products/{productId:[0-9]+}") // ここを修正しました
    public ResponseEntity<ProductDetail> getProductById(@PathVariable Integer productId) {
        ProductDetail product = productService.findProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    
    
    // --- カテゴリ名による商品一覧取得エンドポイント ---
    @GetMapping("/products/category/{categoryName}")
    public ResponseEntity<List<ProductListItem>> getProductsByCategory(@PathVariable String categoryName) {
        List<ProductListItem> products = productService.searchAndFilterProducts(null, categoryName);
        return ResponseEntity.ok(products);
    }
}

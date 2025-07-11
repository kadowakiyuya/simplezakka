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

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // --- 商品名検索エンドポイント ---
    // このエンドポイントは、パス変数を含むエンドポイントよりも物理的に上に配置することが推奨されますが、
    // パス変数に正規表現を追加することで、より確実にルーティングを制御できます。
    @GetMapping("/search")
    public ResponseEntity<List<ProductListItem>> searchProducts
        (@RequestParam(required = false) String keyword, 
        @RequestParam(required = false) String categoryName
        ) {
        List<ProductListItem> products;

        if (keyword != null && !keyword.isEmpty()) {
        // キーワードのみで検索
            products = productService.searchProductsByName(keyword);
        } 
        else if (categoryName != null && !categoryName.isEmpty()) {
        // カテゴリー名のみで検索
            products = productService.searchProductsByCategory(categoryName);
        }
        else {
            // キーワードもカテゴリー名も指定されていない場合は全商品を返す
            products = productService.findAllProducts();
        }
        return ResponseEntity.ok(products);

    }

   

    // --- 全ての商品リスト取得エンドポイント ---
    @GetMapping
    public ResponseEntity<List<ProductListItem>> getAllProducts() {
        List<ProductListItem> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    // --- IDによる商品詳細取得エンドポイント ---
    // ★★★ 修正箇所: {productId} に正規表現を追加し、数値のみにマッチするようにする ★★★
    @GetMapping("/{productId:[0-9]+}") // ここを修正しました
    public ResponseEntity<ProductDetail> getProductById(@PathVariable Integer productId) {
        ProductDetail product = productService.findProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
}
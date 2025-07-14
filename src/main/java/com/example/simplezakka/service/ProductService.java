package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList; // ArrayListを使う可能性があるのでインポート
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductListItem> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }

    public ProductDetail findProductById(Integer productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.map(this::convertToDetail).orElse(null);
    }

   
    /**
     * 商品名で部分一致検索を行うサービスメソッド。
     * キーワードがnullまたは空文字の場合は、すべての商品を返します。
     *
     * @param keyword 検索キーワード
     * @return 検索条件に合致する商品のリスト（ProductListItem形式）
     */
    public List<ProductListItem> searchProductsByName(String keyword) {
        // キーワードがnullまたは空文字の場合のハンドリング
        if (keyword == null || keyword.trim().isEmpty()) {
            // 例：キーワードがない場合は全件表示
            return findAllProducts();
        }
        // ProductRepositoryのfindByNameContainingIgnoreCaseメソッドを呼び出し、
        // その結果をProductListItemのリストに変換して返す
        return productRepository.findByNameContainingIgnoreCase(keyword.trim()).stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }

    // すべての商品を取得
    public List<ProductListItem> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }

    // カテゴリ名で商品を取得
    public List<ProductListItem> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoryName(categoryName).stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }

    // Product → ProductListItem への変換（例）
    private ProductListItem convertToListItem(Product product) {
        return new ProductListItem(product.getId() ,product.getName(), product.getPrice(), product.getImageUrl());
    }
    
   
    

    private ProductDetail convertToDetail(Product product) {
        return new ProductDetail(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getMaterial(),
                product.getCategory(),
                product.getStock(),
                product.getImageUrl()
        );
    }
}
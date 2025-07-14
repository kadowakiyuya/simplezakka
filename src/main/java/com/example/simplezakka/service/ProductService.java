package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductCategory;
import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList; 
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Set;

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

   
    // --- 統合された商品検索・フィルタリングメソッド ---
    /**
     * キーワードとカテゴリ名に基づいて商品を検索・フィルタリングします。
     * キーワードもカテゴリ名も指定されない場合は、すべての商品を返します。
     *
     * @param keyword 検索キーワード（商品名または説明の部分一致）
     * @param categoryName カテゴリ名（完全一致）
     * @return 検索条件に合致するProductListItemのリスト
     */
    public List<ProductListItem> searchAndFilterProducts(String keyword, String categoryName) {
        // ProductRepositoryの新しいfindByKeywordAndCategoryメソッドを呼び出します
        List<Product> products = productRepository.findByKeywordAndCategory(keyword, categoryName);
        return products.stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }
    


    private ProductListItem convertToListItem(Product product) {
        return new ProductListItem(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
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
// src/main/java/com/example/simplezakka/service/CategoryService.java
package com.example.simplezakka.service;

import com.example.simplezakka.entity.Product;
import com.example.simplezakka.dto.product.ProductCategory; 
import com.example.simplezakka.repository.ProductRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final ProductRepository productRepository; 

    @Autowired
    public CategoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     登録されている全ての商品からカテゴリ名を抽出し、重複を除去
     * @return ユニークなカテゴリ名のリスト（String形式）
     */
    public List<String> getAllUniqueCategoryNames() { 
        // ProductRepositoryから全てのProductエンティティを取得
        List<Product> products = productRepository.findAll(); 

        if (products == null || products.isEmpty()) {
            return List.of(); // 空のリストを返す
        }

        Set<String> uniqueCategoryNames = products.stream()
                .map(Product::getCategory) // Productからカテゴリ名を取得
                .collect(Collectors.toSet()); // 重複を排除

        // SetからList<String>に変換して返す
        return uniqueCategoryNames.stream()
                                  .sorted() // ソート
                                  .collect(Collectors.toList());
    }
}
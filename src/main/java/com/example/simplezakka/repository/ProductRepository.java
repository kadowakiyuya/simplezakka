package com.example.simplezakka.repository;

import com.example.simplezakka.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    //商品名での部分一致検索メソッド
    /**
     * 商品名で部分一致検索を行う（大文字小文字を区別しない）メソッド。
     * Spring Data JPAの命名規則により、自動的にSQLクエリが生成されます。
     *
     * @param name 検索キーワード（部分一致の対象となる文字列）
     * @return 検索条件に合致するProductエンティティのリスト
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    // 商品名での部分一致検索メソッドここまで

    // カテゴリ名での完全一致検索メソッド
    List<Product> findByCategory(String categoryName);
    // カテゴリ名での完全一致検索メソッドここまで

    // 商品名または説明での部分一致、かつカテゴリでの完全一致検索
    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR LOWER(p.category) = LOWER(:category))")
    List<Product> findByKeywordAndCategory(String keyword, String category);
    
    // 重複しないカテゴリのリストを取得
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctCategories();
    
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - ?2 WHERE p.productId = ?1 AND p.stock >= ?2")
    int decreaseStock(Integer productId, Integer quantity);
}



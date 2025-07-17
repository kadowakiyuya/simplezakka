package com.example.simplezakka.repository;

import com.example.simplezakka.dto.product.ProductListItem;
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
    
    //商品名での部分一致及びカテゴリ名での完全一致検索メソッド
    List<Product> findByNameContainingIgnoreCaseAndCategory(String name, String category);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - ?2 WHERE p.productId = ?1 AND p.stock >= ?2")
    int decreaseStock(Integer productId, Integer quantity);
   

}
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

    // --- 新規追加: 商品名での部分一致検索メソッド ---
    /**
     * 商品名で部分一致検索を行う（大文字小文字を区別しない）メソッド。
     * Spring Data JPAの命名規則により、自動的にSQLクエリが生成されます。
     *
     * @param name 検索キーワード（部分一致の対象となる文字列）
     * @return 検索条件に合致するProductエンティティのリスト
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    // --- 新規追加ここまで ---
    
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - ?2 WHERE p.productId = ?1 AND p.stock >= ?2")
    int decreaseStock(Integer productId, Integer quantity);
    @Query("SELECT new com.example.simplezakka.dto.product.ProductListItem(p.productId, p.name, p.price, p.imageUrl) FROM Product p ORDER BY p.createdAt DESC")
    List<ProductListItem> findAllOrderByCreatedAtDesc();
 
    @Query("SELECT new com.example.simplezakka.dto.product.ProductListItem(p.productId, p.name, p.price, p.imageUrl) FROM Product p ORDER BY p.price ASC")
    List<ProductListItem> findAllOrderByPriceAsc();

    @Query("SELECT new com.example.simplezakka.dto.product.ProductListItem(p.productId, p.name, p.price, p.imageUrl) FROM Product p ORDER BY p.price DESC")
    List<ProductListItem> findAllOrderByPriceDesc();

    @Query("SELECT new com.example.simplezakka.dto.product.ProductListItem(p.productId, p.name, p.price, p.imageUrl) FROM Product p ORDER BY p.name ASC")
    List<ProductListItem> findAllOrderByName();

}



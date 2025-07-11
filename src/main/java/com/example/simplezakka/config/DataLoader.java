package com.example.simplezakka.config;

import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Autowired
    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        loadSampleProducts();
    }

    private void loadSampleProducts() {
        if (productRepository.count() > 0) {
            return; // すでにデータが存在する場合はスキップ
        }

        List<Product> products = Arrays.asList(
            createProduct(
                "シンプルデスクオーガナイザー", 
<<<<<<< HEAD
                "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 
                "木材",
=======
                "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。",
                null,
                null,
>>>>>>> origin/develop
                3500, 
                20, 
                "/images/desk-organizer.png", 
                true


            ),
            createProduct(
                "アロマディフューザー（ウッド）", 
<<<<<<< HEAD
                "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 
                "木材、プラスチック", 
=======
                "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。",
                null,
                null,
>>>>>>> origin/develop
                4200, 
                15, 
                "/images/aroma-diffuser.png", 
                true


            ),
            createProduct(
                "コットンブランケット", 
<<<<<<< HEAD
                "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 
                "コットン", 
=======
                "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。",
                null,
                null,
>>>>>>> origin/develop
                5800, 
                10, 
                "/images/cotton-blanket.png", 
                false


            ),
            createProduct(
                "ステンレスタンブラー", 
                "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。",
<<<<<<< HEAD
                "ステンレス", 
=======
                null,
                null,
>>>>>>> origin/develop
                2800, 
                30, 
                "/images/tumbler.png", 
                false


            ),
            createProduct(
                "ミニマルウォールクロック", 
                "余計な装飾のないシンプルな壁掛け時計。静音設計。",
<<<<<<< HEAD
                "ガラス、プラスチック", 
=======
                null,
                null,
>>>>>>> origin/develop
                3200, 
                25, 
                "/images/wall-clock.png", 
                false


            ),
            createProduct(
                "リネンクッションカバー", 
                "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。",
<<<<<<< HEAD
                "リネン", 
=======
                null,
                null,
>>>>>>> origin/develop
                2500, 
                40, 
                "/images/cushion-cover.png", 
                true


            ),
            createProduct(
                "陶器フラワーベース", 
                "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。",
<<<<<<< HEAD
                "粘土", 
=======
                null,
                null,
>>>>>>> origin/develop
                4000, 
                15, 
                "/images/flower-vase.png", 
                false


            ),
            createProduct(
                "木製コースター（4枚セット）", 
                "天然木を使用したシンプルなデザインのコースター。4枚セット。",
<<<<<<< HEAD
                "木材", 
=======
                null,
                null,
>>>>>>> origin/develop
                1800, 
                50, 
                "/images/wooden-coaster.png", 
                false


            ),
            createProduct(
                "キャンバストートバッグ", 
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。",
<<<<<<< HEAD
                "綿", 
=======
                null,
                null,
>>>>>>> origin/develop
                3600, 
                35, 
                "/images/tote-bag.png", 
                true


            ),
            createProduct(
                "ガラス保存容器セット", 
                "電子レンジ・食洗機対応のガラス製保存容器。3サイズセット。",
<<<<<<< HEAD
                "木材、ガラス", 
=======
                null,
                null,
>>>>>>> origin/develop
                4500, 
                20, 
                "/images/glass-container.png", 
                false


            )
        );
        
        productRepository.saveAll(products);
    }
    
<<<<<<< HEAD
    private Product createProduct(String name, String description, String material, Integer price, Integer stock, String imageUrl, Boolean isRecommended) {
=======
    private Product createProduct(String name, String description, String material, String category, Integer price, Integer stock, String imageUrl, Boolean isRecommended) {
>>>>>>> origin/develop
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setMaterial(material);
<<<<<<< HEAD
=======
        product.setCategory(category);
>>>>>>> origin/develop
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setIsRecommended(isRecommended);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }
}
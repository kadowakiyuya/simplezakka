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
                "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。",
                "木材",
                "文房具",
                3500, 
                20, 
                "/images/desk-organizer.png", 
                true 
                


            ),
            createProduct(
                "アロマディフューザー（ウッド）", 
                "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。",
                "木材",
                "インテリア",
                4200, 
                15, 
                "/images/aroma-diffuser.png", 
                true


            ),
            createProduct(
                "コットンブランケット", 
                "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。",
                "コットン",
                "インテリア",
                5800, 
                10, 
                "/images/cotton-blanket.png", 
                false


            ),
            createProduct(
                "ステンレスタンブラー", 
                "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。",
                "ステンレス",
                "キッチン用品",
                2800, 
                30, 
                "/images/tumbler.png", 
                false


            ),
            createProduct(
                "ミニマルウォールクロック", 
                "余計な装飾のないシンプルな壁掛け時計。静音設計。",
                "ガラス",
                "インテリア",
                3200, 
                25, 
                "/images/wall-clock.png", 
                false


            ),
            createProduct(
                "リネンクッションカバー", 
                "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。",
                "リネン",
                "インテリア",
                2500, 
                40, 
                "/images/cushion-cover.png", 
                true


            ),
            createProduct(
                "陶器フラワーベース", 
                "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。",
                "粘土",
                "インテリア",
                4000, 
                15, 
                "/images/flower-vase.png", 
                false


            ),
            createProduct(
                "木製コースター（4枚セット）", 
                "天然木を使用したシンプルなデザインのコースター。4枚セット。",
                "木材",
                "キッチン用品",
                1800, 
                50, 
                "/images/wooden-coaster.png", 
                false


            ),
            createProduct(
                "キャンバストートバッグ", 
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。",
                "キャンバス",
                "ファッション小物",
                3600, 
                35, 
                "/images/tote-bag.png", 
                true


            ),
            createProduct(
                "ガラス保存容器セット", 
                "電子レンジ・食洗機対応のガラス製保存容器。3サイズセット。",
                "ガラス",
                "キッチン用品",
                4500, 
                20, 
                "/images/glass-container.png", 
                false


            ),
            createProduct(                                                                                                                                                                 
                "洗えるクッション",
                "ご家庭の洗濯機で洗うことができます。",
                "ポリエステル", 
                "インテリア", 
                4000, 
                20, 
                "/images/スクリーンショット 2025-07-11 181052.png", 
                true
                
            ),
            createProduct(                   
                "油性ボールペン",
                "機能性とモダンなデザインを併せ持つ。", 
                "プラスチック", 
                "文房具", 
                3200, 
                15, 
                "/images/スクリーンショット 2025-07-14 095635.png", 
                true
                
            ),
            createProduct(
                "デザイナーズチェア", 
                "テーブルに肘をかけ足を浮かすことができる軽量チェアーで設計されておりアームチェアーのような肘がついているデザイナーズチェアー", 
                "ブナ・アッシュ・メープル・チェリー・ナラ・ウォールナットなど", 
                "家具", 
                9000, 
                3, 
                "/images/椅子.jpg", 
                true
            ),
            createProduct(
                "折り畳みテーブル", 
                "コンパクトな折り畳みテーブル", 
                "天板：中質繊維板MDF、オーク転写(PU塗装)　脚：天然木ラバーウッド(PU塗装)フェルト付", 
                "家具", 
                3600, 
                5, 
                "/images/折り畳みテーブル.jpg", 
                true
                
            ),
            createProduct(
                "マグカップ（ブラック）", 
                "生涯を共にしたくなる「本物」を日本の職人の手づくりで実現したブランド", 
                "プラスチック", 
                "キッチン用品", 
                1000, 
                30, 
                "/images/マグカップ.webp", 
                true
                ),
            createProduct(
                "本革財布", 
                "キメの細かいシボ感で滑らかな手触りの上質なシュリンクレザーを使用して製作した２つ折り財布です。", 
                "本革", 
                "ファッション小物", 
                5000, 
                5, 
                "/images/財布.webp", 
                true
                ) ,
            createProduct(
                "ステンレス水筒（200ml）",
                "携帯しやすいコンパクトサイズ。口当たりよく飲めるよう、飲み口の仕様を工夫しました。", 
                "ステンレス",
                "日用品", 
                 1500, 
                 20, 
                "/images/水筒.jpg", 
                true
                ),
            createProduct(
                "モダンデスクライト", 
                "木製ベースの金属製テーブルランプ",
                "木材、金属", 
                "インテリア", 
                4500, 
                10, 
                "/images/テーブルランプ.jpg", 
                true
                ),

            createProduct(
                "1-11のテスト用", 
                "これはテスト用の商品です。",
                "木材",
                "文房具",
                3, 
                2, 
                null, 
                true 
                


            )


        );
        
        
        productRepository.saveAll(products);
    }
    
    private Product createProduct(String name, String description, String material, String category, Integer price, Integer stock, String imageUrl, Boolean isRecommended) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setMaterial(material);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setIsRecommended(isRecommended);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }
}
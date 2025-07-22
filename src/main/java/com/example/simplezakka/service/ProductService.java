
package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }




    public ProductDetail findProductById(Integer productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.map(this::convertToDetail).orElse(null);
    }

   
    /**
     * 検索キーワード、カテゴリ、ソート条件に基づいて商品をフィルタリング・ソートして取得する。
     *
     * @param keyword  検索キーワード（商品名）
     * @param category カテゴリ名
     * @param sort     ソート条件（"new", "price_asc", "price_desc", "name"など）
     * @return フィルタリング・ソートされた商品のリスト（ProductListItem形式）
     */
    public List<ProductListItem> getFilteredAndSortedProducts(String keyword, String category, String sort) {
        List<Product> products;

        // 1. フィルタリング処理
        if ((keyword != null && !keyword.trim().isEmpty()) && (category != null && !category.trim().isEmpty())) {
            // キーワードとカテゴリの両方で検索
            products = productRepository.findByNameContainingIgnoreCaseAndCategory(keyword.trim(), category.trim());
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            // キーワードのみで検索
            products = productRepository.findByNameContainingIgnoreCase(keyword.trim());
        } else if (category != null && !category.trim().isEmpty()) {
            // カテゴリのみで検索
            products = productRepository.findByCategory(category.trim());
        } else {
            // フィルタ条件が指定されない場合はすべての商品を取得
            products = productRepository.findAll();
        }

        // 2. ソート処理
        Comparator<Product> comparator = null;
        switch (sort) {
            case "price_asc":
                comparator = Comparator.comparing(Product::getPrice);
                break;
            case "price_desc":
                comparator = Comparator.comparing(Product::getPrice).reversed();
                break;
            case "name":
                comparator = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER); // 大文字小文字を区別しないソート
                break;
            case "new":
            default:
                // 最新順 (createdAt の降順)
                comparator = Comparator.comparing(Product::getCreatedAt).reversed();
                break;
        }

        if (comparator != null) {
            products.sort(comparator);
        }

        // 3. ProductListItem DTOへの変換
        return products.stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }
   
    


    private ProductListItem convertToListItem(Product product) {
        return new ProductListItem(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getCreatedAt(),
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

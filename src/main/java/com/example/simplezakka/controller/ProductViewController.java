// src/main/java/com/example/simplezakka/controller/ProductViewController.java

package com.example.simplezakka.controller;

import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductViewController {

    private final ProductService productService;

    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    // 商品一覧ページ（テンプレート表示）
    @GetMapping("/products")
    public String listProducts(@RequestParam(name = "sort", required = false, defaultValue = "new") String sort,
                               Model model) {
        List<ProductListItem> products = productService.getSortedProducts(sort);
        model.addAttribute("products", products);
        return "index"; // templates/product_list.html を表示
    }
}

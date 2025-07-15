// src/main/java/com/example/simplezakka/controller/CategoryController.java （修正）
package com.example.simplezakka.controller;

import com.example.simplezakka.service.CategoryService; // CategoryServiceをインポート
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService; // CategoryServiceをインジェクト

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // --- カテゴリリスト取得エンドポイント ---
    @GetMapping("/categories")
    public List<String> getAllCategories() {
        // CategoryServiceからユニークなカテゴリ名のリストを取得して返す
        return categoryService.getAllUniqueCategoryNames();
    }
}
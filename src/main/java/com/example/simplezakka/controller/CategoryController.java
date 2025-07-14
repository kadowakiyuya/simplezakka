package com.example.simplezakka.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    // --- カテゴリリスト取得エンドポイント ---
    // ここでカテゴリ名を文字列のリストとして直接返します
    @GetMapping("/categories")
    public List<String> getAllCategories() {
        // 現在は仮のデータですが、将来的にはDBなどから取得するロジックを実装します
        return Arrays.asList("家具", "インテリア", "寝具", "雑貨");
    }
}
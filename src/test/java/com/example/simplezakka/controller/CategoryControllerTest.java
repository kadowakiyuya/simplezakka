package com.example.simplezakka.controller;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.service.ProductService;
import com.example.simplezakka.service.CategoryService; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections; // 空リスト用
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("カテゴリ名が存在する場合、ユニークなカテゴリ名のリストを返す")
    void getAllCategories_WhenCategoriesNameExist_ShouldReturnUniqueCategoryName() throws Exception {
        // Arrange
        var categoryList = Arrays.asList("インテリア", "キッチン用品", "ファッション小物", "家具", "文房具", "日用品");
        when(categoryService.getAllUniqueCategoryNames()).thenReturn(categoryList);

        // Act & Assert
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(categoryList.size()))
                .andExpect(jsonPath("$[0]").value("インテリア"))
                .andExpect(jsonPath("$[1]").value("キッチン用品"))
                .andExpect(jsonPath("$[2]").value("ファッション小物"))
                .andExpect(jsonPath("$[3]").value("家具"))
                .andExpect(jsonPath("$[4]").value("文房具"))
                .andExpect(jsonPath("$[5]").value("日用品"));
    }

    @Test
    @DisplayName("カテゴリ名が存在しない場合、空のリストを返す")
    void getAllCategories_WhenNoCategoriesNamesExist_ShouldReturnEmptyList() throws Exception {
        when(categoryService.getAllUniqueCategoryNames()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("サービスが例外を投げた場合、500エラーを返す")
    void getAllCategories_WhenCategoriesNameExist_ShouldReturnInternalServiceError() throws Exception {
        when(categoryService.getAllUniqueCategoryNames()).thenThrow(new RuntimeException("DB接続失敗"));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("DB接続失敗"));
    }
}

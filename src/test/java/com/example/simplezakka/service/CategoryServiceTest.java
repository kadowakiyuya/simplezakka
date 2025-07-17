package com.example.simplezakka.service;

import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 通常ケース：カテゴリの重複排除 + アルファベット順ソート
    @Test
    void getAllUniqueCategoryNames_ShouldReturnUniqueCategoryNamesList() {
        Product p1 = new Product();
        p1.setName("商品A");
        p1.setCategory("キッチン");

        Product p2 = new Product();
        p2.setName("商品B");
        p2.setCategory("文房具");

        Product p3 = new Product();
        p3.setName("商品C");
        p3.setCategory("キッチン");

        Product p4 = new Product();
        p4.setName("商品D");
        p4.setCategory("インテリア");

        List<Product> products = Arrays.asList(p1, p2, p3, p4);
        when(productRepository.findAll()).thenReturn(products);

        List<String> result = categoryService.getAllUniqueCategoryNames();

        assertEquals(List.of("インテリア", "キッチン", "文房具"), result);
        verify(productRepository, times(1)).findAll();
    }

    // 境界値ケース：商品が1つもない場合
    @Test
    void getAllUniqueCategoryNames_WhenRepositoryReturnsEmptyList_ShouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<String> result = categoryService.getAllUniqueCategoryNames();

        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    // nullカテゴリを含む商品リストの場合
    @Test
    void getAllUniqueCategoryNames_WhenProductHasNullCategory_ShouldExcludeNull() {
        Product p1 = new Product();
        p1.setName("商品A");
        p1.setCategory("キッチン");

        Product p2 = new Product();
        p2.setName("商品B");
        p2.setCategory(null);  // nullカテゴリ

        Product p3 = new Product();
        p3.setName("商品C");
        p3.setCategory("文房具");

        List<Product> products = Arrays.asList(p1, p2, p3);
        when(productRepository.findAll()).thenReturn(products);

        List<String> result = categoryService.getAllUniqueCategoryNames();

        assertEquals(List.of("キッチン", "文房具"), result);
        assertFalse(result.contains(null));
        verify(productRepository, times(1)).findAll();
    }
}

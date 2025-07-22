package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections; // 空のリスト用
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple; // tupleを使った検証用
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private Product productWithNullFields; // nullフィールドを持つテストデータ

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setProductId(1);
        product1.setName("商品1");
        product1.setCategory("インテリア");
        product1.setPrice(100);
        product1.setCreatedAt(LocalDateTime.now());
        product1.setImageUrl("/img1.png");
        product1.setDescription("説明1");
        product1.setStock(10);
        // createdAt, updatedAt はエンティティ側で自動設定される想定

        product2 = new Product();
        product2.setProductId(2);
        product2.setName("商品2");
        product2.setCategory("インテリア");
        product2.setPrice(200);
        product2.setCreatedAt(LocalDateTime.now().minusDays(1));
        product2.setImageUrl("/img2.png");
        product2.setDescription("説明2");
        product2.setStock(5);

        productWithNullFields = new Product();
        productWithNullFields.setProductId(3);
        productWithNullFields.setName("商品3（Nullあり）");
        productWithNullFields.setCategory("インテリア");
        productWithNullFields.setPrice(300);
        productWithNullFields.setCreatedAt(LocalDateTime.now().minusDays(2));
        productWithNullFields.setStock(8);
        productWithNullFields.setDescription(null); // descriptionがnull
        productWithNullFields.setImageUrl(null);    // imageUrlがnull
    }

    // === getFilteredAndSortedProducts(All) のテスト ===

    @Test
    @DisplayName("getFilteredAndSortedProducts(All): リポジトリから複数の商品が返される場合、ProductListItemのリストを返す")
    void getFilteredAndSortedProducts_ShouldReturnListOfProductListItems() {
        // Arrange: モックの設定
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productsFromRepo);

        // Act: テスト対象メソッドの実行
        List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, null, "price_asc");

        // Assert: 結果の検証
        assertThat(result).hasSize(2);
        // 各要素の全フィールドが正しくマッピングされているか検証 (tupleを使うと便利)
        assertThat(result)
            .extracting(ProductListItem::getProductId, ProductListItem::getName, ProductListItem::getPrice, ProductListItem::getImageUrl)
            .containsExactlyInAnyOrder(
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getImageUrl()),
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getImageUrl())
            );

        // Verify: メソッド呼び出し検証
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository); // 他のメソッドが呼ばれていないこと
    }

    @Test
    @DisplayName("getFilteredAndSortedProducts(All): リポジトリから空のリストが返される場合、空のリストを返す")
    void getFilteredAndSortedProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, null, "price_asc");

        // Assert
        assertThat(result).isEmpty();

        // Verify
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("getFilteredAndSortedProducts(All): 商品エンティティにnullフィールドが含まれる場合、DTOにもnullがマッピングされる")
    void getFilteredAndSortedProducts_WhenProductHasNullFields_ShouldMapNullToDto() {
        // Arrange
        List<Product> productsFromRepo = new ArrayList<>(List.of(productWithNullFields));
        when(productRepository.findAll()).thenReturn(productsFromRepo);

        // Act
        List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, null, "price_asc");

        // Assert
        assertThat(result).hasSize(1);
        ProductListItem dto = result.get(0);
        assertThat(dto.getProductId()).isEqualTo(productWithNullFields.getProductId());
        assertThat(dto.getName()).isEqualTo(productWithNullFields.getName());
        assertThat(dto.getPrice()).isEqualTo(productWithNullFields.getPrice());
        assertThat(dto.getImageUrl()).isNull(); // imageUrlがnullであることを確認

        // Verify
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    // === findProductById のテスト ===

    @Test
    @DisplayName("findProductById: 存在するIDで検索した場合、ProductDetailを返す")
    void findProductById_WhenProductExists_ShouldReturnProductDetail() {
        // Arrange
        Integer productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        // Act
        ProductDetail result = productService.findProductById(productId);

        // Assert
        assertThat(result).isNotNull();
        // 全フィールドが正しくマッピングされているか検証
        assertThat(result.getProductId()).isEqualTo(product1.getProductId());
        assertThat(result.getName()).isEqualTo(product1.getName());
        assertThat(result.getPrice()).isEqualTo(product1.getPrice());
        assertThat(result.getDescription()).isEqualTo(product1.getDescription());
        assertThat(result.getStock()).isEqualTo(product1.getStock());
        assertThat(result.getImageUrl()).isEqualTo(product1.getImageUrl());

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("findProductById: 存在しないIDで検索した場合、nullを返す")
    void findProductById_WhenProductNotExists_ShouldReturnNull() {
        // Arrange
        Integer productId = 99;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        ProductDetail result = productService.findProductById(productId);

        // Assert
        assertThat(result).isNull();

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("findProductById: 商品エンティティにnullフィールドが含まれる場合、DTOにもnullがマッピングされる")
    void findProductById_WhenProductHasNullFields_ShouldMapNullToDto() {
        // Arrange
        Integer productId = 3;
        when(productRepository.findById(productId)).thenReturn(Optional.of(productWithNullFields));

        // Act
        ProductDetail result = productService.findProductById(productId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productWithNullFields.getProductId());
        assertThat(result.getName()).isEqualTo(productWithNullFields.getName());
        assertThat(result.getPrice()).isEqualTo(productWithNullFields.getPrice());
        assertThat(result.getStock()).isEqualTo(productWithNullFields.getStock());
        assertThat(result.getDescription()).isNull(); // descriptionがnullであることを確認
        assertThat(result.getImageUrl()).isNull();    // imageUrlがnullであることを確認

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("findProductById: 引数productIdがnullの場合、リポジトリのfindById(null)が呼ばれ、結果的に例外またはnullが返る")
    void findProductById_WhenProductIdIsNull_ShouldDelegateToRepositoryAndPotentiallyFail() {
        // Arrange
        Integer nullProductId = null;
        // findById(null) がどのように振る舞うかはリポジトリの実装やJPAプロバイダに依存する。
        // ここでは、MockitoがfindById(null)を受け付け、Optional.empty() を返すように定義してみる。
        // (実際には NullPointerException や IllegalArgumentException が発生する可能性もある)
        when(productRepository.findById(nullProductId)).thenReturn(Optional.empty());
        // もし例外を期待する場合は以下のように書く
        // when(productRepository.findById(nullProductId)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        // Act
        ProductDetail result = productService.findProductById(nullProductId);

        // Assert
        // Optional.empty() を返すように定義したので、結果は null になるはず
        assertThat(result).isNull();
        // 例外を期待する場合は以下のように書く
        // assertThatThrownBy(() -> productService.findProductById(nullProductId))
        //     .isInstanceOf(IllegalArgumentException.class)
        //     .hasMessage("ID cannot be null");

        // Verify
        verify(productRepository, times(1)).findById(nullProductId);
        verifyNoMoreInteractions(productRepository);
    }


// === getFilteredAndSortedProducts(keyword) のテスト ===

    @Test
    @DisplayName("getFilteredAndSortedProducts(keyword): リポジトリから条件に合った商品が返される場合、ProductListItemのリストを返す")
    void getFilteredAndSortedProducts_ShouldReturnListOfProductListItem() {
        // Arrange: モックの設定
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findByNameContainingIgnoreCase("商品")).thenReturn(productsFromRepo);

        // Act: テスト対象メソッドの実行
        List<ProductListItem> result = productService.getFilteredAndSortedProducts("商品", "", "price_asc");

        // Assert: 結果の検証
        assertThat(result).hasSize(2);
        // 各要素の全フィールドが正しくマッピングされているか検証 (tupleを使うと便利)
        assertThat(result)
            .extracting(ProductListItem::getProductId, ProductListItem::getName, ProductListItem::getPrice, ProductListItem::getImageUrl)
            .containsExactlyInAnyOrder(
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getImageUrl()),
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getImageUrl())
            );

        // Verify: メソッド呼び出し検証
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("商品");
        verifyNoMoreInteractions(productRepository); // 他のメソッドが呼ばれていないこと
    }

    @Test
    @DisplayName("getFilteredAndSortedProducts(keyword): リポジトリから空のリストが返される場合、空のリストを返す")
    void getFilteredAndSortedProducts_WhenRepositoryReturnsEmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(productRepository.findByNameContainingIgnoreCase("商品")).thenReturn(Collections.emptyList());

        // Act
        List<ProductListItem> result = productService.getFilteredAndSortedProducts("商品", null, "price_asc");

        // Assert
        assertThat(result).isEmpty();

        // Verify
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("商品");
        verifyNoMoreInteractions(productRepository);
    }

   @Test
@DisplayName("getFilteredAndSortedProducts(keyword): 商品エンティティにnullフィールドが含まれる場合、DTOにもnullがマッピングされる")
void getFilteredAndSortedProducts_WithKeywordAndNullFields_ShouldMapNullToDto() {
    // Arrange
    String keyword = "商品";
    List<Product> productsFromRepo = new ArrayList<>(List.of(productWithNullFields));
    when(productRepository.findByNameContainingIgnoreCase(keyword)).thenReturn(productsFromRepo);

    // Act
    List<ProductListItem> result = productService.getFilteredAndSortedProducts("商品", null, "price_asc");

    // Assert
    assertThat(result).hasSize(1);
    ProductListItem dto = result.get(0);
    assertThat(dto.getProductId()).isEqualTo(productWithNullFields.getProductId());
    assertThat(dto.getName()).isEqualTo(productWithNullFields.getName());
    assertThat(dto.getPrice()).isEqualTo(productWithNullFields.getPrice());
    assertThat(dto.getImageUrl()).isNull(); // imageUrlがnullであることを確認

    // Verify
    verify(productRepository, times(1)).findByNameContainingIgnoreCase(keyword);
    verifyNoMoreInteractions(productRepository);
}



// === getFilteredAndSortedProducts(category) のテスト ===

@Test
    @DisplayName("getFilteredAndSortedProducts(category): ProductRepository.findByCategory が複数の Product エンティティを含むリストを返すようモック設定")
    void getFilteredAndSortedProducts_ShouldReturnListOfProductListItems_WithCategory() {
        // Arrange
        String category = "インテリア";
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findByCategory(category.trim())).thenReturn(productsFromRepo);
 
        // Act
        List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, "インテリア", "name");
 
        // Assert
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(ProductListItem::getProductId, ProductListItem::getName, ProductListItem::getPrice, ProductListItem::getImageUrl)
            .containsExactlyInAnyOrder(
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getImageUrl()),
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getImageUrl())
            );
 
        // Verify
        verify(productRepository, times(1)).findByCategory(category.trim());
        verifyNoMoreInteractions(productRepository) ;
    }



@Test
    @DisplayName("getFilteredAndSortedProducts(category): リポジトリから空のリストが返される場合、空のリストを返す")
    void category_karanorisuto() {
        // Arrange
        String category = "インテリア";
        when(productRepository.findByCategory(category.trim())).thenReturn(Collections.emptyList());
 
        // Act
        List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, "インテリア", "name");
 
        // Assert
        assertThat(result).isEmpty();
 
        // Verify
        verify(productRepository, times(1)).findByCategory(category.trim());
        verifyNoMoreInteractions(productRepository);
    }


@Test
@DisplayName("getFilteredAndSortedProducts(category): 商品エンティティにnullフィールドが含まれる場合、DTOにもnullがマッピングされる")
void getFilteredAndSortedProducts_WithKeywordAndNullFields_ShouldMpNullToDto() {
    // Arrange
    String category = "インテリア";
    List<Product> productsFromRepo = new ArrayList<>(List.of(productWithNullFields));
    when(productRepository.findByCategory(category)).thenReturn(productsFromRepo);
    // Act
    List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, "インテリア", "name");

    // Assert
    assertThat(result).hasSize(1);
    ProductListItem dto = result.get(0);
    assertThat(dto.getProductId()).isEqualTo(productWithNullFields.getProductId());
    assertThat(dto.getName()).isEqualTo(productWithNullFields.getName());
    assertThat(dto.getPrice()).isEqualTo(productWithNullFields.getPrice());
    assertThat(dto.getImageUrl()).isNull(); // imageUrlがnullであることを確認

    // Verify
    verify(productRepository, times(1)).findByCategory(category);
    verifyNoMoreInteractions(productRepository);
}


//=== getFilteredAndSortedProducts(keyword & category) のテスト ===

@Test
    @DisplayName("getFilteredAndSortedProducts(keyword & category): リポジトリから複数の商品が返される場合、ProductListItemのリストを返す")
    void getFilteredAndSortedProduct(){
        // Arrange: モックの設定
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findByNameContainingIgnoreCaseAndCategory("商品", "インテリア")).thenReturn(productsFromRepo);
 
        // Act: テスト対象メソッドの実行
        List<ProductListItem> result = productService.getFilteredAndSortedProducts("商品", "インテリア", "price_asc");
 
        // Assert: 結果の検証
        assertThat(result).hasSize(2);
        // 各要素の全フィールドが正しくマッピングされているか検証 (tupleを使うと便利)
        assertThat(result)
            .extracting(item -> tuple(
                item.getProductId(),
                item.getName(),
                item.getPrice(),
                item.getImageUrl()
    ))
            .containsExactlyInAnyOrder(
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getImageUrl()),
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getImageUrl())
            );
 
        // Verify: メソッド呼び出し検証
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndCategory("商品","インテリア");
        verifyNoMoreInteractions(productRepository); // 他のメソッドが呼ばれていないこと
    }


@Test
    @DisplayName("getFilteredAndSortedProducts(keyword & category): リポジトリから空のリストが返される場合、空のリストを返す")
    void getFilteredAndSortedProducts_ShouldReturnListOfProducListItems(){
        // Arrange
        when(productRepository.findByNameContainingIgnoreCaseAndCategory("ABC", "ABC")).thenReturn(Collections.emptyList());
 
        // Act
        List<ProductListItem> result = productService.getFilteredAndSortedProducts("ABC", "ABC", "new");
 
        // Assert
        assertThat(result).isEmpty();
 
        // Verify
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndCategory("ABC", "ABC");
        verifyNoMoreInteractions(productRepository);
    }


@Test
@DisplayName("getFilteredAndSortedProducts(keyword & category): 商品エンティティにnullフィールドが含まれる場合、DTOにもnullがマッピングされる")
void getFilteredAndSortedProducts_WitKeywordAndNullFields_ShouldMpNullToDto() {
    // Arrange
    List<Product> productsFromRepo = new ArrayList<>(List.of(productWithNullFields));
    when(productRepository.findByNameContainingIgnoreCaseAndCategory("商品","インテリア")).thenReturn(productsFromRepo);
    // Act
    List<ProductListItem> result = productService.getFilteredAndSortedProducts("商品", "インテリア", "price_asc");

    // Assert
    assertThat(result).hasSize(1);
    ProductListItem dto = result.get(0);
    assertThat(dto.getProductId()).isEqualTo(productWithNullFields.getProductId());
    assertThat(dto.getName()).isEqualTo(productWithNullFields.getName());
    assertThat(dto.getPrice()).isEqualTo(productWithNullFields.getPrice());
    assertThat(dto.getImageUrl()).isNull(); // imageUrlがnullであることを確認

    // Verify
    verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndCategory("商品","インテリア");
    verifyNoMoreInteractions(productRepository);
}



//=== getFilteredAndSortedProducts(price_asc) のテスト ===


@Test
    @DisplayName("①getFilteredAndSortedProducts(sort price_asc): データ取得・DTO変換 ")
    void getFilteredAndSortedProducts_ShouldReturnListOfProductListItes() {
        // Arrange: モックの設定
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productsFromRepo);
 
        // Act: テスト対象メソッドの実行
        List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, null, "price_asc");
 
        // Assert: 結果の検証
        assertThat(result).hasSize(2);
        // 各要素の全フィールドが正しくマッピングされているか検証 (tupleを使うと便利)
        assertThat(result)
            .extracting(ProductListItem::getProductId, ProductListItem::getName, ProductListItem::getPrice, ProductListItem::getImageUrl)
            .containsExactly(
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getImageUrl()),
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getImageUrl())
            );
 
        // Verify: メソッド呼び出し検証
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository); // 他のメソッドが呼ばれていないこと
 
    }

 //=== getFilteredAndSortedProducts(price_desc) のテスト ===

    @Test
    @DisplayName("①getFilteredAndSortedProducts(sort price_desc): データ取得・DTO変換 ")
    void getFilteredAndSortedProducts_ShouldReturnListOfProductLstItes() {
        // Arrange: モックの設定
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productsFromRepo);
 
        // Act: テスト対象メソッドの実行
        List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, null, "price_desc");
 
        // Assert: 結果の検証
        assertThat(result).hasSize(2);
        // 各要素の全フィールドが正しくマッピングされているか検証 (tupleを使うと便利)
        assertThat(result)
            .extracting(ProductListItem::getProductId, ProductListItem::getName, ProductListItem::getPrice, ProductListItem::getImageUrl)
            .containsExactly(
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getImageUrl()),
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getImageUrl())
            );
 
        // Verify: メソッド呼び出し検証
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository); // 他のメソッドが呼ばれていないこと
 
    }


//=== getFilteredAndSortedProducts(name) のテスト ===

@Test
    @DisplayName("getFilteredAndSortedProducts(sort name): リポジトリから複数の商品が返される場合、ProductListItemのリストを返す")
    void getFilteredAndSortedProducts_ShouldReturnListOfProductListIte() {
        // Arrange: モックの設定
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productsFromRepo);
 
        // Act: テスト対象メソッドの実行
        List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, null, "name");
 
        // Assert: 結果の検証
        assertThat(result).hasSize(2);
        // 各要素の全フィールドが正しくマッピングされているか検証 (tupleを使うと便利)
        assertThat(result)
            .extracting(ProductListItem::getProductId, ProductListItem::getName, ProductListItem::getPrice, ProductListItem::getImageUrl)
            .containsExactly(
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getImageUrl()),
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getImageUrl())
            );
 
        // Verify: メソッド呼び出し検証
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository); // 他のメソッドが呼ばれていないこと
        }
    
   //=== getFilteredAndSortedProducts(new) のテスト === 

   @Test
    @DisplayName("getFilteredAndSortedProducts(sort new): 引数として（sort new）が与えられ、リポジトリから複数の商品が返され場合、作成順に並べ替えられたProductListItemのリストを返す")
    void getFilteredAndSortedProducts_ShouldReturnListOfProuctListItems() {
        // Arrange: モックの設定
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productsFromRepo);
 
        // Act: テスト対象メソッドの実行
        List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, null, "createdAt");
 
        // Assert: 結果の検証
        assertThat(result).hasSize(2);
        // 各要素の全フィールドが正しくマッピングされているか検証 (tupleを使うと便利)
        assertThat(result)
            .extracting(ProductListItem::getProductId, ProductListItem::getName, ProductListItem::getPrice, ProductListItem::getCreatedAt, ProductListItem::getImageUrl)
            .containsExactly(
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getCreatedAt(), product1.getImageUrl()),
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getCreatedAt(), product2.getImageUrl())
            );
 
        // Verify: メソッド呼び出し検証
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository); // 他のメソッドが呼ばれていないこと
    }



 //=== 複合検索　getFilteredAndSortedProducts(sort price_asc & keyword) のテスト === 


@Test
    @DisplayName("getFilteredAndSortedProducts(sort price_asc & keyword): リポジトリから条件に合った商品が返される場合、ProductListItemのリストを返す")
    void getFilteredAndSortedProducts_ShouldReurnListOfProductListItem() {
        // Arrange: モックの設定
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findByNameContainingIgnoreCase("商品")).thenReturn(productsFromRepo);

        // Act: テスト対象メソッドの実行
        List<ProductListItem> result = productService.getFilteredAndSortedProducts("商品", null, "price_asc");

        // Assert: 結果の検証
        assertThat(result).hasSize(2);
        // 各要素の全フィールドが正しくマッピングされているか検証 (tupleを使うと便利)
        assertThat(result)
            .extracting(ProductListItem::getProductId, ProductListItem::getName, ProductListItem::getPrice, ProductListItem::getImageUrl)
            .containsExactly(
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getImageUrl()),
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getImageUrl())
            );

        // Verify: メソッド呼び出し検証
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("商品");
        verifyNoMoreInteractions(productRepository); // 他のメソッドが呼ばれていないこと
    }



//=== 複合検索　getFilteredAndSortedProducts(sort name & category) のテスト === 

@Test
    @DisplayName("getFilteredAndSortedProducts(sort name & category): リポジトリから条件に合った商品が返される場合、ProductListItemのリストを返す")
    void getFilteredAndSortedProducts_ShouldReurnListOfProductListItems_WithCategory() {
        // Arrange
        String category = "インテリア";
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findByCategory(category.trim())).thenReturn(productsFromRepo);
 
        // Act
        List<ProductListItem> result = productService.getFilteredAndSortedProducts(null, "インテリア", "name");
 
        // Assert
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(ProductListItem::getProductId, ProductListItem::getName, ProductListItem::getPrice, ProductListItem::getImageUrl)
            .containsExactly(
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getImageUrl()),
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getImageUrl())
            );
 
        // Verify
        verify(productRepository, times(1)).findByCategory(category.trim());
        verifyNoMoreInteractions(productRepository) ;
    }
}
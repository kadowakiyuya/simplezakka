document.addEventListener('DOMContentLoaded', function() {
    // モーダル要素の取得
    const productModal = new bootstrap.Modal(document.getElementById('productModal'));
    const cartModal = new bootstrap.Modal(document.getElementById('cartModal'));
    const checkoutModal = new bootstrap.Modal(document.getElementById('checkoutModal'));
    const orderCompleteModal = new bootstrap.Modal(document.getElementById('orderCompleteModal'));
    
    // APIのベースURL
    const API_BASE = '/api';

    // 検索フォームの要素を取得
    const searchInput = document.getElementById('searchInput');
    const searchButton = document.getElementById('searchButton');
    const categorySelect = document.getElementById('categorySelect'); 
    const sortSelect = document.getElementById('sortExe');

    // --- ヘルパー関数群 ---

    // フィルターの現在の値を取得する関数
    function getCurrentFilters() {
        const currentKeyword = searchInput ? searchInput.value.trim() : '';
        const currentCategory = categorySelect ? categorySelect.value : '';
        const currentSort = sortSelect ? sortSelect.value : 'new'; 
        return { keyword: currentKeyword, category: currentCategory, sort: currentSort };
    }

    // 商品一覧を表示する関数
    function displayProducts(products) {
        const productsContainer = document.getElementById('products-container');
        if (!productsContainer) {
            console.error("商品を表示するコンテナ要素 (ID: products-container) が見つかりませんでした。");
            return;
        }
        productsContainer.innerHTML = ''; // 既存の内容をクリア

        if (products.length === 0) {
            productsContainer.innerHTML = '<p class="text-center w-100">該当する商品が見つかりませんでした。</p>';
            return;
        }

        products.forEach(product => {
    const productCard = `
        <div class="col-md-4 mb-4">
            <div class="card h-100 product-card d-flex flex-column" data-id="${product.productId}">
                <img src="${product.imageUrl || 'https://via.placeholder.com/300x200'}" class="card-img-top" alt="${product.name}">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">${product.name}</h5>
                    <p class="card-text">¥${product.price.toLocaleString()}</p>
                    
                    <div class="mt-auto">
                        <button class="btn btn-gold view-detail" data-id="${product.productId}">詳細を見る</button>
                    </div>
                </div>
             </div>
        </div>
    `;
    productsContainer.insertAdjacentHTML('beforeend', productCard);
});

productsContainer.querySelectorAll('.view-detail').forEach(button => {
    button.addEventListener('click', function(event) {
        event.stopPropagation(); // カードクリックのイベントをキャンセル
        fetchProductDetail(this.dataset.id);
    });
});

    }

    // カテゴリをプルダウンに挿入する関数
    async function populateCategories() {
        if (!categorySelect) {
            console.warn("カテゴリ選択要素 (ID: categorySelect) が見つかりませんでした。");
            return;
        }

        try {
            const response = await fetch(`${API_BASE}/categories`);
            if (!response.ok) {
                throw new Error(`カテゴリリストの取得に失敗しました: ${response.status} ${response.statusText}`);
            }
            
            const categories = await response.json();

            categorySelect.innerHTML = '<option value="">カテゴリを選択</option>';

            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category;      
                option.textContent = category;  
                categorySelect.appendChild(option);
            });

            // URLパラメータに基づいて初期選択を行う
            const initialCategory = new URLSearchParams(window.location.search).get('category') || '';
            if (initialCategory) {
                const optionExists = Array.from(categorySelect.options).some(
                    opt => opt.value === initialCategory
                );
                if (optionExists) {
                    categorySelect.value = initialCategory;
                } else {
                    console.warn(`URLパラメータのカテゴリ "${initialCategory}" は有効なオプションではありません。`);
                }
            }

        } catch (error) {
            console.error('カテゴリの読み込みエラー:', error);
        }
    }

    // 商品一覧を取得して表示する関数 (searchKeyword, selectedCategory, sortOrder を受け取るように修正)
    async function fetchProducts(searchKeyword = '', selectedCategory = '', sortOrder = 'new') { 
        try {
            let url = `${API_BASE}/products`;
            const params = new URLSearchParams();

            if (searchKeyword.trim() !== '') {
                params.append('keyword', searchKeyword.trim());
            }
            if (selectedCategory.trim() !== '') {
                params.append('category', selectedCategory.trim());
            }
            if (sortOrder && sortOrder !== 'new') { // 'new'はデフォルトなので除外
                params.append('sort', sortOrder);
            }

            if (params.toString()) {
                url += `?${params.toString()}`;
            }
            
            const response = await fetch(url);
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                throw new Error(`商品の取得に失敗しました: ${errorData.message || response.statusText}`);
            }
            const products = await response.json();
            displayProducts(products); // displayProducts はこの時点で定義済み

            // 検索結果がない場合の表示はdisplayProductsで処理されるため、重複を避ける
            // if (products.length === 0) { ... } この部分はdisplayProducts関数内に移動済み
        } catch (error) {
            console.error('Error in fetchProducts:', error);
            alert(`商品の読み込みに失敗しました: ${error.message}`);
        }
    }
    
    // 商品詳細を取得する関数
    async function fetchProductDetail(productId) {
        try {
            const response = await fetch(`${API_BASE}/products/${productId}`);
            if (!response.ok) {
                throw new Error('商品詳細の取得に失敗しました');
            }
            const product = await response.json();
            displayProductDetail(product);
        } catch (error) {
            console.error('Error:', error);
            alert('商品詳細の読み込みに失敗しました');
        }
    }
    
    // 商品詳細を表示する関数
    function displayProductDetail(product) {
        document.getElementById('productModalTitle').textContent = product.name;
        
        const modalBody = document.getElementById('productModalBody');
        modalBody.innerHTML = `
            <div class="row">
                <div class="col-md-6">
                    <img src="${product.imageUrl || 'https://via.placeholder.com/400x300'}" class="img-fluid" alt="${product.name}">
                </div>
                <div class="col-md-6">
                    <p class="fs-4">¥${product.price.toLocaleString()}</p>
                    <p>${product.description}</p>
                    <p>素材: ${product.material}</p>
                    <p>カテゴリ: ${product.category}</p>
                    <p>在庫: ${product.stock} 個</p>
                    <div class="d-flex align-items-center mb-3">
                        <label for="quantity" class="me-2">数量:</label>
                        <input type="number" id="quantity" class="form-control w-25" value="1" min="1" max="${product.stock}">
                    </div>
                    <button class="btn btn-gold add-to-cart" data-id="${product.productId}">カートに入れる</button>
                </div>
            </div>
        `;
        
        // カートに追加ボタンのイベント設定
        modalBody.querySelector('.add-to-cart').addEventListener('click', function() {
            const quantity = parseInt(document.getElementById('quantity').value);
            addToCart(product.productId, quantity);
        });
        
        productModal.show();
    }
    
    // カートに商品を追加する関数
    async function addToCart(productId, quantity) {
        try {
            const response = await fetch(`${API_BASE}/cart`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    productId: productId,
                    quantity: quantity
                })
            });
            
            if (!response.ok) {
                throw new Error('カートへの追加に失敗しました');
            }
            
            const cart = await response.json();
            updateCartBadge(cart.totalQuantity);
            
            productModal.hide();
            alert('商品をカートに追加しました');
        } catch (error) {
            console.error('Error:', error);
            alert('カートへの追加に失敗しました');
        }
    }
    
    // カート情報を取得する関数
    async function updateCartDisplay() {
        try {
            const response = await fetch(`${API_BASE}/cart`);
            if (!response.ok) {
                throw new Error('カート情報の取得に失敗しました');
            }
            const cart = await response.json();
            updateCartBadge(cart.totalQuantity);
        } catch (error) {
            console.error('Error:', error);
        }
    }
    
    // カートバッジを更新する関数
    function updateCartBadge(count) {
        document.getElementById('cart-count').textContent = count;
    }
    
    // カートモーダルの内容を更新する関数
    async function updateCartModalContent() {
        try {
            const response = await fetch(`${API_BASE}/cart`);
            if (!response.ok) {
                throw new Error('カート情報の取得に失敗しました');
            }
            const cart = await response.json();
            displayCart(cart);
        } catch (error) {
            console.error('Error:', error);
            alert('カート情報の読み込みに失敗しました');
        }
    }
    
    // カート内容を表示する関数
    function displayCart(cart) {
        const modalBody = document.getElementById('cartModalBody');
        
        if (cart.items && Object.keys(cart.items).length > 0) {
            let html = `
                <table class="table">
                    <thead>
                        <tr>
                            <th>商品</th>
                            <th>単価</th>
                            <th>数量</th>
                            <th>小計</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
            `;
            
            Object.values(cart.items).forEach(item => {
                html += `
                    <tr>
                        <td>${item.name}</td>
                        <td>¥${item.price.toLocaleString()}</td>
                        <td>
                            <input type="number" class="form-control form-control-sm update-quantity" 
                                       data-id="${item.id}" value="${item.quantity}" min="1" style="width: 70px">
                        </td>
                        <td>¥${item.subtotal.toLocaleString()}</td>
                        <td>
                            <button class="btn btn-sm btn-danger remove-item" data-id="${item.id}">削除</button>
                        </td>
                    </tr>
                `;
            });
            
            html += `
                    </tbody>
                    <tfoot>
                        <tr>
                            <th colspan="3" class="text-end">合計:</th>
                            <th>¥${cart.totalPrice.toLocaleString()}</th>
                            <th></th>
                        </tr>
                    </tfoot>
                </table>
            `;
            
            modalBody.innerHTML = html;
            
            // 数量更新イベントの設定
            document.querySelectorAll('.update-quantity').forEach(input => {
                input.addEventListener('change', function() {
                    updateItemQuantity(this.dataset.id, this.value);
                });
            });
            
            // 削除ボタンイベントの設定
            document.querySelectorAll('.remove-item').forEach(button => {
                button.addEventListener('click', function() {
                    removeItem(this.dataset.id);
                });
            });
            
            // 注文ボタンの有効化
            document.getElementById('checkout-btn').disabled = false;
        } else {
            modalBody.innerHTML = '<p class="text-center">カートは空です</p>';
            document.getElementById('checkout-btn').disabled = true;
        }
    }
    
    // カート内の商品数量を更新する関数
    async function updateItemQuantity(itemId, quantity) {
        try {
            const response = await fetch(`${API_BASE}/cart/items/${itemId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    quantity: parseInt(quantity)
                })
            });
            
            if (!response.ok) {
                throw new Error('数量の更新に失敗しました');
            }
            
            const cart = await response.json();
            displayCart(cart);
            updateCartBadge(cart.totalQuantity);
        } catch (error) {
            console.error('Error:', error);
            alert('数量の更新に失敗しました');
            updateCartModalContent(); // 失敗時は元の状態に戻す
        }
    }
    
    // カート内の商品を削除する関数
    async function removeItem(itemId) {
        try {
            const response = await fetch(`${API_BASE}/cart/items/${itemId}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('商品の削除に失敗しました');
            }
            
            const cart = await response.json();
            displayCart(cart);
            updateCartBadge(cart.totalQuantity);
        } catch (error) {
            console.error('Error:', error);
            alert('商品の削除に失敗しました');
        }
    }
    
    // 注文を確定する関数
    async function submitOrder() {
        const form = document.getElementById('order-form');
        
        // フォームバリデーション
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }
        
        const orderData = {
            customerInfo: {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                address: document.getElementById('address').value,
                phoneNumber: document.getElementById('phone').value
            }
        };
        
        try {
            const response = await fetch(`${API_BASE}/orders`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(orderData)
            });
            
            if (!response.ok) {
                throw new Error('注文の確定に失敗しました');
            }
            
            const order = await response.json();
            displayOrderComplete(order);
            
            checkoutModal.hide();
            orderCompleteModal.show();
            
            // カート表示をリセット
            updateCartBadge(0);
            
            // フォームリセット
            form.reset();
            form.classList.remove('was-validated');
        } catch (error) {
            console.error('Error:', error);
            alert('注文の確定に失敗しました');
        }
    }
    
    // 注文完了画面を表示する関数
    function displayOrderComplete(order) {
        document.getElementById('orderCompleteBody').innerHTML = `
            <p>ご注文ありがとうございます。注文番号は <strong>${order.orderId}</strong> です。</p>
            <p>ご注文日時: ${new Date(order.orderDate).toLocaleString()}</p>
           
        `;
    }

    // --- イベントリスナーの初期化 ---

    // 検索ボタンクリック時のイベントリスナー
    if (searchButton) {
        searchButton.addEventListener('click', function() {
            const filters = getCurrentFilters();
            fetchProducts(filters.keyword, filters.category, filters.sort);
        });
    }

    // 検索入力欄でEnterキーを押した時のイベントリスナー
    if (searchInput) {
        searchInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault(); 
                const filters = getCurrentFilters();
                fetchProducts(filters.keyword, filters.category, filters.sort);
            }
        });
    }

    // カテゴリセレクトボックスの値が変わった時のイベントリスナー 
    if (categorySelect) {
        categorySelect.addEventListener('change', function() {
            const filters = getCurrentFilters();
            fetchProducts(filters.keyword, filters.category, filters.sort);
        });
    }

    // ソートセレクトボックスの値が変わった時のイベントリスナー
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            const filters = getCurrentFilters();
            fetchProducts(filters.keyword, filters.category, filters.sort);
        });
    }
    
    // カートボタンクリックイベント
    document.getElementById('cart-btn').addEventListener('click', function() {
        updateCartModalContent();
        cartModal.show();
    });
    
    // 注文手続きボタンクリックイベント
    document.getElementById('checkout-btn').addEventListener('click', function() {
        cartModal.hide();
        checkoutModal.show();
    });
    
    // 注文確定ボタンクリックイベント
    document.getElementById('confirm-order-btn').addEventListener('click', function() {
        submitOrder();
    });
    
    // --- 初期ロード処理 ---

    // カテゴリをロードし、その後初期の商品一覧もロードする
    populateCategories().then(() => {
        const initialFilters = getCurrentFilters();
        fetchProducts(initialFilters.keyword, initialFilters.category, initialFilters.sort);
    });
    
    // カート情報の初期表示
    updateCartDisplay();
});
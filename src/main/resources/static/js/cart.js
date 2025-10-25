console.log('🛒 Cart module loaded');

// Глобальные переменные
let currentCart = null;

// Вспомогательные функции
function isValidPhone(phone) {
    if (!phone) return false;
    
    const cleaned = phone.replace(/[\s\-\(\)]/g, '');
    const digitsOnly = cleaned.replace(/\D/g, '');
    
    if (digitsOnly.length === 11) {
        if (digitsOnly.startsWith('7') || digitsOnly.startsWith('8')) {
            const secondDigit = digitsOnly.charAt(1);
            return ['9', '4', '8'].includes(secondDigit);
        }
    }
    
    if (cleaned.startsWith('+')) {
        const internationalDigits = cleaned.substring(1).replace(/\D/g, '');
        return internationalDigits.length >= 10 && internationalDigits.length <= 15;
    }
    
    return false;
}

function formatPrice(price) {
    if (typeof price !== 'number') {
        price = parseFloat(price) || 0;
    }
    return price.toFixed(2);
}

function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// Основные функции корзины
async function createCart() {
    const phoneInput = document.getElementById('userPhone');
    if (!phoneInput) {
        console.error('Phone input not found');
        return;
    }
    
    const phone = phoneInput.value.trim();
    console.log('Creating cart for phone:', phone);
    
    if (!isValidPhone(phone)) {
        NotificationManager.show('Введите корректный номер телефона (+79991234567)', 'error');
        phoneInput.focus();
        return;
    }

    try {
        console.log('Sending cart creation request...');
        
        const cart = await ApiClient.post(`/carts/add/${encodeURIComponent(phone)}`);
        console.log('Cart created successfully:', cart);
        
        currentCart = cart;
        
        // После создания корзины загружаем ее содержимое
        await loadCartByPhone(phone);
        NotificationManager.show('Корзина создана успешно!', 'success');
        
    } catch (error) {
        console.error('Error creating cart:', error);
        
        let errorMessage = 'Ошибка создания корзины';
        if (error.message.includes('Failed to fetch')) {
            errorMessage = 'Не удалось подключиться к серверу. Проверьте, запущен ли бэкенд.';
        } else if (error.message.includes('404')) {
            errorMessage = 'Сервер не найден. Проверьте URL API.';
        } else if (error.message.includes('500')) {
            errorMessage = 'Ошибка сервера. Проверьте логи бэкенда.';
        } else if (error.message.includes('409')) {
            errorMessage = 'Корзина для этого номера уже существует.';
            // Если корзина уже существует, загружаем ее
            await loadCartByPhone(phone);
        }
        
        NotificationManager.show(errorMessage, 'error');
    }
}

async function loadCartByPhone(phone) {
    console.log('Loading cart for phone:', phone);
    
    if (!isValidPhone(phone)) {
        NotificationManager.show('Введите корректный номер телефона', 'error');
        return;
    }

    const container = document.getElementById('cartItems');
    if (!container) {
        console.error('Cart items container not found');
        return;
    }
    
    container.innerHTML = '<div class="loading">Загрузка корзины...</div>';

    try {
        // Реальный запрос к API для получения корзины с товарами
        const cart = await ApiClient.get(`/carts/${encodeURIComponent(phone)}`);
        console.log('Cart loaded successfully:', cart);
        
        currentCart = cart;
        displayCartInfo(cart);
        displayCartItems(cart.items);
        
        NotificationManager.show('Корзина загружена', 'success');
        
    } catch (error) {
        console.error('Error loading cart:', error);
        
        if (error.message.includes('404') || error.message.includes('Корзина не найдена')) {
            container.innerHTML = `
                <div class="empty-cart">
                    <i class="fas fa-shopping-cart"></i>
                    <h3>Корзина не найдена</h3>
                    <p>Создайте новую корзину</p>
                    <button onclick="createCart()" class="btn btn-primary">Создать корзину</button>
                </div>
            `;
            // Скрываем информацию о корзине если она не найдена
            document.getElementById('cartInfo').classList.add('hidden');
        } else {
            container.innerHTML = '<div class="error">Ошибка загрузки корзины</div>';
            NotificationManager.show('Ошибка загрузки корзины', 'error');
        }
    }
}

function displayCartInfo(cart) {
    console.log('Displaying cart info:', cart);
    
    const cartInfo = document.getElementById('cartInfo');
    const cartIdElement = document.getElementById('cartId');
    const cartTotalElement = document.getElementById('cartTotal');
    
    if (!cartInfo || !cartIdElement || !cartTotalElement) {
        console.error('Cart HTML elements not found');
        return;
    }
    
    cartInfo.classList.remove('hidden');
    cartIdElement.textContent = cart.cartId || cart.id;
    cartTotalElement.textContent = formatPrice(cart.totalAmount);
    
    console.log('Cart info displayed successfully');
}

function displayCartItems(items) {
    const container = document.getElementById('cartItems');
    
    if (!items || items.length === 0) {
        container.innerHTML = `
            <div class="empty-cart">
                <i class="fas fa-shopping-cart"></i>
                <h3>Корзина пуста</h3>
                <p>Добавьте блюда из меню</p>
            </div>
        `;
        return;
    }

    container.innerHTML = items.map(item => `
        <div class="cart-item" data-item-id="${item.cartItemId}">
            <div class="cart-item-info">
                <div class="cart-item-title">${escapeHtml(item.dishTitle)}</div>
                <div class="cart-item-meta">
                    <span class="price">${formatPrice(item.unitPrice)} ₽ × ${item.count}</span>
                    <span class="total">Итого: ${formatPrice(item.totalPrice)} ₽</span>
                </div>
            </div>
            <div class="cart-item-actions">
                <div class="quantity-controls">
                    <button class="quantity-btn" onclick="updateCartItemQuantity(${item.cartItemId}, ${item.count - 1})">
                        <i class="fas fa-minus"></i>
                    </button>
                    <span class="quantity">${item.count}</span>
                    <button class="quantity-btn" onclick="updateCartItemQuantity(${item.cartItemId}, ${item.count + 1})">
                        <i class="fas fa-plus"></i>
                    </button>
                </div>
                <button class="btn btn-danger btn-remove" onclick="removeCartItem(${item.cartItemId})">
                    <i class="fas fa-trash"></i> Удалить
                </button>
            </div>
        </div>
    `).join('');
    
    console.log(`Displayed ${items.length} cart items`);
}

async function updateCartItemQuantity(cartItemId, newQuantity) {
    console.log(`Updating cart item ${cartItemId} quantity to ${newQuantity}`);
    
    if (newQuantity < 1) {
        await removeCartItem(cartItemId);
        return;
    }

    const phoneInput = document.getElementById('userPhone');
    if (!phoneInput) {
        NotificationManager.show('Введите номер телефона', 'error');
        return;
    }

    const phone = phoneInput.value.trim();
    if (!isValidPhone(phone)) {
        NotificationManager.show('Введите корректный номер телефона', 'error');
        return;
    }

    try {
        const updateData = {
            phone: phone,
            cartItemId: cartItemId,
            count: newQuantity
        };

        console.log('Sending update request:', updateData);
        
        const response = await ApiClient.patch('/carts/items', updateData);
        
        NotificationManager.show('Количество обновлено', 'success');
        
        // Обновляем отображение корзины
        await loadCartByPhone(phone);
        
    } catch (error) {
        console.error('Error updating quantity:', error);
        NotificationManager.show(`Ошибка обновления: ${error.message}`, 'error');
    }
}

async function removeCartItem(cartItemId) {
    console.log(`Removing cart item: ${cartItemId}`);
    
    if (!confirm('Удалить этот элемент из корзины?')) {
        return;
    }

    const phoneInput = document.getElementById('userPhone');
    if (!phoneInput) {
        NotificationManager.show('Введите номер телефона', 'error');
        return;
    }

    const phone = phoneInput.value.trim();
    if (!isValidPhone(phone)) {
        NotificationManager.show('Введите корректный номер телефона', 'error');
        return;
    }

    try {
        const deleteData = {
            phone: phone,
            cartItemId: cartItemId
        };

        console.log('Sending delete request:', deleteData);
        
        await ApiClient.delete('/carts/drop/items', deleteData);
        
        NotificationManager.show('Элемент удален из корзины', 'success');
        
        // Обновляем корзину
        await loadCartByPhone(phone);
        
    } catch (error) {
        console.error('Error removing cart item:', error);
        NotificationManager.show(`Ошибка удаления: ${error.message}`, 'error');
    }
}

async function clearCart() {
    console.log('Clearing cart');
    
    if (!currentCart) {
        NotificationManager.show('Корзина не найдена', 'error');
        return;
    }

    if (!confirm('Очистить всю корзину? Это действие нельзя отменить.')) {
        return;
    }

    const phoneInput = document.getElementById('userPhone');
    if (!phoneInput) {
        NotificationManager.show('Введите номер телефона', 'error');
        return;
    }

    const phone = phoneInput.value.trim();
    if (!isValidPhone(phone)) {
        NotificationManager.show('Введите корректный номер телефона', 'error');
        return;
    }

    try {
        await ApiClient.delete(`/carts/drop/${encodeURIComponent(phone)}`);
        
        currentCart = null;
        document.getElementById('cartInfo').classList.add('hidden');
        document.getElementById('cartItems').innerHTML = `
            <div class="empty-cart">
                <i class="fas fa-shopping-cart"></i>
                <h3>Корзина очищена</h3>
                <p>Добавьте блюда из меню</p>
            </div>
        `;
        
        NotificationManager.show('Корзина очищена', 'success');
        
    } catch (error) {
        console.error('Error clearing cart:', error);
        NotificationManager.show(`Ошибка очистки корзины: ${error.message}`, 'error');
    }
}

async function createOrder() {
    console.log('Creating order from cart');
    
    if (!currentCart) {
        NotificationManager.show('Сначала создайте корзину', 'error');
        return;
    }

    const phoneInput = document.getElementById('userPhone');
    if (!phoneInput) {
        NotificationManager.show('Введите номер телефона', 'error');
        return;
    }

    const phone = phoneInput.value.trim();
    if (!isValidPhone(phone)) {
        NotificationManager.show('Введите корректный номер телефона', 'error');
        return;
    }

    try {
        console.log('Creating order for phone:', phone);
        
        const order = await ApiClient.post(`/orders/add/${encodeURIComponent(phone)}`);
        
        NotificationManager.show('Заказ успешно создан!', 'success');
        
        // Очистить корзину после создания заказа
        currentCart = null;
        document.getElementById('cartInfo').classList.add('hidden');
        document.getElementById('cartItems').innerHTML = `
            <div class="empty-cart">
                <i class="fas fa-check-circle"></i>
                <h3>Заказ оформлен!</h3>
                <p>Номер заказа: #${order.id}</p>
            </div>
        `;
        
        // Перейти к заказам
        tabManager.switchTab('orders');
        
        // Загрузить заказы
        if (typeof loadOrders === 'function') {
            setTimeout(loadOrders, 500);
        }
        
    } catch (error) {
        console.error('Error creating order:', error);
        NotificationManager.show(`Ошибка создания заказа: ${error.message}`, 'error');
    }
}

// Функция для автоматической загрузки корзины при вводе телефона
function initializeCartAutoLoad() {
    const phoneInput = document.getElementById('userPhone');
    if (phoneInput) {
        // Загружаем корзину при изменении телефона (с debounce)
        let debounceTimer;
        phoneInput.addEventListener('input', function(e) {
            clearTimeout(debounceTimer);
            const phone = e.target.value.trim();
            
            if (isValidPhone(phone)) {
                debounceTimer = setTimeout(() => {
                    loadCartByPhone(phone);
                }, 1000);
            }
        });
        
        // Также загружаем корзину при переходе на вкладку корзины
        const cartTabBtn = document.querySelector('[data-tab="cart"]');
        if (cartTabBtn) {
            cartTabBtn.addEventListener('click', function() {
                const phone = phoneInput.value.trim();
                if (isValidPhone(phone)) {
                    loadCartByPhone(phone);
                }
            });
        }
    }
}

// Инициализация обработчиков событий
function initializeCartEventHandlers() {
    console.log('Initializing cart event handlers...');
    
    // Создание корзины
    const createCartBtn = document.getElementById('createCartBtn');
    if (createCartBtn) {
        createCartBtn.addEventListener('click', createCart);
        console.log('✅ Create cart button handler added');
    } else {
        console.error('❌ Create cart button not found');
    }
    
    // Загрузка корзины
    const loadCartBtn = document.getElementById('loadCartBtn');
    if (loadCartBtn) {
        loadCartBtn.addEventListener('click', function() {
            const phone = document.getElementById('userPhone').value.trim();
            if (isValidPhone(phone)) {
                loadCartByPhone(phone);
            } else {
                NotificationManager.show('Введите корректный номер телефона', 'error');
            }
        });
        console.log('✅ Load cart button handler added');
    }
    
    // Оформление заказа
    const createOrderBtn = document.getElementById('createOrderBtn');
    if (createOrderBtn) {
        createOrderBtn.addEventListener('click', createOrder);
        console.log('✅ Create order button handler added');
    }
    
    // Очистка корзины
    const clearCartBtn = document.getElementById('clearCartBtn');
    if (clearCartBtn) {
        clearCartBtn.addEventListener('click', clearCart);
        console.log('✅ Clear cart button handler added');
    }
    
    // Добавление в корзину из модального окна
    const addToCartBtn = document.getElementById('addToCartBtn');
    if (addToCartBtn) {
        addToCartBtn.addEventListener('click', function() {
            if (typeof addToCart === 'function') {
                addToCart();
            }
        });
        console.log('✅ Add to cart button handler added');
    }
    
    // Инициализация авто-загрузки
    initializeCartAutoLoad();
}

// Отладочные функции
function debugPageElements() {
    console.log('=== ДЕБАГ ЭЛЕМЕНТОВ СТРАНИЦЫ ===');

    const elements = {
        'userPhone': document.getElementById('userPhone'),
        'createCartBtn': document.getElementById('createCartBtn'),
        'loadCartBtn': document.getElementById('loadCartBtn'),
        'cartInfo': document.getElementById('cartInfo'),
        'createOrderBtn': document.getElementById('createOrderBtn'),
        'clearCartBtn': document.getElementById('clearCartBtn'),
        'addToCartBtn': document.getElementById('addToCartBtn')
    };

    for (const [name, element] of Object.entries(elements)) {
        if (element) {
            console.log(`✅ ${name}: НАЙДЕН`, element);
        } else {
            console.log(`❌ ${name}: НЕ НАЙДЕН!`);
        }
    }
}

// Добавляем тестовую кнопку на страницу
function addTestButton() {
    if (document.getElementById('testCartBtn')) return;
    
    const testBtn = document.createElement('button');
    testBtn.id = 'testCartBtn';
    testBtn.textContent = '🔧 ТЕСТ КОРЗИНЫ';
    testBtn.style.cssText = `
        position: fixed;
        top: 10px;
        right: 10px;
        background: red;
        color: white;
        padding: 10px;
        border: none;
        border-radius: 5px;
        z-index: 10000;
        cursor: pointer;
        font-size: 12px;
    `;
    testBtn.onclick = function() {
        console.log('🔧 ТЕСТОВАЯ КНОПКА НАЖАТА');
        const phone = document.getElementById('userPhone').value || '+79991234567';
        document.getElementById('userPhone').value = phone;
        loadCartByPhone(phone);
    };
    document.body.appendChild(testBtn);
    console.log('✅ Тестовая кнопка добавлена');
}

// Инициализация с детальной отладкой
function initializeCartWithDebug() {
    console.log('=== ИНИЦИАЛИЗАЦИЯ CART С ОТЛАДКОЙ ===');

    debugPageElements();
    initializeCartEventHandlers();
    addTestButton();
}

// Запускаем инициализацию когда DOM готов
document.addEventListener('DOMContentLoaded', function() {
    console.log('📄 DOM ЗАГРУЖЕН, инициализируем cart...');
    setTimeout(initializeCartWithDebug, 100);
});

// Также запускаем если DOM уже загружен
if (document.readyState === 'complete' || document.readyState === 'interactive') {
    setTimeout(initializeCartWithDebug, 100);
}

// Глобальные функции
window.createCart = createCart;
window.loadCartByPhone = loadCartByPhone;
window.updateCartItemQuantity = updateCartItemQuantity;
window.removeCartItem = removeCartItem;
window.clearCart = clearCart;
window.createOrder = createOrder;

// Для обратной совместимости
window.loadCart = loadCartByPhone;

console.log('✅ Cart module fully initialized');
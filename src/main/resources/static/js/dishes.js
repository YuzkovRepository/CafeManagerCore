console.log('🍽️ Dishes module loaded');

async function loadDishes() {
    console.log('Loading dishes...');
    
    const container = document.getElementById('dishesList');
    if (!container) {
        console.error('Dishes container not found');
        return;
    }
    
    container.innerHTML = '<div class="loading">Загрузка меню...</div>';
    
    try {
        const dishes = await ApiClient.get('/dishes');
        console.log(`Loaded ${dishes.length} dishes`);
        
        allDishes = dishes;
        displayDishes(dishes);
        updateCategoryFilter(dishes);
        
    } catch (error) {
        console.error('Error loading dishes:', error);
        container.innerHTML = `
            <div class="error">
                <h3>Ошибка загрузки меню</h3>
                <p>${error.message}</p>
                <button onclick="loadDishes()" class="btn btn-primary">Попробовать снова</button>
            </div>
        `;
        NotificationManager.show('Ошибка загрузки меню', 'error');
    }
}

function displayDishes(dishes) {
    const container = document.getElementById('dishesList');
    
    if (!dishes || dishes.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>Блюда не найдены</h3>
                <p>Попробуйте изменить параметры поиска</p>
            </div>
        `;
        return;
    }

    container.innerHTML = dishes.map(dish => `
        <div class="dish-card" onclick="showAddToCartModal(${dish.id})">
            <div class="dish-image-container">
                ${dish.imageBase64 ? 
                    `<img src="data:image/jpeg;base64,${dish.imageBase64}" 
                         alt="${dish.title}" 
                         class="dish-image"
                         onerror="this.style.display='none'">` :
                    `<div class="dish-image-placeholder">
                        <i class="fas fa-utensils"></i>
                    </div>`
                }
                ${!dish.isAvailable ? `<div class="dish-unavailable">Нет в наличии</div>` : ''}
            </div>
            <div class="dish-info">
                <h3 class="dish-title">${escapeHtml(dish.title)}</h3>
                <p class="dish-description">${escapeHtml(dish.description || 'Описание отсутствует')}</p>
                <div class="dish-price">${formatPrice(dish.cost)} ₽</div>
                <div class="dish-meta">
                    <span class="rating">★ ${dish.rating || '0.0'}</span>
                    <span class="category">${escapeHtml(dish.dishCategory)}</span>
                    ${dish.discount > 0 ? `<span class="discount">-${dish.discount}%</span>` : ''}
                </div>
                <button class="btn btn-primary btn-add-to-cart" onclick="event.stopPropagation(); showAddToCartModal(${dish.id})">
                    Добавить в корзину
                </button>
            </div>
        </div>
    `).join('');
    
    console.log(`Displayed ${dishes.length} dishes`);
}

function updateCategoryFilter(dishes) {
    const filter = document.getElementById('categoryFilter');
    if (!filter) {
        console.log('Category filter not found, skipping...');
        return;
    }
    
    const categories = [...new Set(dishes.map(dish => dish.dishCategory).filter(Boolean))];
    console.log(`Found ${categories.length} categories:`, categories);
    
    filter.innerHTML = '<option value="">Все категории</option>' +
        categories.map(category => 
            `<option value="${escapeHtml(category)}">${escapeHtml(category)}</option>`
        ).join('');
}

// Поиск и фильтрация
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchDish');
    const categoryFilter = document.getElementById('categoryFilter');
    
    if (searchInput) {
        searchInput.addEventListener('input', debounce(filterDishes, 300));
    }
    
    if (categoryFilter) {
        categoryFilter.addEventListener('change', filterDishes);
    }
});

function filterDishes() {
    const searchTerm = document.getElementById('searchDish')?.value.toLowerCase() || '';
    const category = document.getElementById('categoryFilter')?.value || '';
    
    console.log(`Filtering dishes: search="${searchTerm}", category="${category}"`);
    
    const filtered = allDishes.filter(dish => {
        const matchesSearch = !searchTerm || 
                            dish.title.toLowerCase().includes(searchTerm) ||
                            (dish.description && dish.description.toLowerCase().includes(searchTerm));
        const matchesCategory = !category || dish.dishCategory === category;
        return matchesSearch && matchesCategory;
    });
    
    console.log(`Found ${filtered.length} dishes after filtering`);
    displayDishes(filtered);
}

// Модальное окно добавления в корзину
function showAddToCartModal(dishId) {
    console.log(`Showing add to cart modal for dish ${dishId}`);
    
    const dish = allDishes.find(d => d.id === dishId);
    if (!dish) {
        console.error(`Dish with id ${dishId} not found`);
        NotificationManager.show('Блюдо не найдено', 'error');
        return;
    }



    selectedDish = dish;
    currentQuantity = 1;

    document.getElementById('dishDetails').innerHTML = `
        <h4>${escapeHtml(dish.title)}</h4>
        <p class="dish-modal-description">${escapeHtml(dish.description || '')}</p>
        <div class="dish-modal-price">
            <span class="price">${formatPrice(dish.cost)} ₽</span>
            ${dish.discount > 0 ? `<span class="original-price">${formatPrice(dish.cost * (1 + dish.discount/100))} ₽</span>` : ''}
        </div>
        <div class="dish-modal-category">Категория: ${escapeHtml(dish.dishCategory)}</div>
    `;

    document.getElementById('quantity').textContent = currentQuantity;
    
    modalManager.show('addToCartModal');
}

function changeQuantity(delta) {
    currentQuantity = Math.max(1, currentQuantity + delta);
    document.getElementById('quantity').textContent = currentQuantity;
    
    // Обновляем итоговую цену
    if (selectedDish) {
        const totalPrice = selectedDish.cost * currentQuantity;
        const totalElement = document.querySelector('.dish-modal-price .total');
        if (!totalElement) {
            const priceElement = document.querySelector('.dish-modal-price');
            priceElement.innerHTML += `<div class="total-price">Итого: ${formatPrice(totalPrice)} ₽</div>`;
        } else {
            totalElement.textContent = `Итого: ${formatPrice(totalPrice)} ₽`;
        }
    }
}

async function addToCart() {
    if (!selectedDish) {
        NotificationManager.show('Блюдо не выбрано', 'error');
        return;
    }

    if (!currentCart) {
        NotificationManager.show('Сначала создайте корзину', 'error');
        modalManager.hideAll();
        // Переключаем на вкладку корзины
        tabManager.switchTab('cart');
        return;
    }

    try {
        const cartItem = {
            cartId: currentCart.cartId || currentCart.id,
            dishId: selectedDish.id,
            count: currentQuantity
        };

        console.log('Adding to cart:', cartItem);
        
        const response = await ApiClient.post('/carts/add/items', cartItem);
        
        NotificationManager.show(`"${selectedDish.title}" добавлен в корзину`, 'success');
        modalManager.hideAll();
        
        // Обновить корзину
        if (typeof loadCart === 'function') {
            loadCart(currentCart.cartId || currentCart.id);
        }
        
    } catch (error) {
        console.error('Error adding to cart:', error);
        NotificationManager.show(`Ошибка: ${error.message}`, 'error');
    }
}

// Вспомогательные функции
function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Глобальные функции
window.showAddToCartModal = showAddToCartModal;
window.changeQuantity = changeQuantity;
window.addToCart = addToCart;
window.loadDishes = loadDishes;
window.filterDishes = filterDishes;
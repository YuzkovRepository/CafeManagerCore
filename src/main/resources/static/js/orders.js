console.log('📦 Orders module loaded');

async function loadOrders() {
    const phoneInput = document.getElementById('orderPhone');
    if (!phoneInput) {
        console.error('Order phone input not found');
        return;
    }
    
    const phone = phoneInput.value.trim();
    console.log('Loading orders for phone:', phone);
    
    if (!isValidPhone(phone)) {
        NotificationManager.show('Введите корректный номер телефона', 'error');
        phoneInput.focus();
        return;
    }

    const container = document.getElementById('ordersList');
    if (!container) {
        console.error('Orders container not found');
        return;
    }
    
    container.innerHTML = '<div class="loading">Поиск заказов...</div>';

    try {
        // В реальном приложении здесь был бы запрос для получения заказов по телефону
        // Для демонстрации используем mock данные
        console.log('Fetching orders from API...');
        
        // Имитация загрузки
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        const mockOrders = [
            {
                id: 1,
                dateTime: '2024-01-15T14:30:00',
                status: 'ACCEPTED',
                guestId: 1,
                totalAmount: 1250.50
            },
            {
                id: 2,
                dateTime: '2024-01-14T18:45:00',
                status: 'COMPLETED',
                guestId: 1,
                totalAmount: 890.00
            },
            {
                id: 3,
                dateTime: '2024-01-13T12:15:00',
                status: 'IN_PROGRESS',
                guestId: 1,
                totalAmount: 2100.00
            }
        ];
        
        displayOrders(mockOrders);
        NotificationManager.show(`Найдено ${mockOrders.length} заказов`, 'success');
        
    } catch (error) {
        console.error('Error loading orders:', error);
        container.innerHTML = `
            <div class="error">
                <h3>Ошибка загрузки заказов</h3>
                <p>${error.message}</p>
                <button onclick="loadOrders()" class="btn btn-primary">Попробовать снова</button>
            </div>
        `;
        NotificationManager.show('Ошибка загрузки заказов', 'error');
    }
}

function displayOrders(orders) {
    const container = document.getElementById('ordersList');
    
    if (!orders || orders.length === 0) {
        container.innerHTML = `
            <div class="empty-orders">
                <i class="fas fa-receipt"></i>
                <h3>Заказы не найдены</h3>
                <p>У вас пока нет заказов или введен неправильный номер телефона</p>
            </div>
        `;
        return;
    }

    container.innerHTML = orders.map(order => `
        <div class="order-card" data-order-id="${order.id}">
            <div class="order-header">
                <div class="order-info">
                    <div class="order-id">Заказ #${order.id}</div>
                    <div class="order-date">${formatDateTime(order.dateTime)}</div>
                </div>
                <div class="order-status ${getStatusClass(order.status)}">
                    ${getStatusText(order.status)}
                </div>
            </div>
            
            <div class="order-details">
                <div class="order-detail">
                    <span class="detail-label">ID гостя:</span>
                    <span class="detail-value">${order.guestId}</span>
                </div>
                <div class="order-detail">
                    <span class="detail-label">Статус:</span>
                    <span class="detail-value">${getStatusText(order.status)}</span>
                </div>
                <div class="order-detail">
                    <span class="detail-label">Дата создания:</span>
                    <span class="detail-value">${formatDateTime(order.dateTime)}</span>
                </div>
            </div>
            
            <div class="order-footer">
                <div class="order-total">
                    Итого: <span class="total-amount">${formatPrice(order.totalAmount)} ₽</span>
                </div>
                <button class="btn btn-outline" onclick="viewOrderDetails(${order.id})">
                    <i class="fas fa-eye"></i> Подробности
                </button>
            </div>
        </div>
    `).join('');
    
    console.log(`Displayed ${orders.length} orders`);
}

function getStatusClass(status) {
    const statusClasses = {
        'ACCEPTED': 'status-accepted',
        'COMPLETED': 'status-completed',
        'IN_PROGRESS': 'status-in-progress',
        'DRAFT': 'status-draft',
        'CANCELLED': 'status-cancelled'
    };
    return statusClasses[status] || 'status-draft';
}

function getStatusText(status) {
    const statusTexts = {
        'ACCEPTED': 'Принят',
        'COMPLETED': 'Выполнен',
        'IN_PROGRESS': 'В процессе',
        'DRAFT': 'Черновик',
        'CANCELLED': 'Отменен'
    };
    return statusTexts[status] || status;
}

function viewOrderDetails(orderId) {
    console.log('Viewing order details:', orderId);
    NotificationManager.show(`Детали заказа #${orderId} (функциональность в разработке)`, 'info');
}

// Глобальные функции
window.loadOrders = loadOrders;
window.viewOrderDetails = viewOrderDetails;
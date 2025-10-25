// Конфигурация API
const API_CONFIG = {
    BASE_URL: 'http://localhost:8080/api/v1',
    DEBUG: true
};

// Утилиты
class ApiClient {
    static async request(url, options = {}) {
        const fullUrl = `${API_CONFIG.BASE_URL}${url}`;
        
        if (API_CONFIG.DEBUG) {
            console.log(`🔄 API Request: ${options.method || 'GET'} ${fullUrl}`, options.body || '');
        }

        try {
            const response = await fetch(fullUrl, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });

            if (API_CONFIG.DEBUG) {
                console.log(`📡 API Response: ${response.status} ${response.statusText}`);
            }

            if (!response.ok) {
                let errorText = 'Unknown error';
                try {
                    errorText = await response.text();
                } catch (e) {
                    errorText = 'Cannot read error response';
                }
                
                console.error('API Error Details:', {
                    status: response.status,
                    statusText: response.statusText,
                    url: fullUrl,
                    error: errorText
                });
                
                throw new Error(`HTTP ${response.status}: ${errorText}`);
            }

            // Для DELETE запросов без тела
            if (response.status === 204) {
                return null;
            }

            const data = await response.json();
            
            if (API_CONFIG.DEBUG) {
                console.log('✅ API Success:', data);
            }
            
            return data;
        } catch (error) {
            console.error('❌ API Request Failed:', error);
            
            // Более информативные сообщения об ошибках
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Не удалось подключиться к серверу. Проверьте, запущен ли бэкенд.');
            }
            
            throw error;
        }
    }

    static async get(url) {
        return this.request(url);
    }

    static async post(url, data) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    static async patch(url, data) {
        return this.request(url, {
            method: 'PATCH',
            body: JSON.stringify(data)
        });
    }

    static async delete(url, data = null) {
        const options = {
            method: 'DELETE'
        };
        
        if (data) {
            options.body = JSON.stringify(data);
        }
        
        return this.request(url, options);
    }
}

// Управление вкладками
class TabManager {
    constructor() {
        this.currentTab = 'dishes';
        this.init();
    }

    init() {
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                this.switchTab(e.target.dataset.tab);
            });
        });
    }

    switchTab(tabName) {
        console.log(`Switching to tab: ${tabName}`);
        
        // Скрыть все вкладки
        document.querySelectorAll('.tab-content').forEach(tab => {
            tab.classList.remove('active');
        });
        
        // Убрать активный класс у всех кнопок
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.classList.remove('active');
        });

        // Показать выбранную вкладку
        const targetTab = document.getElementById(tabName);
        if (targetTab) {
            targetTab.classList.add('active');
        } else {
            console.error(`Tab with id ${tabName} not found`);
        }
        
        // Активировать кнопку
        const targetButton = document.querySelector(`[data-tab="${tabName}"]`);
        if (targetButton) {
            targetButton.classList.add('active');
        }

        this.currentTab = tabName;

        // Загрузить данные для вкладки
        this.loadTabData(tabName);
    }

    loadTabData(tabName) {
        console.log(`Loading data for tab: ${tabName}`);
        switch(tabName) {
            case 'dishes':
                if (typeof loadDishes === 'function') {
                    loadDishes();
                }
                break;
            case 'cart':
                // Корзина загружается при вводе телефона
                console.log('Cart tab activated');
                break;
            case 'orders':
                // Заказы загружаются при вводе телефона
                console.log('Orders tab activated');
                break;
        }
    }
}

// Управление модальными окнами
class ModalManager {
    constructor() {
        this.init();
    }

    init() {
        // Закрытие модальных окон
        document.querySelectorAll('.close').forEach(closeBtn => {
            closeBtn.addEventListener('click', () => {
                this.hideAll();
            });
        });

        // Закрытие по клику вне модального окна
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                this.hideAll();
            }
        });

        // Закрытие по ESC
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.hideAll();
            }
        });
    }

    show(modalId) {
        console.log(`Showing modal: ${modalId}`);
        this.hideAll();
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('hidden');
        } else {
            console.error(`Modal with id ${modalId} not found`);
        }
    }

    hide(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('hidden');
        }
    }

    hideAll() {
        document.querySelectorAll('.modal').forEach(modal => {
            modal.classList.add('hidden');
        });
    }
}

// Утилиты для уведомлений
class NotificationManager {
    static show(message, type = 'info', duration = 5000) {
        console.log(`Notification [${type}]: ${message}`);
        
        // Создаем контейнер для уведомлений если его нет
        let container = document.getElementById('notifications-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'notifications-container';
            container.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 10000;
                max-width: 400px;
            `;
            document.body.appendChild(container);
        }

        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <span class="notification-message">${message}</span>
                <button class="notification-close" onclick="this.parentElement.parentElement.remove()">×</button>
            </div>
        `;
        
        notification.style.cssText = `
            background: ${this.getBackgroundColor(type)};
            color: white;
            padding: 1rem;
            border-radius: 10px;
            margin-bottom: 0.5rem;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            animation: slideInRight 0.3s ease;
        `;

        const content = notification.querySelector('.notification-content');
        content.style.display = 'flex';
        content.style.justifyContent = 'space-between';
        content.style.alignItems = 'center';
        
        const closeBtn = notification.querySelector('.notification-close');
        closeBtn.style.cssText = `
            background: none;
            border: none;
            color: white;
            font-size: 1.2rem;
            cursor: pointer;
            padding: 0;
            margin-left: 1rem;
        `;

        container.appendChild(notification);

        // Автоматическое удаление
        if (duration > 0) {
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.style.animation = 'slideOutRight 0.3s ease';
                    setTimeout(() => {
                        if (notification.parentNode) {
                            notification.parentNode.removeChild(notification);
                        }
                    }, 300);
                }
            }, duration);
        }
        
        return notification;
    }

    static getBackgroundColor(type) {
        const colors = {
            'error': 'linear-gradient(135deg, #ff416c 0%, #ff4b2b 100%)',
            'success': 'linear-gradient(135deg, #56ab2f 0%, #a8e6cf 100%)',
            'warning': 'linear-gradient(135deg, #f39c12 0%, #f1c40f 100%)',
            'info': 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
        };
        return colors[type] || colors.info;
    }
}

// Глобальные переменные
let currentCart = null;
let selectedDish = null;
let currentQuantity = 1;
let allDishes = [];

// Утилиты
function formatPrice(price) {
    if (!price) return '0.00';
    return parseFloat(price).toFixed(2);
}

function formatDateTime(dateTime) {
    if (!dateTime) return 'Не указано';
    try {
        const date = new Date(dateTime);
        return date.toLocaleString('ru-RU');
    } catch (e) {
        return 'Неверный формат даты';
    }
}

function isValidPhone(phone) {
    if (!phone) return false;
    const phoneRegex = /^\+?[7-8]?[0-9]{10}$/;
    return phoneRegex.test(phone.replace(/\s/g, ''));
}

// Тестирование подключения
async function testBackendConnection() {
    console.log('Testing backend connection...');
    
    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}/dishes`);
        const isOk = response.ok;
        
        console.log(`Backend connection test: ${isOk ? 'SUCCESS' : 'FAILED'}`);
        console.log(`Status: ${response.status} ${response.statusText}`);
        
        return isOk;
    } catch (error) {
        console.error('Backend connection test FAILED:', error);
        return false;
    }
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', async () => {
    console.log('🚀 Initializing application...');
    
    // Инициализация менеджеров
    window.tabManager = new TabManager();
    window.modalManager = new ModalManager();
    
    // Тестирование подключения к бэкенду
    const isConnected = await testBackendConnection();
    
    if (!isConnected) {
        NotificationManager.show(
            '⚠️ Не удалось подключиться к серверу. Проверьте, что бэкенд запущен на localhost:8080', 
            'warning',
            10000
        );
    } else {
        NotificationManager.show('✅ Подключение к серверу установлено', 'success', 3000);
    }
    
    // Загрузка блюд
    if (typeof loadDishes === 'function') {
        loadDishes();
    }
    
    console.log('✅ Application initialized successfully');
});

// Добавляем CSS для анимаций
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOutRight {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
    
    .debug-panel {
        position: fixed;
        bottom: 10px;
        left: 10px;
        background: rgba(0,0,0,0.8);
        color: white;
        padding: 10px;
        border-radius: 5px;
        font-size: 12px;
        z-index: 1000;
    }
`;
document.head.appendChild(style);

// Функция для отладки (можно вызвать из консоли)
window.debugInfo = function() {
    return {
        currentCart,
        selectedDish,
        currentQuantity,
        allDishesCount: allDishes.length,
        apiConfig: API_CONFIG
    };
};
// QR Manager - JavaScript Application

// Global app object
const QRManager = {
    // Configuration
    config: {
        apiBaseUrl: '/api',
        toastDuration: 3000,
        confirmDelay: 300
    },

    // Initialize application
    init: function() {
        this.initLucideIcons();
        this.initTooltips();
        this.initModals();
        this.initForms();
        this.initFileUploads();
        console.log('QR Manager initialized');
    },

    // Initialize Lucide icons
    initLucideIcons: function() {
        if (typeof lucide !== 'undefined') {
            lucide.createIcons();
        }
    },

    // Initialize tooltips
    initTooltips: function() {
        const tooltips = document.querySelectorAll('[title]');
        tooltips.forEach(element => {
            element.addEventListener('mouseenter', this.showTooltip.bind(this));
            element.addEventListener('mouseleave', this.hideTooltip.bind(this));
        });
    },

    // Show tooltip
    showTooltip: function(event) {
        const element = event.target;
        const title = element.getAttribute('title');
        if (!title) return;

        const tooltip = document.createElement('div');
        tooltip.className = 'absolute z-50 px-2 py-1 text-xs bg-gray-900 text-white rounded shadow-lg';
        tooltip.textContent = title;
        tooltip.style.top = (element.offsetTop - 30) + 'px';
        tooltip.style.left = element.offsetLeft + 'px';

        document.body.appendChild(tooltip);
        element.setAttribute('data-tooltip-id', Date.now());
    },

    // Hide tooltip
    hideTooltip: function(event) {
        const tooltips = document.querySelectorAll('.absolute.z-50');
        tooltips.forEach(tooltip => tooltip.remove());
    },

    // Initialize modals
    initModals: function() {
        const modals = document.querySelectorAll('[data-modal]');
        modals.forEach(modal => {
            const trigger = document.querySelector(`[data-modal-trigger="${modal.id}"]`);
            const close = modal.querySelector('[data-modal-close]');

            if (trigger) {
                trigger.addEventListener('click', () => this.showModal(modal.id));
            }

            if (close) {
                close.addEventListener('click', () => this.hideModal(modal.id));
            }
        });
    },

    // Show modal
    showModal: function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('hidden');
            modal.classList.add('flex');
            document.body.style.overflow = 'hidden';
        }
    },

    // Hide modal
    hideModal: function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('hidden');
            modal.classList.remove('flex');
            document.body.style.overflow = 'auto';
        }
    },

    // Initialize forms
    initForms: function() {
        const forms = document.querySelectorAll('form');
        forms.forEach(form => {
            form.addEventListener('submit', this.handleFormSubmit.bind(this));
        });

        // Initialize form validation
        this.initFormValidation();
    },

    // Handle form submission
    handleFormSubmit: function(event) {
        const form = event.target;
        const submitBtn = form.querySelector('button[type="submit"]');

        if (submitBtn) {
            const originalText = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<div class="spinner mr-2"></div>Elaborazione...';

            // Re-enable after 5 seconds as fallback
            setTimeout(() => {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            }, 5000);
        }
    },

    // Initialize form validation
    initFormValidation: function() {
        const inputs = document.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            input.addEventListener('blur', this.validateField.bind(this));
            input.addEventListener('input', this.clearFieldError.bind(this));
        });
    },

    // Validate single field
    validateField: function(event) {
        const field = event.target;
        const value = field.value.trim();

        // Remove existing error
        this.clearFieldError(event);

        // Required field validation
        if (field.hasAttribute('required') && !value) {
            this.showFieldError(field, 'Questo campo è obbligatorio');
            return false;
        }

        // Email validation
        if (field.type === 'email' && value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) {
                this.showFieldError(field, 'Inserisci un indirizzo email valido');
                return false;
            }
        }

        // Password validation
        if (field.type === 'password' && value) {
            if (value.length < 6) {
                this.showFieldError(field, 'La password deve essere di almeno 6 caratteri');
                return false;
            }
        }

        return true;
    },

    // Show field error
    showFieldError: function(field, message) {
        field.classList.add('border-red-300');

        let errorDiv = field.parentNode.querySelector('.field-error');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'field-error text-red-600 text-sm mt-1';
            field.parentNode.appendChild(errorDiv);
        }

        errorDiv.textContent = message;
    },

    // Clear field error
    clearFieldError: function(event) {
        const field = event.target;
        field.classList.remove('border-red-300');

        const errorDiv = field.parentNode.querySelector('.field-error');
        if (errorDiv) {
            errorDiv.remove();
        }
    },

    // Initialize file uploads
    initFileUploads: function() {
        const fileInputs = document.querySelectorAll('input[type="file"]');
        fileInputs.forEach(input => {
            input.addEventListener('change', this.handleFileUpload.bind(this));
        });
    },

    // Handle file upload
    handleFileUpload: function(event) {
        const input = event.target;
        const file = input.files[0];

        if (!file) return;

        // Validate file size (10MB max)
        const maxSize = 10 * 1024 * 1024;
        if (file.size > maxSize) {
            this.showToast('File troppo grande. Massimo 10MB consentiti.', 'error');
            input.value = '';
            return;
        }

        // Validate file type (images only)
        if (input.accept && !input.accept.split(',').some(type => 
            file.type.match(type.trim()))) {
            this.showToast('Tipo di file non supportato.', 'error');
            input.value = '';
            return;
        }

        // Show preview if image
        if (file.type.startsWith('image/')) {
            this.showImagePreview(input, file);
        }
    },

    // Show image preview
    showImagePreview: function(input, file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            let preview = input.parentNode.querySelector('.image-preview');
            if (!preview) {
                preview = document.createElement('img');
                preview.className = 'image-preview w-32 h-32 object-cover rounded-lg mt-2';
                input.parentNode.appendChild(preview);
            }
            preview.src = e.target.result;
        };
        reader.readAsDataURL(file);
    },

    // Show toast notification
    showToast: function(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `fixed top-4 right-4 z-50 px-6 py-3 rounded-lg shadow-lg text-white max-w-sm ${
            type === 'error' ? 'bg-red-600' : 
            type === 'success' ? 'bg-green-600' : 
            type === 'warning' ? 'bg-yellow-600' : 'bg-blue-600'
        }`;

        toast.innerHTML = `
            <div class="flex items-center">
                <span class="mr-2">${message}</span>
                <button onclick="this.parentElement.parentElement.remove()" class="ml-auto">
                    <i data-lucide="x" class="w-4 h-4"></i>
                </button>
            </div>
        `;

        document.body.appendChild(toast);

        // Auto-remove after duration
        setTimeout(() => {
            if (toast.parentNode) {
                toast.remove();
            }
        }, this.config.toastDuration);

        // Re-initialize icons for the toast
        this.initLucideIcons();
    },

    // Confirm action
    confirm: function(message, callback) {
        if (window.confirm(message)) {
            setTimeout(callback, this.config.confirmDelay);
        }
    },

    // Format date
    formatDate: function(date) {
        return new Date(date).toLocaleDateString('it-IT');
    },

    // Format file size
    formatFileSize: function(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    QRManager.init();
});

// Global utility functions
window.showAddForm = function() {
    const form = document.getElementById('add-article-form');
    if (form) {
        form.classList.remove('hidden');
        const nameInput = form.querySelector('input[name="name"]');
        if (nameInput) nameInput.focus();
    }
};

window.hideAddForm = function() {
    const form = document.getElementById('add-article-form');
    if (form) {
        form.classList.add('hidden');
        form.reset();
        // Clear any previews
        const previews = form.querySelectorAll('.image-preview');
        previews.forEach(preview => preview.remove());
    }
};

// Export for use in other scripts
window.QRManager = QRManager;
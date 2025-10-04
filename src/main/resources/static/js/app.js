// QR Manager - Main JavaScript File
// Gestisce tutti gli eventi dell'interfaccia utente

class QRManager {
    constructor() {
        this.init();
    }

    init() {
        // Aspetta che il DOM sia completamente caricato
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.initializeApp());
        } else {
            this.initializeApp();
        }
    }

    initializeApp() {
        console.log('🚀 QR Manager initialized');

        // Inizializza tutte le funzionalità
        this.initializeLucideIcons();
        this.initializeMobileMenu();
        this.initializeUserDropdown();
        this.initializeSettingsDropdown();
        this.initializeDarkMode();
        this.initializeActiveNavigation();
        this.initializeTooltips();
        this.initializeFormValidation();
        this.initializeImagePreview();
        this.initializeQRCodeGeneration();

        // Personalizzazioni specifiche
        this.initializeExpiryDateInput();
        this.initializeOwnerEmailSelect();
    }

    // ========================================
    // INIZIALIZZAZIONE ICONE LUCIDE
    // ========================================
    initializeLucideIcons() {
        if (typeof lucide !== 'undefined') {
            lucide.createIcons();
            console.log('✅ Lucide icons loaded');
        } else {
            console.warn('⚠️ Lucide not found, loading from CDN...');
            this.loadLucideFromCDN();
        }
    }

    loadLucideFromCDN() {
        const script = document.createElement('script');
        script.src = 'https://cdn.jsdelivr.net/npm/lucide@latest/dist/umd/lucide.js';
        script.onload = () => {
            lucide.createIcons();
            console.log('✅ Lucide icons loaded from CDN');
        };
        document.head.appendChild(script);
    }

    // ========================================
    // MENU MOBILE HAMBURGER
    // ========================================
    initializeMobileMenu() {
        const mobileMenuButton = document.getElementById('mobileMenuButton');
        const mobileMenu = document.getElementById('mobileMenu');
        const menuIconOpen = document.getElementById('menuIconOpen');
        const menuIconClose = document.getElementById('menuIconClose');

        if (!mobileMenuButton || !mobileMenu) return;

        let isOpen = false;

        mobileMenuButton.addEventListener('click', () => {
            isOpen = !isOpen;
            if (isOpen) {
                mobileMenu.style.maxHeight = mobileMenu.scrollHeight + 'px';
                mobileMenu.classList.remove('max-h-0');

                menuIconOpen.classList.add('hidden');
                menuIconClose.classList.remove('hidden');
            } else {
                mobileMenu.style.maxHeight = '0';
                mobileMenu.classList.add('max-h-0');

                menuIconClose.classList.add('hidden');
                menuIconOpen.classList.remove('hidden');
            }
        });

        mobileMenu.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => {
                isOpen = false;
                mobileMenu.style.maxHeight = '0';
                mobileMenu.classList.add('max-h-0');

                menuIconClose.classList.add('hidden');
                menuIconOpen.classList.remove('hidden');
            });
        });
    }

    // ================================
    // DROPDOWN UTENTE (Desktop)
    // ================================
    initializeUserDropdown() {
        const userMenuButton = document.getElementById('userMenuButton');
        const userDropdown = document.getElementById('userDropdown');

        if (!userMenuButton || !userDropdown) {
            console.warn('⚠️ User dropdown elements not found');
            return;
        }

        let isOpen = false;

        userMenuButton.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            this.closeAllDropdowns('userDropdown');
            isOpen = !isOpen;

            if (isOpen) {
                userDropdown.classList.remove('opacity-0', 'invisible');
                userDropdown.classList.add('opacity-100', 'visible');
            } else {
                userDropdown.classList.add('opacity-0', 'invisible');
                userDropdown.classList.remove('opacity-100', 'visible');
            }

            console.log(`👤 User dropdown ${isOpen ? 'opened' : 'closed'}`);
        });

        // Chiudi dropdown cliccando fuori
        document.addEventListener('click', (e) => {
            if (!userMenuButton.contains(e.target) && !userDropdown.contains(e.target)) {
                if (isOpen) {
                    isOpen = false;
                    userDropdown.classList.add('opacity-0', 'invisible');
                    userDropdown.classList.remove('opacity-100', 'visible');
                }
            }
        });
    }

    // ================================
    // DROPDOWN IMPOSTAZIONI (Mobile)
    // ================================
    initializeSettingsDropdown() {
        const settingsButton = document.getElementById('mobileSettingsButton');
        const settingsDropdown = document.getElementById('mobileSettingsDropdown');

        if (!settingsButton || !settingsDropdown) {
            console.warn('⚠️ Settings dropdown elements not found');
            return;
        }

        let isOpen = false;

        settingsButton.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            this.closeAllDropdowns('mobileSettingsDropdown');
            isOpen = !isOpen;

            if (isOpen) {
                settingsDropdown.classList.remove('opacity-0', 'invisible');
                settingsDropdown.classList.add('opacity-100', 'visible');
                console.log('⚙️ Settings dropdown opened');
            } else {
                settingsDropdown.classList.add('opacity-0', 'invisible');
                settingsDropdown.classList.remove('opacity-100', 'visible');
                console.log('⚙️ Settings dropdown closed');
            }
        });

        // Chiudi dropdown cliccando fuori
        document.addEventListener('click', (e) => {
            if (!settingsButton.contains(e.target) && !settingsDropdown.contains(e.target)) {
                if (isOpen) {
                    isOpen = false;
                    settingsDropdown.classList.add('opacity-0', 'invisible');
                    settingsDropdown.classList.remove('opacity-100', 'visible');
                }
            }
        });

        // Dark mode toggle nel dropdown mobile
        const darkModeToggleMobile = document.getElementById('darkModeToggleMobile');
        if (darkModeToggleMobile) {
            darkModeToggleMobile.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                const isDark = document.documentElement.classList.toggle('dark');
                localStorage.setItem('darkMode', isDark);
                const darkModeInput = document.getElementById('darkModeInput');
                if (darkModeInput) {
                    darkModeInput.value = isDark;
                }
                this.updateDarkModeIcon(isDark);
                console.log('🌓 Dark mode toggled from mobile: ' + isDark);
            });
        }
    }

    // ================================
    // CHIUDI TUTTI I DROPDOWN
    // ================================
    closeAllDropdowns(excludeId) {
        const dropdowns = [{
            id: 'userDropdown',
            element: document.getElementById('userDropdown')
        }, {id: 'mobileSettingsDropdown', element: document.getElementById('mobileSettingsDropdown')}];
        dropdowns.forEach(({id, element}) => {
            if (id !== excludeId && element) {
                element.classList.add('opacity-0', 'invisible');
                element.classList.remove('opacity-100', 'visible');
            }
        });
    }

    // ========================================
    // MODALITÀ SCURA
    // ========================================
    initializeDarkMode() {
        const darkModeToggle = document.getElementById('darkModeToggle');
        const darkModeInput = document.getElementById('darkModeInput');

        if (!darkModeToggle) {
            console.warn('⚠️ Dark mode toggle not found');
            return;
        }

        // Carica preferenza salvata
        const isDarkMode = localStorage.getItem('darkMode') === 'true';

        if (isDarkMode) {
            document.documentElement.classList.add('dark');
            this.updateDarkModeIcon(true);
        }

        if (darkModeInput) {
            darkModeInput.value = isDarkMode;
        }

        darkModeToggle.addEventListener('click', () => {
            const isDark = document.documentElement.classList.toggle('dark');
            localStorage.setItem('darkMode', isDark);
            if (darkModeInput) {
                darkModeInput.value = isDark;
            }
            this.updateDarkModeIcon(isDark);

            console.log(`🌓 Dark mode ${isDark ? 'enabled' : 'disabled'}`);
        });
    }

    updateDarkModeIcon(isDark) {
        const icon = document.querySelector('#darkModeToggle i');
        if (icon) {
            icon.setAttribute('data-lucide', isDark ? 'sun' : 'moon');
            if (typeof lucide !== 'undefined') {
                lucide.createIcons();
            }
        }
    }

    // ========================================
    // NAVIGAZIONE ATTIVA
    // ========================================
    initializeActiveNavigation() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.nav-link');

        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (currentPath === href || (href === '/admin/dashboard' && currentPath === '/') || (href !== '/admin/dashboard' && currentPath.startsWith(href))) {
                link.classList.add('text-teal-600', 'font-medium');
                link.classList.remove('text-gray-600');
            }
        });
    }

    // ========================================
    // TOOLTIP
    // ========================================
    initializeTooltips() {
        const tooltipElements = document.querySelectorAll('[data-tooltip]');

        tooltipElements.forEach(element => {
            element.addEventListener('mouseenter', (e) => {
                this.showTooltip(e.target, e.target.dataset.tooltip);
            });

            element.addEventListener('mouseleave', () => {
                this.hideTooltip();
            });
        });
    }

    showTooltip(element, text) {
        const tooltip = document.createElement('div');
        tooltip.className = 'fixed z-50 px-2 py-1 text-xs text-white bg-gray-900 rounded shadow-lg pointer-events-none';
        tooltip.textContent = text;
        tooltip.id = 'tooltip';

        document.body.appendChild(tooltip);

        const rect = element.getBoundingClientRect();
        tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
        tooltip.style.top = rect.bottom + 5 + 'px';
    }

    hideTooltip() {
        const tooltip = document.getElementById('tooltip');
        if (tooltip) {
            tooltip.remove();
        }
    }

    // ========================================
    // VALIDAZIONE FORM
    // ========================================
    initializeFormValidation() {
        const forms = document.querySelectorAll('form[data-validate]');

        forms.forEach(form => {
            form.addEventListener('submit', (e) => {
                if (!this.validateForm(form)) {
                    e.preventDefault();
                }
            });
        });
    }

    validateForm(form) {
        let isValid = true;
        const inputs = form.querySelectorAll('input[required], textarea[required], select[required]');

        inputs.forEach(input => {
            if (!input.value.trim()) {
                this.showFieldError(input, 'Questo campo è obbligatorio');
                isValid = false;
            } else {
                this.clearFieldError(input);
            }
        });

        return isValid;
    }

    showFieldError(input, message) {
        input.classList.add('border-red-500');

        let errorElement = input.parentNode.querySelector('.error-message');
        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.className = 'error-message text-red-500 text-sm mt-1';
            input.parentNode.appendChild(errorElement);
        }
        errorElement.textContent = message;
    }

    clearFieldError(input) {
        input.classList.remove('border-red-500');
        const errorElement = input.parentNode.querySelector('.error-message');
        if (errorElement) {
            errorElement.remove();
        }
    }

    // ========================================
    // PREVIEW IMMAGINI
    // ========================================
    initializeImagePreview() {
        const imageInputs = document.querySelectorAll('input[type="file"][accept*="image"]');

        imageInputs.forEach(input => {
            input.addEventListener('change', (e) => {
                this.handleImagePreview(e.target);
            });
        });
    }

    handleImagePreview(input) {
        const file = input.files[0];
        if (!file) return;

        if (!file.type.startsWith('image/')) {
            alert('Per favore seleziona un file immagine valido.');
            input.value = '';
            return;
        }

        if (file.size > 10 * 1024 * 1024) {
            alert('Il file è troppo grande. Dimensione massima: 10MB');
            input.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
            let preview = input.parentNode.querySelector('.image-preview');
            if (!preview) {
                preview = document.createElement('img');
                preview.className = 'image-preview mt-2 h-32 w-32 object-cover rounded-lg border';
                input.parentNode.appendChild(preview);
            }
            preview.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }

    // ========================================
    // GENERAZIONE QR CODE (placeholder)
    // ========================================
    initializeQRCodeGeneration() {
        const qrButtons = document.querySelectorAll('[data-qr-generate]');

        qrButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                const qrData = button.dataset.qrGenerate;
                this.generateQRCode(qrData);
            });
        });
    }

    generateQRCode(data) {
        // Implementazione generazione QR code
        console.log('🎯 Generating QR code for:', data);
        // Verrà implementata nella parte 3
    }

    // ========================================
    // EXPIRY DATE INPUT INIT
    // ========================================
    initializeExpiryDateInput() {
        const expiryInput = document.querySelector('input[name="expiryDate"]');
        if (expiryInput) {
            if (!expiryInput.value) {
                const nextYear = new Date();
                nextYear.setFullYear(nextYear.getFullYear() + 1);
                expiryInput.value = nextYear.toISOString().split('T')[0];
            }
        }
    }

    initializeOwnerEmailSelect() {
        if (typeof TomSelect === 'undefined') {
            console.warn('⚠️ TomSelect non caricato: assicurati che CSS e JS siano inclusi');
            return;
        }
        const ownerEmailSelect = document.querySelector('#ownerEmail');
        if (ownerEmailSelect) {
            new TomSelect(ownerEmailSelect, {
                create: false,
                sortField: {field: "text", direction: "asc"},
                highlight: true,
                searchField: ['text'],
                dropdownClass: 'ts-dropdown',
                controlClass: 'ts-control',
                render: {
                    option: function (data, escape) {
                        return `<div class="cursor-pointer px-3 py-2 hover:bg-teal-600 hover:text-white dark:hover:bg-teal-500">${escape(data.text)}</div>`;
                    },
                    item: function (data, escape) {
                        // Mostra placeholder correttamente come negli input se nessuna selezione
                        if (data.value === "") {
                            return `<span role="presentation">${escape(data.text)}</span>`;
                        }
                        return `<span>${escape(data.text)}</span>`;
                    }
                }
            });
        }
    }

}

// Inizializza l'applicazione
const qrManager = new QRManager();

// Espone per debugging
window.QRManager = qrManager;

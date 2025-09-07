/* ===== RESPONSIVE JAVASCRIPT FOR PANACEA HOTEL WEBSITE ===== */

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    initResponsiveFeatures();
});

function initResponsiveFeatures() {
    // Initialize mobile navigation
    initMobileNavigation();
    
    // Initialize responsive tables
    initResponsiveTables();
    
    // Initialize touch gestures
    initTouchGestures();
    
    // Initialize window resize handler
    initResizeHandler();
    
    // Initialize orientation change handler
    initOrientationHandler();
    
    // Initialize grid optimization
    initGridOptimization();
    
    // Initialize flexbox enhancements
    initFlexboxEnhancements();
    
    // Initialize advanced responsive features
    initAdvancedResponsive();
    
    // Initialize typography optimization
    initTypographyOptimization();
    
    // Initialize spacing optimization
    initSpacingOptimization();
    
    // Initialize button group optimization
    initButtonGroupOptimization();
    
    // Initialize forms, tables and modals optimization
    initFormsTablesModalsOptimization();
}

/* ===== MOBILE NAVIGATION ===== */
function initMobileNavigation() {
    const mobileToggle = document.getElementById('mobileToggle') || document.querySelector('.mobile-toggle');
    const sidebar = document.getElementById('sidebar') || document.querySelector('.sidebar');
    const overlay = createMobileOverlay();
    
    if (mobileToggle && sidebar) {
        // Toggle sidebar on mobile
        mobileToggle.addEventListener('click', function(e) {
            e.preventDefault();
            toggleMobileSidebar(sidebar, overlay);
        });
        
        // Close sidebar when clicking overlay
        overlay.addEventListener('click', function() {
            closeMobileSidebar(sidebar, overlay);
        });
        
        // Close sidebar on escape key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && sidebar.classList.contains('open')) {
                closeMobileSidebar(sidebar, overlay);
            }
        });
        
        // Close sidebar when clicking on main content (mobile only)
        const main = document.querySelector('main');
        if (main) {
            main.addEventListener('click', function() {
                if (window.innerWidth <= 768 && sidebar.classList.contains('open')) {
                    closeMobileSidebar(sidebar, overlay);
                }
            });
        }
    }
}

function createMobileOverlay() {
    let overlay = document.querySelector('.mobile-overlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.className = 'mobile-overlay';
        document.body.appendChild(overlay);
    }
    return overlay;
}

function toggleMobileSidebar(sidebar, overlay) {
    sidebar.classList.toggle('open');
    overlay.classList.toggle('active');
    document.body.classList.toggle('sidebar-open');
    
    // Prevent body scroll when sidebar is open
    if (sidebar.classList.contains('open')) {
        document.body.style.overflow = 'hidden';
    } else {
        document.body.style.overflow = '';
    }
}

function closeMobileSidebar(sidebar, overlay) {
    sidebar.classList.remove('open');
    overlay.classList.remove('active');
    document.body.classList.remove('sidebar-open');
    document.body.style.overflow = '';
}

/* ===== RESPONSIVE TABLES ===== */
function initResponsiveTables() {
    const tables = document.querySelectorAll('table');
    
    tables.forEach(table => {
        makeTableResponsive(table);
    });
}

function makeTableResponsive(table) {
    // Add responsive class
    table.classList.add('table-responsive-stack');
    
    // Add data labels for mobile view
    const headers = table.querySelectorAll('thead th');
    const rows = table.querySelectorAll('tbody tr');
    
    rows.forEach(row => {
        const cells = row.querySelectorAll('td');
        cells.forEach((cell, index) => {
            if (headers[index]) {
                cell.setAttribute('data-label', headers[index].textContent.trim());
            }
        });
    });
    
    // Wrap table in responsive container
    if (!table.parentElement.classList.contains('table-responsive')) {
        const wrapper = document.createElement('div');
        wrapper.className = 'table-responsive';
        table.parentNode.insertBefore(wrapper, table);
        wrapper.appendChild(table);
    }
}

/* ===== TOUCH GESTURES ===== */
function initTouchGestures() {
    let startX = 0;
    let startY = 0;
    const sidebar = document.querySelector('.sidebar');
    
    if (!sidebar) return;
    
    // Swipe to open/close sidebar
    document.addEventListener('touchstart', function(e) {
        startX = e.touches[0].clientX;
        startY = e.touches[0].clientY;
    }, { passive: true });
    
    document.addEventListener('touchend', function(e) {
        if (window.innerWidth > 768) return; // Only on mobile
        
        const endX = e.changedTouches[0].clientX;
        const endY = e.changedTouches[0].clientY;
        const diffX = endX - startX;
        const diffY = endY - startY;
        
        // Check if it's a horizontal swipe
        if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 50) {
            const overlay = document.querySelector('.mobile-overlay');
            
            if (diffX > 0 && startX < 50) {
                // Swipe right from left edge - open sidebar
                if (!sidebar.classList.contains('open')) {
                    toggleMobileSidebar(sidebar, overlay);
                }
            } else if (diffX < 0 && sidebar.classList.contains('open')) {
                // Swipe left - close sidebar
                closeMobileSidebar(sidebar, overlay);
            }
        }
    }, { passive: true });
}

/* ===== WINDOW RESIZE HANDLER ===== */
function initResizeHandler() {
    let resizeTimer;
    
    window.addEventListener('resize', function() {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function() {
            handleWindowResize();
        }, 250);
    });
}

function handleWindowResize() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.mobile-overlay');
    
    // Close mobile sidebar if window becomes larger
    if (window.innerWidth > 768 && sidebar && sidebar.classList.contains('open')) {
        closeMobileSidebar(sidebar, overlay);
    }
    
    // Update table responsiveness
    updateTableResponsiveness();
    
    // Update font sizes for better readability
    updateResponsiveFontSizes();
}

function updateTableResponsiveness() {
    const tables = document.querySelectorAll('table');
    
    tables.forEach(table => {
        if (window.innerWidth <= 767) {
            table.classList.add('table-responsive-stack');
        } else {
            table.classList.remove('table-responsive-stack');
        }
    });
}

function updateResponsiveFontSizes() {
    const root = document.documentElement;
    
    if (window.innerWidth <= 575) {
        root.style.fontSize = '14px';
    } else if (window.innerWidth <= 767) {
        root.style.fontSize = '15px';
    } else {
        root.style.fontSize = '16px';
    }
}

/* ===== ORIENTATION CHANGE HANDLER ===== */
function initOrientationHandler() {
    window.addEventListener('orientationchange', function() {
        setTimeout(function() {
            handleWindowResize();
            
            // Force repaint to fix potential layout issues
            document.body.style.display = 'none';
            document.body.offsetHeight; // Trigger reflow
            document.body.style.display = '';
        }, 100);
    });
}

/* ===== UTILITY FUNCTIONS ===== */

// Check if device is mobile
function isMobile() {
    return window.innerWidth <= 768;
}

// Check if device is tablet
function isTablet() {
    return window.innerWidth > 768 && window.innerWidth <= 1024;
}

// Check if device is desktop
function isDesktop() {
    return window.innerWidth > 1024;
}

// Get current breakpoint
function getCurrentBreakpoint() {
    const width = window.innerWidth;
    
    if (width <= 575) return 'xs';
    if (width <= 767) return 'sm';
    if (width <= 991) return 'md';
    if (width <= 1199) return 'lg';
    return 'xl';
}

// Smooth scroll to element
function smoothScrollTo(element) {
    if (element) {
        element.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
}

// Debounce function for performance
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

// Grid optimization for responsive layouts
function initGridOptimization() {
    const gridContainers = document.querySelectorAll('.app, .grid-container, .content-grid');
    
    function optimizeGridLayout() {
        const screenWidth = window.innerWidth;
        
        gridContainers.forEach(container => {
            if (screenWidth <= 575) {
                // Mobile: Single column
                container.style.gridTemplateColumns = '1fr';
                container.style.gridTemplateAreas = '"header" "sidebar" "main" "footer"';
            } else if (screenWidth <= 768) {
                // Tablet: Collapsed sidebar
                container.style.gridTemplateColumns = '1fr';
                container.style.gridTemplateAreas = '"header" "main" "footer"';
            } else if (screenWidth <= 992) {
                // Small desktop: Narrow sidebar
                container.style.gridTemplateColumns = '240px 1fr';
                container.style.gridTemplateAreas = '"header header" "sidebar main" "footer footer"';
            } else {
                // Large desktop: Full sidebar
                container.style.gridTemplateColumns = '280px 1fr';
                container.style.gridTemplateAreas = '"header header" "sidebar main" "footer footer"';
            }
        });
    }
    
    // Apply on load and resize
    optimizeGridLayout();
    window.addEventListener('resize', debounce(optimizeGridLayout, 250));
}

// Flexbox enhancements for better responsive behavior
function initFlexboxEnhancements() {
    const flexContainers = document.querySelectorAll('.flex-container, .btn-group, .form-actions, .modal-footer');
    
    function optimizeFlexLayout() {
        const screenWidth = window.innerWidth;
        
        flexContainers.forEach(container => {
            if (screenWidth <= 575) {
                // Mobile: Stack vertically
                container.style.flexDirection = 'column';
                container.style.gap = '0.5rem';
                
                // Make buttons full width on mobile
                const buttons = container.querySelectorAll('.btn, button');
                buttons.forEach(btn => {
                    btn.style.width = '100%';
                    btn.style.margin = '0';
                });
            } else {
                // Desktop: Horizontal layout
                container.style.flexDirection = 'row';
                container.style.gap = '1rem';
                
                // Reset button widths
                const buttons = container.querySelectorAll('.btn, button');
                buttons.forEach(btn => {
                    btn.style.width = 'auto';
                });
            }
        });
    }
    
    optimizeFlexLayout();
    window.addEventListener('resize', debounce(optimizeFlexLayout, 250));
}

// Advanced responsive features
function initAdvancedResponsive() {
    // Enhanced table responsiveness with data labels
    function enhanceTableResponsiveness() {
        const tables = document.querySelectorAll('table');
        
        tables.forEach(table => {
            if (!table.closest('.table-responsive')) {
                const wrapper = document.createElement('div');
                wrapper.className = 'table-responsive';
                table.parentNode.insertBefore(wrapper, table);
                wrapper.appendChild(table);
            }
            
            // Add data labels for mobile view
            const headers = table.querySelectorAll('thead th');
            const rows = table.querySelectorAll('tbody tr');
            
            rows.forEach(row => {
                const cells = row.querySelectorAll('td');
                cells.forEach((cell, index) => {
                    if (headers[index]) {
                        cell.setAttribute('data-label', headers[index].textContent.trim());
                    }
                });
            });
        });
    }
    
    // Form optimization for mobile
    function optimizeForms() {
        const forms = document.querySelectorAll('form');
        
        forms.forEach(form => {
            const screenWidth = window.innerWidth;
            
            if (screenWidth <= 575) {
                form.classList.add('form-mobile');
                
                // Stack form groups vertically
                const formGroups = form.querySelectorAll('.form-group, .admin-modal-form-group');
                formGroups.forEach(group => {
                    group.style.flexDirection = 'column';
                    group.style.alignItems = 'flex-start';
                });
                
                // Make inputs full width
                const inputs = form.querySelectorAll('input, select, textarea');
                inputs.forEach(input => {
                    input.style.width = '100%';
                });
            } else {
                form.classList.remove('form-mobile');
            }
        });
    }
    
    // Card layout optimization
    function optimizeCards() {
        const cards = document.querySelectorAll('.card, .content-section, .admin-modal-content');
        const screenWidth = window.innerWidth;
        
        cards.forEach(card => {
            if (screenWidth <= 575) {
                card.style.margin = '0.5rem';
                card.style.padding = '1rem';
                card.style.borderRadius = '8px';
            } else {
                card.style.margin = '';
                card.style.padding = '';
                card.style.borderRadius = '';
            }
        });
    }
    
    // Navigation optimization
    function optimizeNavigation() {
        const navTabs = document.querySelectorAll('.nav-tabs, .tab-container');
        const screenWidth = window.innerWidth;
        
        navTabs.forEach(nav => {
            if (screenWidth <= 767) {
                nav.style.flexDirection = 'column';
                
                const tabs = nav.querySelectorAll('.nav-link, .tab');
                tabs.forEach(tab => {
                    tab.style.textAlign = 'center';
                    tab.style.borderRadius = '0';
                });
            } else {
                nav.style.flexDirection = 'row';
            }
        });
    }
    
    // Apply optimizations
    enhanceTableResponsiveness();
    optimizeForms();
    optimizeCards();
    optimizeNavigation();
    
    // Re-apply on window resize
    window.addEventListener('resize', debounce(() => {
        optimizeForms();
        optimizeCards();
        optimizeNavigation();
    }, 250));
}

// Typography and spacing optimization
function initTypographyOptimization() {
    function optimizeTypography() {
        const screenWidth = window.innerWidth;
        
        // Dynamic font size adjustment
        if (screenWidth <= 480) {
            document.documentElement.style.fontSize = '14px';
        } else if (screenWidth <= 768) {
            document.documentElement.style.fontSize = '15px';
        } else {
            document.documentElement.style.fontSize = '16px';
        }
        
        // Optimize button spacing
        const buttons = document.querySelectorAll('.btn, .btn-edit, .btn-delete, button');
        buttons.forEach(btn => {
            if (screenWidth <= 768) {
                btn.style.minHeight = '44px';
                btn.style.padding = '12px 20px';
                btn.style.fontSize = '16px';
            }
        });
        
        // Optimize form controls
        const formControls = document.querySelectorAll('input, select, textarea');
        formControls.forEach(control => {
            if (screenWidth <= 768) {
                control.style.fontSize = '16px';
                control.style.padding = '12px 15px';
                control.style.minHeight = '44px';
            }
        });
        
        // Optimize touch targets
        const clickableElements = document.querySelectorAll('a, button, .btn, .clickable');
        clickableElements.forEach(element => {
            if (screenWidth <= 768) {
                element.style.minHeight = '44px';
                element.style.minWidth = '44px';
            }
        });
    }
    
    optimizeTypography();
    window.addEventListener('resize', debounce(optimizeTypography, 250));
}

// Enhanced mobile spacing optimization
function initSpacingOptimization() {
    function optimizeSpacing() {
        const screenWidth = window.innerWidth;
        
        if (screenWidth <= 768) {
            // Optimize container spacing
            const containers = document.querySelectorAll('.container, .content-section');
            containers.forEach(container => {
                container.style.paddingLeft = '15px';
                container.style.paddingRight = '15px';
            });
            
            // Optimize card spacing
            const cards = document.querySelectorAll('.card, .card-body');
            cards.forEach(card => {
                card.style.padding = '1rem';
                card.style.marginBottom = '1rem';
            });
            
            // Optimize form spacing
            const formGroups = document.querySelectorAll('.form-group');
            formGroups.forEach(group => {
                group.style.marginBottom = '1.2rem';
            });
            
            // Optimize table spacing
            const tableCells = document.querySelectorAll('.table td, .table th');
            tableCells.forEach(cell => {
                cell.style.padding = '0.5rem';
                cell.style.fontSize = '0.9rem';
            });
        }
    }
    
    optimizeSpacing();
    window.addEventListener('resize', debounce(optimizeSpacing, 250));
}

// Enhanced button group optimization
function initButtonGroupOptimization() {
    function optimizeButtonGroups() {
        const screenWidth = window.innerWidth;
        const buttonGroups = document.querySelectorAll('.btn-group, .action-buttons');
        
        buttonGroups.forEach(group => {
            if (screenWidth <= 768) {
                group.style.display = 'flex';
                group.style.flexDirection = 'column';
                group.style.gap = '10px';
                
                const buttons = group.querySelectorAll('.btn, button');
                buttons.forEach(btn => {
                    btn.style.width = '100%';
                    btn.style.margin = '0';
                });
            } else {
                group.style.flexDirection = 'row';
                group.style.gap = '1rem';
                
                const buttons = group.querySelectorAll('.btn, button');
                buttons.forEach(btn => {
                    btn.style.width = 'auto';
                });
            }
        });
    }
    
    optimizeButtonGroups();
    window.addEventListener('resize', debounce(optimizeButtonGroups, 250));
}

// Throttle function for performance
function throttle(func, limit) {
    let inThrottle;
    return function() {
        const args = arguments;
        const context = this;
        if (!inThrottle) {
            func.apply(context, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

/* ===== FORM ENHANCEMENTS ===== */
function enhanceFormsForMobile() {
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        // Add mobile-friendly classes
        if (isMobile()) {
            form.classList.add('mobile-form');
        }
        
        // Enhance input focus for mobile
        const inputs = form.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.addEventListener('focus', function() {
                if (isMobile()) {
                    // Scroll input into view on mobile
                    setTimeout(() => {
                        this.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    }, 300);
                }
            });
        });
    });
}

// Initialize form enhancements
document.addEventListener('DOMContentLoaded', enhanceFormsForMobile);

/* ===== MODAL ENHANCEMENTS ===== */
function enhanceModalsForMobile() {
    const modals = document.querySelectorAll('.modal, [class*="modal"]');
    
    modals.forEach(modal => {
        if (isMobile()) {
            modal.classList.add('mobile-modal');
        }
    });
}

// Initialize modal enhancements
document.addEventListener('DOMContentLoaded', enhanceModalsForMobile);

/* ===== EXPORT FUNCTIONS FOR GLOBAL USE ===== */
// Forms, Tables and Modals Optimization
function initFormsTablesModalsOptimization() {
    function optimizeFormsTablesModals() {
        const screenWidth = window.innerWidth;
        
        // Table optimization
        const tables = document.querySelectorAll('table');
        tables.forEach(table => {
            if (screenWidth <= 480) {
                // Add stack class for very small screens
                table.classList.add('table-stack');
                
                // Add data-label attributes for mobile view
                const headers = table.querySelectorAll('thead th');
                const rows = table.querySelectorAll('tbody tr');
                
                rows.forEach(row => {
                    const cells = row.querySelectorAll('td');
                    cells.forEach((cell, index) => {
                        if (headers[index]) {
                            cell.setAttribute('data-label', headers[index].textContent.trim());
                        }
                    });
                });
            } else {
                table.classList.remove('table-stack');
            }
            
            // Wrap table in container for horizontal scroll
            if (!table.parentElement.classList.contains('table-container')) {
                const wrapper = document.createElement('div');
                wrapper.className = 'table-container';
                table.parentNode.insertBefore(wrapper, table);
                wrapper.appendChild(table);
            }
        });
        
        // Form optimization
        const forms = document.querySelectorAll('form');
        forms.forEach(form => {
            if (screenWidth <= 768) {
                form.classList.add('mobile-form');
                
                // Optimize form groups
                const formGroups = form.querySelectorAll('.form-group, .admin-modal-form-group');
                formGroups.forEach(group => {
                    group.style.flexDirection = 'column';
                    group.style.alignItems = 'flex-start';
                });
                
                // Optimize inputs
                const inputs = form.querySelectorAll('input, select, textarea');
                inputs.forEach(input => {
                    input.style.width = '100%';
                    input.style.fontSize = '16px'; // Prevent zoom on iOS
                });
            } else {
                form.classList.remove('mobile-form');
            }
        });
        
        // Modal optimization
        const modals = document.querySelectorAll('.modal-overlay, .admin-modal-overlay');
        modals.forEach(modal => {
            if (screenWidth <= 768) {
                modal.classList.add('mobile-modal');
                
                // Optimize modal content
                const modalContent = modal.querySelector('.modal-content-wrapper, .admin-modal-content');
                if (modalContent) {
                    modalContent.style.width = '100%';
                    modalContent.style.maxWidth = '100%';
                    modalContent.style.margin = '0';
                    
                    if (screenWidth <= 480) {
                        modalContent.style.height = '100vh';
                        modalContent.style.borderRadius = '0';
                    }
                }
                
                // Stack modal actions
                const modalActions = modal.querySelector('.modal-actions, .admin-modal-actions');
                if (modalActions) {
                    modalActions.style.flexDirection = 'column';
                    
                    const buttons = modalActions.querySelectorAll('.btn');
                    buttons.forEach(btn => {
                        btn.style.width = '100%';
                        btn.style.margin = '0.25rem 0';
                    });
                }
            } else {
                modal.classList.remove('mobile-modal');
            }
        });
        
        // Enhance touch targets
        if (screenWidth <= 768) {
            const clickableElements = document.querySelectorAll('button, .btn, a, input[type="submit"], input[type="button"]');
            clickableElements.forEach(element => {
                const computedStyle = window.getComputedStyle(element);
                const height = parseInt(computedStyle.height);
                const width = parseInt(computedStyle.width);
                
                if (height < 44) {
                    element.style.minHeight = '44px';
                }
                if (width < 44 && element.tagName === 'BUTTON') {
                    element.style.minWidth = '44px';
                }
            });
        }
    }
    
    // Initial optimization
    optimizeFormsTablesModals();
    
    // Re-optimize on window resize
    window.addEventListener('resize', debounce(optimizeFormsTablesModals, 250));
    
    // Re-optimize when new content is added dynamically
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.type === 'childList' && mutation.addedNodes.length > 0) {
                // Check if any added nodes contain forms, tables, or modals
                mutation.addedNodes.forEach(function(node) {
                    if (node.nodeType === 1) { // Element node
                        const hasForms = node.querySelectorAll && node.querySelectorAll('form').length > 0;
                        const hasTables = node.querySelectorAll && node.querySelectorAll('table').length > 0;
                        const hasModals = node.querySelectorAll && node.querySelectorAll('[class*="modal"]').length > 0;
                        
                        if (hasForms || hasTables || hasModals) {
                            setTimeout(optimizeFormsTablesModals, 100);
                        }
                    }
                });
            }
        });
    });
    
    // Start observing
    observer.observe(document.body, {
        childList: true,
        subtree: true
    });
}

window.ResponsiveUtils = {
    isMobile,
    isTablet,
    isDesktop,
    getCurrentBreakpoint,
    smoothScrollTo,
    debounce,
    throttle,
    toggleMobileSidebar,
    closeMobileSidebar
};
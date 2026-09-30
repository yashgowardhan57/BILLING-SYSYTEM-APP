package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AuditLogEntity
import com.example.data.BackupRecordEntity
import com.example.data.BusinessSettingsEntity
import com.example.data.CartItem
import com.example.data.CategoryEntity
import com.example.data.ColorEntity
import com.example.data.CustomerEntity
import com.example.data.DailyClosingEntity
import com.example.data.DatabaseSeeder
import com.example.data.ExpenseCategoryEntity
import com.example.data.ExpenseEntity
import com.example.data.InventoryMovementEntity
import com.example.data.PaymentTransactionEntity
import com.example.data.PosRepository
import com.example.data.ProductEntity
import com.example.data.ProductVariantDraft
import com.example.data.ProductVariantEntity
import com.example.data.ReturnEntity
import com.example.data.ReturnItemEntity
import com.example.data.ReturnItemRequest
import com.example.data.SaleEntity
import com.example.data.SaleItemEntity
import com.example.data.SizeEntity
import com.example.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PosViewModel(application: Application) : AndroidViewModel(application) {

    val db = AppDatabase.getDatabase(application)
    val repository = PosRepository(db)

    val currentUser = repository.currentUser
    val connectionState = repository.connectionState
    val settings = repository.getSettings().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Catalog & Variants
    val categories = repository.getCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sizes = repository.getSizes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val colors = repository.getColors().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allVariants = repository.getVariants().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val lowStockVariants = repository.getLowStockVariants().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val outOfStockVariants = repository.getOutOfStockVariants().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val products = repository.getProducts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Billing POS State
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _billingSearchQuery = MutableStateFlow("")
    val billingSearchQuery: StateFlow<String> = _billingSearchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null) // null = ALL
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    // Filtered variants for billing search/picker
    val filteredBillingVariants = combine(
        allVariants,
        _billingSearchQuery,
        _selectedCategoryId,
        products
    ) { variants, query, catId, prods ->
        val prodCategoryMap = prods.associate { it.id to it.categoryId }
        variants.filter { v ->
            val matchCategory = catId == null || prodCategoryMap[v.productId] == catId
            val q = query.trim().lowercase()
            val matchQuery = q.isEmpty() ||
                v.productName.lowercase().contains(q) ||
                v.sku.lowercase().contains(q) ||
                (v.barcode?.lowercase()?.contains(q) == true) ||
                v.sizeName.lowercase().contains(q) ||
                v.colorName.lowercase().contains(q)
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Invoice receipt preview modal state
    private val _activeReceiptModal = MutableStateFlow<ReceiptModalData?>(null)
    val activeReceiptModal: StateFlow<ReceiptModalData?> = _activeReceiptModal.asStateFlow()

    // Toast/Snackbar notifications
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // 7-day restricted sales for Staff
    val staff7DaysSales = repository.getStaff7DaysSales().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All sales for Admin
    val allSales = repository.getAllSales().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pendingSales = repository.getPendingSales().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTransactions = repository.getAllPaymentTransactions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allMovements = repository.getInventoryMovements().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allExpenses = repository.getExpenses().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val expenseCategories = repository.getExpenseCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allCustomers = repository.getCustomers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allStaff = repository.getAllStaff().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allReturns = repository.getReturns().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allExchanges = repository.getExchanges().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val auditLogs = repository.getAuditLogs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val backupRecords = repository.getBackupRecords().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allDailyClosings = db.dailyClosingDao().getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Dashboard Date Filter
    private val _dashboardPeriod = MutableStateFlow(PeriodFilter.TODAY)
    val dashboardPeriod: StateFlow<PeriodFilter> = _dashboardPeriod.asStateFlow()

    init {
        viewModelScope.launch {
            DatabaseSeeder.seedIfEmpty(db)
            // Remove demo data on clean startup
            DatabaseSeeder.purgeAllDemoData(db)
            // By default log in as Staff for instant retail billing access
            var staff = db.userDao().getUserByMobile("9876500001")
            if (staff == null) {
                staff = db.userDao().getUserById("user-staff-1")
            }
            if (staff != null) {
                repository.setCurrentUser(staff)
            }
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    // Auth methods
    fun loginStaff(mobile: String, pin: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.loginStaff(mobile, pin)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun loginAdmin(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.loginAdmin(email, pass)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun switchUser(user: UserEntity) {
        repository.setCurrentUser(user)
        showMessage("Switched to ${user.fullName} (${user.role})")
    }

    fun switchToStaff() {
        viewModelScope.launch {
            val staff = db.userDao().getAllStaff().first().firstOrNull()
                ?: db.userDao().getUserByMobile("9876500001")
            if (staff != null) {
                repository.setCurrentUser(staff)
                showMessage("Switched to Staff Portal (${staff.fullName})")
            } else {
                repository.logout()
            }
        }
    }

    fun logout() {
        switchToStaff()
    }

    fun toggleConnectionSimulation() {
        val next = when (connectionState.value) {
            "ONLINE" -> "OFFLINE"
            "OFFLINE" -> "SYNCING"
            "SYNCING" -> "SYNCED"
            else -> "ONLINE"
        }
        repository.setConnectionState(next)
        showMessage("Connection State: $next")
    }

    // Billing POS Methods
    fun setBillingSearchQuery(q: String) {
        _billingSearchQuery.value = q
    }

    fun selectCategory(catId: String?) {
        _selectedCategoryId.value = catId
    }

    fun addToCart(variant: ProductVariantEntity, quantity: Double = 1.0) {
        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.variant.id == variant.id }
        if (index >= 0) {
            val existing = currentList[index]
            val newQty = existing.quantity + quantity
            if (newQty > variant.stockQuantity) {
                showMessage("Cannot add more: Max available stock is ${variant.stockQuantity}")
                return
            }
            currentList[index] = existing.copy(quantity = newQty)
        } else {
            if (quantity > variant.stockQuantity) {
                showMessage("Cannot add: Max available stock is ${variant.stockQuantity}")
                return
            }
            currentList.add(CartItem(variant = variant, quantity = quantity))
        }
        _cart.value = currentList
        showMessage("Added ${variant.productName} (${variant.sizeName}/${variant.colorName})")
    }

    fun updateCartQuantity(variantId: String, newQty: Double) {
        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.variant.id == variantId }
        if (index >= 0) {
            if (newQty <= 0) {
                currentList.removeAt(index)
            } else {
                val variant = currentList[index].variant
                if (newQty > variant.stockQuantity) {
                    showMessage("Stock limit: Only ${variant.stockQuantity} available")
                    return
                }
                currentList[index] = currentList[index].copy(quantity = newQty)
            }
            _cart.value = currentList
        }
    }

    fun removeFromCart(variantId: String) {
        _cart.value = _cart.value.filter { it.variant.id != variantId }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun completeCheckout(
        paymentMethod: String, // CASH, ONLINE, PENDING
        paidAmountInput: Double,
        discountAmount: Double,
        customerName: String?,
        customerMobile: String?,
        notes: String?,
        onSuccess: (SaleEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.completeSale(
                cartItems = _cart.value,
                paymentMethod = paymentMethod,
                paidAmountInput = paidAmountInput,
                discountAmount = discountAmount,
                customerName = customerName,
                customerMobile = customerMobile,
                notes = notes
            )
            if (result.isSuccess) {
                val sale = result.getOrThrow()
                val items = db.saleItemDao().getItemsForSaleSync(sale.id)
                val receiptText = repository.formatThermalReceipt(sale, items)
                _activeReceiptModal.value = ReceiptModalData(sale, items, receiptText)
                _cart.value = emptyList()
                onSuccess(sale)
            } else {
                onError(result.exceptionOrNull()?.message ?: "Checkout failed")
            }
        }
    }

    fun openReceiptModal(sale: SaleEntity) {
        viewModelScope.launch {
            val items = db.saleItemDao().getItemsForSaleSync(sale.id)
            val receiptText = repository.formatThermalReceipt(sale, items)
            _activeReceiptModal.value = ReceiptModalData(sale, items, receiptText)
        }
    }

    fun closeReceiptModal() {
        _activeReceiptModal.value = null
    }

    // Pending payment collection
    fun collectPendingPayment(saleId: String, amount: Double, method: String, notes: String?, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.recordPendingPaymentCollection(saleId, amount, method, notes)
            if (res.isSuccess) {
                showMessage("Collected ₹${amount.toInt()} successfully.")
                onComplete(true, null)
            } else {
                onComplete(false, res.exceptionOrNull()?.message ?: "Collection failed")
            }
        }
    }

    // Returns
    fun processReturn(
        originalSaleId: String,
        returnItems: List<ReturnItemRequest>,
        refundMethod: String,
        reason: String,
        notes: String?,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.processReturn(originalSaleId, returnItems, refundMethod, reason, notes)
            if (res.isSuccess) {
                showMessage("Return processed successfully: ${res.getOrThrow().returnNumber}")
                onComplete(true, null)
            } else {
                onComplete(false, res.exceptionOrNull()?.message ?: "Return failed")
            }
        }
    }

    // Exchanges
    fun processExchange(
        originalSaleId: String,
        returnedSaleItemId: String,
        returnedQuantity: Double,
        replacementVariantId: String,
        replacementQuantity: Double,
        notes: String?,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.processExchange(
                originalSaleId, returnedSaleItemId, returnedQuantity,
                replacementVariantId, replacementQuantity, notes
            )
            if (res.isSuccess) {
                val ex = res.getOrThrow()
                val diffMsg = if (ex.customerDifference > 0) "Customer pays ₹${ex.customerDifference.toInt()}"
                else if (ex.refundDifference > 0) "Refund ₹${ex.refundDifference.toInt()}"
                else "Even exchange (₹0 difference)"
                showMessage("Exchange ${ex.exchangeNumber} complete! $diffMsg")
                onComplete(true, null)
            } else {
                onComplete(false, res.exceptionOrNull()?.message ?: "Exchange failed")
            }
        }
    }

    // Inventory Adjustment (Admin)
    fun adjustInventory(variantId: String, delta: Double, reason: String, notes: String?, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.adjustInventory(variantId, delta, reason, notes)
            if (res.isSuccess) {
                showMessage("Inventory updated to ${res.getOrThrow().stockQuantity}")
                onComplete(true, null)
            } else {
                onComplete(false, res.exceptionOrNull()?.message ?: "Adjustment failed")
            }
        }
    }

    // Expenses (Admin)
    fun addExpense(category: String, amount: Double, desc: String, method: String, notes: String?, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.addExpense(category, amount, desc, method, notes)
            if (res.isSuccess) {
                showMessage("Expense of ₹${amount.toInt()} recorded.")
                onComplete(true, null)
            } else {
                onComplete(false, res.exceptionOrNull()?.message ?: "Expense addition failed")
            }
        }
    }

    fun voidExpense(expenseId: String, reason: String) {
        viewModelScope.launch {
            val res = repository.voidExpense(expenseId, reason)
            if (res.isSuccess) showMessage("Expense voided.")
            else showMessage(res.exceptionOrNull()?.message ?: "Failed to void expense")
        }
    }

    // Daily Closing (Admin)
    fun setDashboardPeriod(period: PeriodFilter) {
        _dashboardPeriod.value = period
    }

    fun executeDailyClosing(dateStr: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.calculateAndCloseDay(dateStr)
            if (res.isSuccess) {
                showMessage("Day $dateStr marked CLOSED successfully.")
                onComplete(true, null)
            } else {
                onComplete(false, res.exceptionOrNull()?.message ?: "Daily closing failed")
            }
        }
    }

    // Backup & Restore
    fun triggerBackup() {
        viewModelScope.launch {
            val res = repository.createBackup()
            if (res.isSuccess) {
                showMessage("Backup completed! Saved ${res.getOrThrow().recordCount} records.")
            } else {
                showMessage(res.exceptionOrNull()?.message ?: "Backup failed")
            }
        }
    }

    fun triggerRestore(backupId: String) {
        viewModelScope.launch {
            val res = repository.restoreBackup(backupId)
            if (res.isSuccess) {
                showMessage("Backup restored successfully!")
            } else {
                showMessage(res.exceptionOrNull()?.message ?: "Restore failed")
            }
        }
    }

    // Settings
    fun updatePrinterPaperWidth(width: Int) {
        viewModelScope.launch {
            val current = settings.value ?: return@launch
            repository.updateSettings(current.copy(printerPaperWidth = width))
            showMessage("Printer width set to ${width}mm")
        }
    }

    fun updateReorderThreshold(threshold: Double) {
        viewModelScope.launch {
            val current = settings.value ?: return@launch
            repository.updateSettings(current.copy(lowStockThreshold = threshold))
            showMessage("Reorder threshold set to $threshold")
        }
    }

    fun updateShopDetails(name: String, addr1: String, addr2: String, phone: String, returnPolicy: String) {
        viewModelScope.launch {
            val current = settings.value ?: return@launch
            repository.updateSettings(
                current.copy(
                    businessName = name,
                    addressLine1 = addr1,
                    addressLine2 = addr2,
                    phone = phone,
                    returnPolicy = returnPolicy
                )
            )
            showMessage("Shop details updated.")
        }
    }

    // Staff Edit (Admin only)
    fun updateStaff(
        staffId: String,
        fullName: String,
        email: String,
        mobile: String,
        pin: String,
        isActive: Boolean,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.updateStaff(staffId, fullName, email, mobile, pin, isActive)
            if (res.isSuccess) {
                showMessage("Staff updated: $fullName (Email: $email, Mobile: $mobile)")
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to update staff"
                showMessage(err)
                onComplete(false, err)
            }
        }
    }

    fun createStaff(
        fullName: String,
        email: String?,
        mobile: String,
        pin: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.createStaff(fullName, email, mobile, pin)
            if (res.isSuccess) {
                showMessage("Staff account created: $fullName")
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to create staff account"
                showMessage(err)
                onComplete(false, err)
            }
        }
    }

    // Change Admin Credentials (Email, Name, Mobile, Password)
    fun changeAdminCredentials(
        email: String,
        fullName: String,
        mobile: String,
        currentPass: String,
        newPass: String?,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val admin = currentUser.value ?: return@launch
            val res = repository.changeAdminCredentials(admin.id, email, fullName, mobile, currentPass, newPass)
            if (res.isSuccess) {
                showMessage("Admin profile updated successfully (Email: $email)")
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to update admin profile"
                showMessage(err)
                onComplete(false, err)
            }
        }
    }

    // Change Admin Password
    fun changeAdminPassword(
        currentPass: String,
        newPass: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val admin = currentUser.value ?: return@launch
            val res = repository.changeAdminPassword(admin.id, currentPass, newPass)
            if (res.isSuccess) {
                showMessage("Admin password changed successfully.")
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to change password"
                showMessage(err)
                onComplete(false, err)
            }
        }
    }

    // Product & Variant Edit and Delete (Admin only)
    fun updateProduct(
        productId: String,
        name: String,
        description: String?,
        categoryId: String,
        categoryName: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.updateProduct(productId, name, description, categoryId, categoryName)
            if (res.isSuccess) {
                showMessage("Product '$name' updated.")
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to update product"
                showMessage(err)
                onComplete(false, err)
            }
        }
    }

    fun loadVariantsForProduct(productId: String, onResult: (List<ProductVariantEntity>) -> Unit) {
        viewModelScope.launch {
            val list = repository.getVariantsForProductSync(productId)
            onResult(list)
        }
    }

    fun updateProductAndVariants(
        productId: String,
        name: String,
        description: String?,
        categoryId: String,
        categoryName: String,
        updatedVariants: List<ProductVariantEntity>,
        newVariants: List<ProductVariantDraft> = emptyList(),
        deletedVariantIds: List<String> = emptyList(),
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.updateProductAndVariants(
                productId = productId,
                name = name,
                description = description,
                categoryId = categoryId,
                categoryName = categoryName,
                updatedVariants = updatedVariants,
                newVariants = newVariants,
                deletedVariantIds = deletedVariantIds
            )
            if (res.isSuccess) {
                showMessage("Product '$name' & variants updated successfully.")
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to update product and variants"
                showMessage(err)
                onComplete(false, err)
            }
        }
    }

    fun updateVariant(variant: ProductVariantEntity, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateVariant(variant)
                showMessage("Variant ${variant.sku} updated.")
                onComplete(true, null)
            } catch (e: Exception) {
                showMessage(e.message ?: "Failed to update variant")
                onComplete(false, e.message)
            }
        }
    }

    fun deleteProduct(productId: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.deleteProduct(productId)
            if (res.isSuccess) {
                showMessage("Product deleted.")
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to delete product"
                showMessage(err)
                onComplete(false, err)
            }
        }
    }

    fun deleteVariant(variantId: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.deleteVariant(variantId)
            if (res.isSuccess) {
                showMessage("Variant deleted.")
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to delete variant"
                showMessage(err)
                onComplete(false, err)
            }
        }
    }

    fun purgeAllDemoData(onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.purgeAllDemoData()
            if (res.isSuccess) {
                showMessage("All demo sales and expenses removed. Database clean.")
                onComplete(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to purge demo data"
                showMessage(err)
                onComplete(false, err)
            }
        }
    }

    // Sizes & Colors Management
    fun addSize(name: String, sortOrder: Int = 0, onComplete: ((SizeEntity?) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val size = repository.addSize(name, sortOrder)
                showMessage("Size '$name' added successfully")
                onComplete?.invoke(size)
            } catch (e: Exception) {
                showMessage("Error adding size: ${e.message}")
                onComplete?.invoke(null)
            }
        }
    }

    fun deleteSize(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteSize(id)
                showMessage("Size removed")
            } catch (e: Exception) {
                showMessage("Error deleting size: ${e.message}")
            }
        }
    }

    fun addColor(name: String, hexCode: String?, onComplete: ((ColorEntity?) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val color = repository.addColor(name, hexCode)
                showMessage("Color '$name' added successfully")
                onComplete?.invoke(color)
            } catch (e: Exception) {
                showMessage("Error adding color: ${e.message}")
                onComplete?.invoke(null)
            }
        }
    }

    fun deleteColor(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteColor(id)
                showMessage("Color removed")
            } catch (e: Exception) {
                showMessage("Error deleting color: ${e.message}")
            }
        }
    }

    // Categories Management
    fun addCategory(name: String, desc: String? = null, onComplete: ((CategoryEntity?) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val cat = repository.addCategory(name, desc)
                showMessage("Category '${cat.name}' added successfully")
                onComplete?.invoke(cat)
            } catch (e: Exception) {
                showMessage("Error adding category: ${e.message}")
                onComplete?.invoke(null)
            }
        }
    }

    fun deleteCategory(id: String, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(id)
                showMessage("Category deleted successfully")
                onComplete?.invoke(true)
            } catch (e: Exception) {
                showMessage("Error deleting category: ${e.message}")
                onComplete?.invoke(false)
            }
        }
    }

    // Firebase Cloud Database Integration
    fun testFirebaseConnection(url: String, apiKey: String? = null, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.testFirebaseConnection(url, apiKey)
            if (res.isSuccess) {
                onResult(true, res.getOrThrow())
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Firebase connection failed")
            }
        }
    }

    fun syncToFirebase(url: String, apiKey: String? = null, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.syncToFirebase(url, apiKey)
            if (res.isSuccess) {
                showMessage("Firebase Database synced successfully!")
                onComplete(true, res.getOrThrow())
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to sync to Firebase"
                showMessage(err)
                onComplete(false, err)
            }
        }
    }

    // Supabase Integration
    fun testSupabaseConnection(url: String, key: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.testSupabaseConnection(url, key)
            if (res.isSuccess) {
                onResult(true, res.getOrThrow())
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Connection failed")
            }
        }
    }

    fun saveSupabaseConfig(url: String, key: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.updateSupabaseCredentials(url, key)
            showMessage("Supabase configuration saved.")
            onComplete()
        }
    }
}

data class ReceiptModalData(
    val sale: SaleEntity,
    val items: List<SaleItemEntity>,
    val formattedThermalText: String
)

enum class PeriodFilter(val label: String) {
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    LAST_7_DAYS("7 Days"),
    LAST_30_DAYS("30 Days"),
    ALL_TIME("All Time")
}

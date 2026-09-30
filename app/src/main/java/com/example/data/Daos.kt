package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email AND isActive = 1 LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE mobile = :mobile AND isActive = 1 LIMIT 1")
    suspend fun getUserByMobile(mobile: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY role ASC, fullName ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'STAFF' ORDER BY fullName ASC")
    fun getAllStaff(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'ADMIN' LIMIT 1")
    suspend fun getAdminUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)
}

@Dao
interface BusinessSettingsDao {
    @Query("SELECT * FROM business_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<BusinessSettingsEntity?>

    @Query("SELECT * FROM business_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsOnce(): BusinessSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: BusinessSettingsEntity)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE status = 'ACTIVE' ORDER BY name ASC")
    fun getAllActive(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAll(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface SizeDao {
    @Query("SELECT * FROM sizes WHERE isActive = 1 ORDER BY sortOrder ASC, name ASC")
    fun getAllActive(): Flow<List<SizeEntity>>

    @Query("SELECT * FROM sizes ORDER BY sortOrder ASC, name ASC")
    fun getAll(): Flow<List<SizeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(size: SizeEntity)

    @Update
    suspend fun update(size: SizeEntity)

    @Query("DELETE FROM sizes WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface ColorDao {
    @Query("SELECT * FROM colors WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<ColorEntity>>

    @Query("SELECT * FROM colors ORDER BY name ASC")
    fun getAll(): Flow<List<ColorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(color: ColorEntity)

    @Update
    suspend fun update(color: ColorEntity)

    @Query("DELETE FROM colors WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE status = 'ACTIVE' ORDER BY name ASC")
    fun getAllActive(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}

@Dao
interface ProductVariantDao {
    @Query("SELECT * FROM product_variants WHERE status = 'ACTIVE' ORDER BY productName ASC, sku ASC")
    fun getAllActive(): Flow<List<ProductVariantEntity>>

    @Query("SELECT * FROM product_variants ORDER BY productName ASC, sku ASC")
    fun getAll(): Flow<List<ProductVariantEntity>>

    @Query("SELECT * FROM product_variants WHERE productId = :productId AND status = 'ACTIVE'")
    fun getByProductId(productId: String): Flow<List<ProductVariantEntity>>

    @Query("SELECT * FROM product_variants WHERE id = :id LIMIT 1")
    suspend fun getVariantById(id: String): ProductVariantEntity?

    @Query("SELECT * FROM product_variants WHERE sku = :sku LIMIT 1")
    suspend fun getBySku(sku: String): ProductVariantEntity?

    @Query("SELECT * FROM product_variants WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): ProductVariantEntity?

    @Query("""
        SELECT * FROM product_variants 
        WHERE status = 'ACTIVE' AND (
            productName LIKE '%' || :query || '%' OR 
            sku LIKE '%' || :query || '%' OR 
            barcode LIKE '%' || :query || '%' OR
            sizeName LIKE '%' || :query || '%' OR
            colorName LIKE '%' || :query || '%'
        )
        ORDER BY productName ASC
    """)
    fun searchVariants(query: String): Flow<List<ProductVariantEntity>>

    @Query("SELECT * FROM product_variants WHERE status = 'ACTIVE' AND stockQuantity <= reorderLevel AND stockQuantity > 0 ORDER BY stockQuantity ASC")
    fun getLowStockVariants(): Flow<List<ProductVariantEntity>>

    @Query("SELECT * FROM product_variants WHERE status = 'ACTIVE' AND stockQuantity <= 0 ORDER BY productName ASC")
    fun getOutOfStockVariants(): Flow<List<ProductVariantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(variant: ProductVariantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(variants: List<ProductVariantEntity>)

    @Update
    suspend fun update(variant: ProductVariantEntity)

    @Query("UPDATE product_variants SET stockQuantity = :newStock WHERE id = :variantId")
    suspend fun updateStock(variantId: String, newStock: Double)

    @Query("DELETE FROM product_variants WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM product_variants WHERE productId = :productId")
    suspend fun deleteByProductId(productId: String)

    @Query("DELETE FROM product_variants")
    suspend fun deleteAll()
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers WHERE isActive = 1 ORDER BY name ASC")
    fun getAll(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE mobile = :mobile LIMIT 1")
    suspend fun getByMobile(mobile: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR mobile LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: CustomerEntity)

    @Update
    suspend fun update(customer: CustomerEntity)
}

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY createdAt DESC")
    fun getAllSales(): Flow<List<SaleEntity>>

    // Strict 7-day restriction query for Staff
    @Query("SELECT * FROM sales WHERE businessDate >= :fromDate ORDER BY createdAt DESC")
    fun getSalesForLast7Days(fromDate: String): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE businessDate = :businessDate ORDER BY createdAt DESC")
    fun getSalesByDate(businessDate: String): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE businessDate = :businessDate ORDER BY createdAt DESC")
    suspend fun getSalesByDateSync(businessDate: String): List<SaleEntity>

    @Query("SELECT * FROM sales WHERE businessDate BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getSalesBetweenDates(startDate: String, endDate: String): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE id = :id LIMIT 1")
    suspend fun getSaleById(id: String): SaleEntity?

    @Query("SELECT * FROM sales WHERE invoiceNumber = :invoiceNumber LIMIT 1")
    suspend fun getSaleByInvoice(invoiceNumber: String): SaleEntity?

    @Query("SELECT * FROM sales WHERE pendingAmount > 0 AND status != 'VOID' ORDER BY createdAt DESC")
    fun getPendingSales(): Flow<List<SaleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sale: SaleEntity)

    @Update
    suspend fun update(sale: SaleEntity)

    @Query("DELETE FROM sales")
    suspend fun deleteAll()
}

@Dao
interface SaleItemDao {
    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    fun getItemsForSale(saleId: String): Flow<List<SaleItemEntity>>

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    suspend fun getItemsForSaleSync(saleId: String): List<SaleItemEntity>

    @Query("SELECT * FROM sale_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: String): SaleItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SaleItemEntity>)

    @Update
    suspend fun update(item: SaleItemEntity)

    @Query("DELETE FROM sale_items")
    suspend fun deleteAll()
}

@Dao
interface PaymentTransactionDao {
    @Query("SELECT * FROM payment_transactions WHERE saleId = :saleId ORDER BY createdAt ASC")
    fun getTransactionsForSale(saleId: String): Flow<List<PaymentTransactionEntity>>

    @Query("SELECT * FROM payment_transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<PaymentTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: PaymentTransactionEntity)

    @Query("DELETE FROM payment_transactions")
    suspend fun deleteAll()
}

@Dao
interface InventoryMovementDao {
    @Query("SELECT * FROM inventory_movements ORDER BY createdAt DESC")
    fun getAll(): Flow<List<InventoryMovementEntity>>

    @Query("SELECT * FROM inventory_movements WHERE variantId = :variantId ORDER BY createdAt DESC")
    fun getMovementsForVariant(variantId: String): Flow<List<InventoryMovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movement: InventoryMovementEntity)

    @Query("DELETE FROM inventory_movements")
    suspend fun deleteAll()
}

@Dao
interface ReturnDao {
    @Query("SELECT * FROM returns ORDER BY createdAt DESC")
    fun getAllReturns(): Flow<List<ReturnEntity>>

    @Query("SELECT * FROM returns WHERE id = :id LIMIT 1")
    suspend fun getReturnById(id: String): ReturnEntity?

    @Query("SELECT * FROM returns WHERE originalSaleId = :saleId ORDER BY createdAt DESC")
    fun getReturnsForSale(saleId: String): Flow<List<ReturnEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(returnEntity: ReturnEntity)

    @Query("DELETE FROM returns")
    suspend fun deleteAll()
}

@Dao
interface ReturnItemDao {
    @Query("SELECT * FROM return_items WHERE returnId = :returnId")
    fun getItemsForReturn(returnId: String): Flow<List<ReturnItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ReturnItemEntity>)

    @Query("DELETE FROM return_items")
    suspend fun deleteAll()
}

@Dao
interface ExchangeDao {
    @Query("SELECT * FROM exchanges ORDER BY createdAt DESC")
    fun getAllExchanges(): Flow<List<ExchangeEntity>>

    @Query("SELECT * FROM exchanges WHERE id = :id LIMIT 1")
    suspend fun getExchangeById(id: String): ExchangeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exchange: ExchangeEntity)

    @Query("DELETE FROM exchanges")
    suspend fun deleteAll()
}

@Dao
interface ExchangeItemDao {
    @Query("SELECT * FROM exchange_items WHERE exchangeId = :exchangeId")
    fun getItemsForExchange(exchangeId: String): Flow<List<ExchangeItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ExchangeItemEntity>)

    @Query("DELETE FROM exchange_items")
    suspend fun deleteAll()
}

@Dao
interface ExpenseCategoryDao {
    @Query("SELECT * FROM expense_categories WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<ExpenseCategoryEntity>>

    @Query("SELECT * FROM expense_categories ORDER BY name ASC")
    fun getAll(): Flow<List<ExpenseCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: ExpenseCategoryEntity)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY expenseDate DESC, createdAt DESC")
    fun getAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE expenseDate BETWEEN :startDate AND :endDate AND status = 'ACTIVE' ORDER BY expenseDate DESC")
    fun getExpensesBetweenDates(startDate: String, endDate: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE expenseDate = :businessDate AND status = 'ACTIVE'")
    fun getExpensesByDate(businessDate: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE expenseDate = :businessDate")
    suspend fun getExpensesByDateSync(businessDate: String): List<ExpenseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()
}

@Dao
interface DailyClosingDao {
    @Query("SELECT * FROM daily_closings ORDER BY businessDate DESC")
    fun getAll(): Flow<List<DailyClosingEntity>>

    @Query("SELECT * FROM daily_closings WHERE businessDate = :businessDate LIMIT 1")
    suspend fun getClosingForDate(businessDate: String): DailyClosingEntity?

    @Query("SELECT * FROM daily_closings WHERE businessDate = :businessDate LIMIT 1")
    fun getClosingFlowForDate(businessDate: String): Flow<DailyClosingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(closing: DailyClosingEntity)

    @Update
    suspend fun update(closing: DailyClosingEntity)

    @Query("DELETE FROM daily_closings")
    suspend fun deleteAll()
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY createdAt DESC LIMIT 200")
    fun getAll(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AuditLogEntity)
}

@Dao
interface BackupRecordDao {
    @Query("SELECT * FROM backup_records ORDER BY createdAt DESC")
    fun getAll(): Flow<List<BackupRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(backup: BackupRecordEntity)
}

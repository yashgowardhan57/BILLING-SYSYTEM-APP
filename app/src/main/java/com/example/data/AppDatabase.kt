package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        BusinessSettingsEntity::class,
        CategoryEntity::class,
        SizeEntity::class,
        ColorEntity::class,
        ProductEntity::class,
        ProductVariantEntity::class,
        CustomerEntity::class,
        SaleEntity::class,
        SaleItemEntity::class,
        PaymentTransactionEntity::class,
        InventoryMovementEntity::class,
        ReturnEntity::class,
        ReturnItemEntity::class,
        ExchangeEntity::class,
        ExchangeItemEntity::class,
        ExpenseCategoryEntity::class,
        ExpenseEntity::class,
        DailyClosingEntity::class,
        AuditLogEntity::class,
        BackupRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun businessSettingsDao(): BusinessSettingsDao
    abstract fun categoryDao(): CategoryDao
    abstract fun sizeDao(): SizeDao
    abstract fun colorDao(): ColorDao
    abstract fun productDao(): ProductDao
    abstract fun productVariantDao(): ProductVariantDao
    abstract fun customerDao(): CustomerDao
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun paymentTransactionDao(): PaymentTransactionDao
    abstract fun inventoryMovementDao(): InventoryMovementDao
    abstract fun returnDao(): ReturnDao
    abstract fun returnItemDao(): ReturnItemDao
    abstract fun exchangeDao(): ExchangeDao
    abstract fun exchangeItemDao(): ExchangeItemDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun dailyClosingDao(): DailyClosingDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun backupRecordDao(): BackupRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "escape_clothing_pos.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

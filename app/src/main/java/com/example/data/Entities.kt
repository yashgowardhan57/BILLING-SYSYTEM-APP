package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "users",
    indices = [Index(value = ["mobile"], unique = true), Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val role: String, // "ADMIN" or "STAFF"
    val fullName: String,
    val email: String? = null,
    val mobile: String? = null,
    val passwordHash: String,
    val isActive: Boolean = true,
    val lastLoginAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "business_settings")
data class BusinessSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val businessName: String = "Escape Clothing",
    val addressLine1: String = "City Center 2, Butibori",
    val addressLine2: String = "441122, Maharashtra, India",
    val city: String = "Butibori",
    val postalCode: String = "441122",
    val state: String = "Maharashtra",
    val country: String = "India",
    val phone: String = "+91 98765 43210",
    val currencySymbol: String = "₹",
    val currencyCode: String = "INR",
    val timezone: String = "Asia/Kolkata",
    val invoicePrefix: String = "ESC",
    val invoiceNextNumber: Long = 1,
    val returnPolicy: String = "Returns & exchanges accepted within 7 days with original invoice and tags intact.",
    val lowStockThreshold: Double = 5.0,
    val printerPaperWidth: Int = 80, // 58 or 80 mm
    val supabaseUrl: String = "",
    val supabaseAnonKey: String = ""
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val status: String = "ACTIVE" // ACTIVE, ARCHIVED
)

@Entity(tableName = "sizes")
data class SizeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val sortOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "colors")
data class ColorEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val hexCode: String? = null,
    val isActive: Boolean = true
)

@Entity(
    tableName = "products",
    indices = [Index(value = ["categoryId"]), Index(value = ["name"])]
)
data class ProductEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val categoryId: String,
    val categoryName: String,
    val name: String,
    val description: String? = null,
    val status: String = "ACTIVE", // ACTIVE, ARCHIVED
    val defaultReorderLevel: Double = 5.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "product_variants",
    indices = [
        Index(value = ["sku"], unique = true),
        Index(value = ["productId"]),
        Index(value = ["barcode"], unique = true)
    ]
)
data class ProductVariantEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val productName: String,
    val sizeId: String,
    val sizeName: String,
    val colorId: String,
    val colorName: String,
    val sku: String,
    val barcode: String? = null,
    val costPrice: Double,
    val sellingPrice: Double,
    val stockQuantity: Double,
    val reorderLevel: Double = 5.0,
    val status: String = "ACTIVE" // ACTIVE, ARCHIVED
)

@Entity(
    tableName = "customers",
    indices = [Index(value = ["mobile"]), Index(value = ["name"])]
)
data class CustomerEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val mobile: String,
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "sales",
    indices = [
        Index(value = ["invoiceNumber"], unique = true),
        Index(value = ["businessDate"]),
        Index(value = ["staffUserId"]),
        Index(value = ["customerId"])
    ]
)
data class SaleEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val invoiceNumber: String,
    val businessDate: String, // "YYYY-MM-DD"
    val customerId: String? = null,
    val customerName: String? = null,
    val customerMobile: String? = null,
    val staffUserId: String,
    val staffName: String,
    val subtotal: Double,
    val discountAmount: Double = 0.0,
    val totalAmount: Double,
    val paidAmount: Double,
    val pendingAmount: Double = 0.0,
    val costOfGoods: Double = 0.0,
    val grossProfit: Double = 0.0,
    val paymentMethod: String, // "CASH", "ONLINE", "PENDING"
    val paymentStatus: String, // "PAID", "PARTIAL", "PENDING", "VOID"
    val status: String = "COMPLETED", // COMPLETED, PARTIALLY_RETURNED, RETURNED, VOID
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED" // PENDING, SYNCING, SYNCED
)

@Entity(
    tableName = "sale_items",
    indices = [Index(value = ["saleId"]), Index(value = ["variantId"])]
)
data class SaleItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val saleId: String,
    val variantId: String,
    val productName: String,
    val sku: String,
    val size: String,
    val color: String,
    val quantity: Double,
    val unitPrice: Double,
    val discountAmount: Double = 0.0,
    val lineTotal: Double,
    val unitCost: Double,
    val lineCost: Double,
    val lineProfit: Double,
    val returnedQuantity: Double = 0.0
)

@Entity(
    tableName = "payment_transactions",
    indices = [Index(value = ["saleId"]), Index(value = ["createdAt"])]
)
data class PaymentTransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val saleId: String,
    val invoiceNumber: String,
    val amount: Double,
    val paymentMethod: String, // "CASH", "ONLINE"
    val collectedByUserId: String,
    val collectedByName: String,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "inventory_movements",
    indices = [Index(value = ["variantId"]), Index(value = ["createdAt"]), Index(value = ["movementType"])]
)
data class InventoryMovementEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val variantId: String,
    val productName: String,
    val sku: String,
    val size: String,
    val color: String,
    val movementType: String, // "OPENING_STOCK", "PURCHASE", "SALE", "SALE_RETURN", "EXCHANGE_IN", "EXCHANGE_OUT", "DAMAGE", "ADJUSTMENT_IN", "ADJUSTMENT_OUT", "CORRECTION"
    val quantityDelta: Double,
    val previousQuantity: Double,
    val newQuantity: Double,
    val reason: String,
    val referenceType: String? = null,
    val referenceId: String? = null,
    val userId: String,
    val userName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "returns",
    indices = [Index(value = ["returnNumber"], unique = true), Index(value = ["originalSaleId"])]
)
data class ReturnEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val returnNumber: String,
    val originalSaleId: String,
    val originalInvoiceNumber: String,
    val customerId: String? = null,
    val customerName: String? = null,
    val customerMobile: String? = null,
    val businessDate: String,
    val refundAmount: Double,
    val refundMethod: String, // "CASH", "ONLINE", "NONE"
    val reason: String, // "CUSTOMER_RETURN", "DEFECTIVE", "WRONG_SIZE", "WRONG_COLOR", "EXCHANGE", "OTHER"
    val processedByUserId: String,
    val processedByName: String,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "return_items",
    indices = [Index(value = ["returnId"]), Index(value = ["originalSaleItemId"])]
)
data class ReturnItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val returnId: String,
    val originalSaleItemId: String,
    val variantId: String,
    val productName: String,
    val sku: String,
    val size: String,
    val color: String,
    val quantity: Double,
    val unitPrice: Double,
    val refundLineTotal: Double
)

@Entity(
    tableName = "exchanges",
    indices = [Index(value = ["exchangeNumber"], unique = true), Index(value = ["originalSaleId"])]
)
data class ExchangeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exchangeNumber: String,
    val originalSaleId: String,
    val originalInvoiceNumber: String,
    val businessDate: String,
    val originalValue: Double,
    val replacementValue: Double,
    val customerDifference: Double, // customer pays this
    val refundDifference: Double, // shop refunds this
    val processedByUserId: String,
    val processedByName: String,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "exchange_items",
    indices = [Index(value = ["exchangeId"])]
)
data class ExchangeItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exchangeId: String,
    val direction: String, // "RETURNED" or "ISSUED"
    val variantId: String,
    val productName: String,
    val sku: String,
    val size: String,
    val color: String,
    val quantity: Double,
    val unitPrice: Double
)

@Entity(tableName = "expense_categories")
data class ExpenseCategoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isActive: Boolean = true
)

@Entity(
    tableName = "expenses",
    indices = [Index(value = ["expenseDate"]), Index(value = ["categoryId"])]
)
data class ExpenseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val categoryId: String,
    val categoryName: String,
    val amount: Double,
    val expenseDate: String, // "YYYY-MM-DD"
    val description: String,
    val paymentMethod: String = "CASH", // "CASH", "ONLINE"
    val notes: String? = null,
    val status: String = "ACTIVE", // ACTIVE, VOID
    val voidReason: String? = null,
    val createdByUserId: String,
    val createdByName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "daily_closings",
    indices = [Index(value = ["businessDate"], unique = true)]
)
data class DailyClosingEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val businessDate: String,
    val status: String = "OPEN", // "OPEN", "CLOSED"
    val billCount: Long = 0,
    val unitsSold: Double = 0.0,
    val cashSales: Double = 0.0,
    val onlineSales: Double = 0.0,
    val pendingSales: Double = 0.0,
    val grossRevenue: Double = 0.0,
    val returnsAmount: Double = 0.0,
    val refundsAmount: Double = 0.0,
    val costOfGoods: Double = 0.0,
    val grossProfit: Double = 0.0,
    val expenses: Double = 0.0,
    val netOperatingProfit: Double = 0.0,
    val closedAt: Long? = null,
    val closedByUserId: String? = null,
    val closedByName: String? = null
)

@Entity(
    tableName = "audit_logs",
    indices = [Index(value = ["createdAt"])]
)
data class AuditLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val role: String,
    val action: String, // "SALE_CREATED", "INVENTORY_ADJUSTMENT", "RETURN_CREATED", "EXCHANGE_CREATED", "PENDING_COLLECTED", "EXPENSE_CREATED", "EXPENSE_VOIDED", "DAILY_CLOSING", "BACKUP_CREATED", "RESTORE_COMPLETED", "LOGIN"
    val entityType: String,
    val entityId: String? = null,
    val details: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "backup_records")
data class BackupRecordEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val backupType: String = "MANUAL", // "MANUAL", "AUTOMATIC"
    val status: String = "COMPLETED",
    val recordCount: Long = 0,
    val sizeBytes: Long = 0,
    val notes: String? = null,
    val isCloudSynced: Boolean = false,
    val cloudSyncError: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String = "Admin"
)

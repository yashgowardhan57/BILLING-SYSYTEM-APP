package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DatabaseSeeder {

    const val DEFAULT_SUPABASE_URL = ""
    const val DEFAULT_SUPABASE_KEY = ""

    suspend fun purgeAllDemoData(database: AppDatabase) = withContext(Dispatchers.IO) {
        database.saleItemDao().deleteAll()
        database.paymentTransactionDao().deleteAll()
        database.returnItemDao().deleteAll()
        database.returnDao().deleteAll()
        database.exchangeItemDao().deleteAll()
        database.exchangeDao().deleteAll()
        database.saleDao().deleteAll()
        database.expenseDao().deleteAll()
        database.dailyClosingDao().deleteAll()
        database.auditLogDao().insert(
            AuditLogEntity(
                userId = "system",
                userName = "System",
                role = "ADMIN",
                action = "DEMO_DATA_PURGED",
                entityType = "DATABASE",
                entityId = "all",
                details = "All demo sales, transactions, returns, and expenses purged."
            )
        )
    }

    suspend fun seedIfEmpty(database: AppDatabase) = withContext(Dispatchers.IO) {
        val settingsDao = database.businessSettingsDao()
        val existingSettings = settingsDao.getSettingsOnce()
        val userDao = database.userDao()

        // Always ensure admin credentials match: harish@gmail.com / harish123 / mobile: 9022341807
        val existingAdmin = userDao.getAdminUser() ?: userDao.getUserByEmail("harish@gmail.com")
        if (existingAdmin != null) {
            userDao.update(
                existingAdmin.copy(
                    fullName = "Harish",
                    email = "harish@gmail.com",
                    mobile = "9022341807",
                    passwordHash = "harish123",
                    isActive = true
                )
            )
        } else {
            userDao.insert(
                UserEntity(
                    id = "user-admin-harish",
                    role = "ADMIN",
                    fullName = "Harish",
                    email = "harish@gmail.com",
                    mobile = "9022341807",
                    passwordHash = "harish123",
                    isActive = true
                )
            )
        }

        if (existingSettings != null) {
            settingsDao.insertOrUpdate(
                existingSettings.copy(
                    businessName = "Escape Clothing",
                    addressLine1 = "City Center 2, Butibori",
                    phone = "9022341807"
                )
            )
            return@withContext
        }

        // 1. Business Settings with Shop Info
        settingsDao.insertOrUpdate(
            BusinessSettingsEntity(
                id = 1,
                businessName = "Escape Clothing",
                addressLine1 = "City Center 2, Butibori",
                addressLine2 = "Butibori, Maharashtra, India",
                city = "Butibori",
                postalCode = "441122",
                state = "Maharashtra",
                country = "India",
                phone = "9022341807",
                currencySymbol = "₹",
                currencyCode = "INR",
                timezone = "Asia/Kolkata",
                invoicePrefix = "ESC",
                invoiceNextNumber = 1001,
                returnPolicy = "Returns & exchanges accepted within 7 days with original invoice and tags intact.",
                lowStockThreshold = 5.0,
                printerPaperWidth = 80,
                supabaseUrl = DEFAULT_SUPABASE_URL,
                supabaseAnonKey = DEFAULT_SUPABASE_KEY
            )
        )

        // 2. Users (Admin + Clean Staff)
        val adminUser = UserEntity(
            id = "user-admin-harish",
            role = "ADMIN",
            fullName = "Harish",
            email = "harish@gmail.com",
            mobile = "9022341807",
            passwordHash = "harish123",
            isActive = true
        )
        val staffUser1 = UserEntity(
            id = "user-staff-1",
            role = "STAFF",
            fullName = "Store Staff",
            email = "staff@escapeclothing.com",
            mobile = "9876500001",
            passwordHash = "1234",
            isActive = true
        )
        userDao.insert(adminUser)
        userDao.insert(staffUser1)

        // 3. Clothing Categories
        val categoryDao = database.categoryDao()
        val catTShirts = CategoryEntity(id = "cat-1", name = "T-Shirts")
        val catShirts = CategoryEntity(id = "cat-2", name = "Shirts")
        val catJeans = CategoryEntity(id = "cat-3", name = "Jeans")
        val catTrousers = CategoryEntity(id = "cat-4", name = "Trousers")
        val catJackets = CategoryEntity(id = "cat-5", name = "Jackets")
        val catDresses = CategoryEntity(id = "cat-6", name = "Dresses")
        val catKids = CategoryEntity(id = "cat-7", name = "Kids")
        val catAccessories = CategoryEntity(id = "cat-8", name = "Accessories")

        listOf(catTShirts, catShirts, catJeans, catTrousers, catJackets, catDresses, catKids, catAccessories)
            .forEach { categoryDao.insert(it) }

        // 4. Standard Garment Sizes
        val sizeDao = database.sizeDao()
        val sizes = listOf(
            SizeEntity(id = "sz-xs", name = "XS", sortOrder = 10),
            SizeEntity(id = "sz-s", name = "S", sortOrder = 20),
            SizeEntity(id = "sz-m", name = "M", sortOrder = 30),
            SizeEntity(id = "sz-l", name = "L", sortOrder = 40),
            SizeEntity(id = "sz-xl", name = "XL", sortOrder = 50),
            SizeEntity(id = "sz-xxl", name = "XXL", sortOrder = 60),
            SizeEntity(id = "sz-28", name = "28", sortOrder = 70),
            SizeEntity(id = "sz-30", name = "30", sortOrder = 80),
            SizeEntity(id = "sz-32", name = "32", sortOrder = 90),
            SizeEntity(id = "sz-34", name = "34", sortOrder = 100),
            SizeEntity(id = "sz-36", name = "36", sortOrder = 110),
            SizeEntity(id = "sz-free", name = "Free Size", sortOrder = 120)
        )
        sizes.forEach { sizeDao.insert(it) }

        // 5. Standard Color Palette
        val colorDao = database.colorDao()
        val colors = listOf(
            ColorEntity(id = "col-blk", name = "Black", hexCode = "#101010"),
            ColorEntity(id = "col-wht", name = "White", hexCode = "#FFFFFF"),
            ColorEntity(id = "col-red", name = "Red", hexCode = "#D71920"),
            ColorEntity(id = "col-blu", name = "Blue", hexCode = "#2196F3"),
            ColorEntity(id = "col-grn", name = "Green", hexCode = "#4CAF50"),
            ColorEntity(id = "col-nvy", name = "Navy", hexCode = "#0B2545"),
            ColorEntity(id = "col-gry", name = "Grey", hexCode = "#7E7E85")
        )
        colors.forEach { colorDao.insert(it) }

        // 6. Expense Categories
        val expCatDao = database.expenseCategoryDao()
        val expCategories = listOf(
            "Bill", "Repairing", "Packaging", "Transport", "Salary",
            "Electricity", "Rent", "Maintenance", "Marketing", "Other"
        )
        expCategories.forEach {
            expCatDao.insert(ExpenseCategoryEntity(name = it))
        }

        // 7. Base Clothing Products Catalog
        val productDao = database.productDao()
        val variantDao = database.productVariantDao()

        val prod1 = ProductEntity(
            id = "prod-1",
            categoryId = catTShirts.id,
            categoryName = catTShirts.name,
            name = "Classic Crew T-Shirt",
            description = "100% Combed Compact Cotton, pre-shrunk premium finish",
            defaultReorderLevel = 5.0
        )
        productDao.insert(prod1)

        val v1_1 = ProductVariantEntity(
            id = "var-1-1", productId = prod1.id, productName = prod1.name,
            sizeId = "sz-m", sizeName = "M", colorId = "col-blk", colorName = "Black",
            sku = "TS-BLK-M", barcode = "890100100011",
            costPrice = 350.0, sellingPrice = 599.0, stockQuantity = 20.0, reorderLevel = 5.0
        )
        val v1_2 = ProductVariantEntity(
            id = "var-1-2", productId = prod1.id, productName = prod1.name,
            sizeId = "sz-l", sizeName = "L", colorId = "col-blk", colorName = "Black",
            sku = "TS-BLK-L", barcode = "890100100012",
            costPrice = 350.0, sellingPrice = 599.0, stockQuantity = 15.0, reorderLevel = 5.0
        )
        val v1_3 = ProductVariantEntity(
            id = "var-1-3", productId = prod1.id, productName = prod1.name,
            sizeId = "sz-m", sizeName = "M", colorId = "col-wht", colorName = "White",
            sku = "TS-WHT-M", barcode = "890100100013",
            costPrice = 350.0, sellingPrice = 599.0, stockQuantity = 18.0, reorderLevel = 5.0
        )

        val prod2 = ProductEntity(
            id = "prod-2",
            categoryId = catJeans.id,
            categoryName = catJeans.name,
            name = "Slim Fit Stretch Denim",
            description = "Indigo dyed stretch denim with washed aesthetic",
            defaultReorderLevel = 4.0
        )
        productDao.insert(prod2)

        val v2_1 = ProductVariantEntity(
            id = "var-2-1", productId = prod2.id, productName = prod2.name,
            sizeId = "sz-30", sizeName = "30", colorId = "col-blu", colorName = "Blue",
            sku = "JN-BLU-30", barcode = "890100200021",
            costPrice = 750.0, sellingPrice = 1699.0, stockQuantity = 10.0, reorderLevel = 4.0
        )
        val v2_2 = ProductVariantEntity(
            id = "var-2-2", productId = prod2.id, productName = prod2.name,
            sizeId = "sz-32", sizeName = "32", colorId = "col-blu", colorName = "Blue",
            sku = "JN-BLU-32", barcode = "890100200022",
            costPrice = 750.0, sellingPrice = 1699.0, stockQuantity = 14.0, reorderLevel = 4.0
        )

        val prod3 = ProductEntity(
            id = "prod-3",
            categoryId = catShirts.id,
            categoryName = catShirts.name,
            name = "Oxford Button-Down Shirt",
            description = "Crisp casual & semi-formal cotton weave",
            defaultReorderLevel = 5.0
        )
        productDao.insert(prod3)

        val v3_1 = ProductVariantEntity(
            id = "var-3-1", productId = prod3.id, productName = prod3.name,
            sizeId = "sz-m", sizeName = "M", colorId = "col-wht", colorName = "White",
            sku = "SH-WHT-M", barcode = "890100300031",
            costPrice = 520.0, sellingPrice = 1199.0, stockQuantity = 16.0, reorderLevel = 5.0
        )
        val v3_2 = ProductVariantEntity(
            id = "var-3-2", productId = prod3.id, productName = prod3.name,
            sizeId = "sz-l", sizeName = "L", colorId = "col-nvy", colorName = "Navy",
            sku = "SH-NVY-L", barcode = "890100300032",
            costPrice = 520.0, sellingPrice = 1199.0, stockQuantity = 8.0, reorderLevel = 5.0
        )

        variantDao.insertAll(listOf(v1_1, v1_2, v1_3, v2_1, v2_2, v3_1, v3_2))

        // Initial System Audit Log
        database.auditLogDao().insert(
            AuditLogEntity(
                userId = adminUser.id,
                userName = adminUser.fullName,
                role = "ADMIN",
                action = "SYSTEM_INITIALIZED",
                entityType = "STORE",
                entityId = "1",
                details = "Escape Clothing initialized with Supabase cloud backup readiness at City Center 2, Butibori"
            )
        )
    }
}

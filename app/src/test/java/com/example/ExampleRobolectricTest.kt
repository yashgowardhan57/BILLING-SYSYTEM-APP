package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.CartItem
import com.example.data.DatabaseSeeder
import com.example.data.PosRepository
import com.example.data.ProductVariantEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var repository: PosRepository

    @Before
    fun setup() = runBlocking {
        context = ApplicationProvider.getApplicationContext<Context>()
        database = AppDatabase.getDatabase(context)
        DatabaseSeeder.seedIfEmpty(database)
        repository = PosRepository(database)
    }

    @Test
    fun `verify app name resource is Escape Clothing`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Escape Clothing", appName)
    }

    @Test
    fun `verify seed data loads admin and staff correctly`() = runBlocking {
        val admin = database.userDao().getUserByEmail("admin@escapeclothing.com")
        assertNotNull(admin)
        assertEquals("ADMIN", admin?.role)

        val staff = database.userDao().getUserByMobile("9876500001")
        assertNotNull(staff)
        assertEquals("STAFF", staff?.role)
    }

    @Test
    fun `verify normal sale creates invoice and updates inventory`() = runBlocking {
        // Log in as staff
        val staff = database.userDao().getUserByMobile("9876500001")!!
        repository.setCurrentUser(staff)

        val variants = database.productVariantDao().getAll().first()
        val variant = variants.first { it.stockQuantity >= 2 }
        val initialStock = variant.stockQuantity

        val cart = listOf(CartItem(variant = variant, quantity = 2.0))
        val result = repository.completeSale(
            cartItems = cart,
            paymentMethod = "CASH",
            paidAmountInput = variant.sellingPrice * 2.0,
            discountAmount = 0.0
        )

        assertTrue(result.isSuccess)
        val sale = result.getOrThrow()
        assertTrue(sale.invoiceNumber.startsWith("ESC-"))
        assertEquals(variant.sellingPrice * 2.0, sale.totalAmount, 0.01)
        assertEquals("PAID", sale.paymentStatus)

        // Check stock updated
        val updatedVariant = database.productVariantDao().getVariantById(variant.id)!!
        assertEquals(initialStock - 2.0, updatedVariant.stockQuantity, 0.01)
    }

    @Test
    fun `verify pending payment requires customer name and phone`() = runBlocking {
        val staff = database.userDao().getUserByMobile("9876500001")!!
        repository.setCurrentUser(staff)

        val variants = database.productVariantDao().getAll().first()
        val variant = variants.first { it.stockQuantity >= 1 }

        val cart = listOf(CartItem(variant = variant, quantity = 1.0))

        // Without customer info
        val failResult = repository.completeSale(
            cartItems = cart,
            paymentMethod = "PENDING",
            paidAmountInput = variant.sellingPrice / 2,
            customerName = null,
            customerMobile = null
        )
        assertTrue(failResult.isFailure)

        // With customer info
        val successResult = repository.completeSale(
            cartItems = cart,
            paymentMethod = "PENDING",
            paidAmountInput = variant.sellingPrice / 2,
            customerName = "Rohan Verma",
            customerMobile = "9823099999"
        )
        assertTrue(successResult.isSuccess)
        val sale = successResult.getOrThrow()
        assertTrue(sale.pendingAmount > 0)
        assertEquals("Rohan Verma", sale.customerName)
    }
}

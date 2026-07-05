package com.example.moneyflow.data.repository

import app.cash.turbine.test
import com.example.moneyflow.data.local.FinlyDataStore
import com.example.moneyflow.data.local.dao.FinlyDao
import com.example.moneyflow.data.local.entity.TransactionEntity
import com.example.moneyflow.data.local.entity.TransactionType
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TransactionRepositoryImplTest {

    private lateinit var repository: TransactionRepositoryImpl
    private val dao: FinlyDao = mockk()
    private val dataStore: FinlyDataStore = mockk()

    @Before
    fun setUp() {
        every { dao.getAllTransactions() } returns flowOf(emptyList())
        repository = TransactionRepositoryImpl(dao, dataStore)
    }

    @Test
    fun `allTransactions returns flow from dao`() = runTest {
        val transactions = listOf(
            TransactionEntity(id = 1, title = "Test", amount = 100L, type = TransactionType.INCOME, categoryId = 1, date = 0L)
        )
        every { dao.getAllTransactions() } returns flowOf(transactions)
        
        val repo = TransactionRepositoryImpl(dao, dataStore)

        repo.allTransactions.test {
            assertEquals(transactions, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertTransaction calls dao and updates dataStore balance`() = runTest {
        val transaction = TransactionEntity(id = 1, title = "Test", amount = 100L, type = TransactionType.INCOME, categoryId = 1, date = 0L)
        coEvery { dao.insertTransaction(any()) } just Runs
        coEvery { dataStore.updateBalance(any()) } just Runs

        repository.insertTransaction(transaction)

        coVerify { dao.insertTransaction(transaction) }
        coVerify { dataStore.updateBalance(100L) }
    }

    @Test
    fun `deleteTransaction calls dao and reverts dataStore balance`() = runTest {
        val transaction = TransactionEntity(id = 1, title = "Test", amount = 100L, type = TransactionType.INCOME, categoryId = 1, date = 0L)
        coEvery { dao.deleteTransaction(any()) } just Runs
        coEvery { dataStore.updateBalance(any()) } just Runs

        repository.deleteTransaction(transaction)

        coVerify { dao.deleteTransaction(transaction) }
        coVerify { dataStore.updateBalance(-100L) }
    }

    @Test
    fun `getTransactionById returns transaction from dao`() = runTest {
        val transaction = TransactionEntity(id = 1, title = "Test", amount = 100L, type = TransactionType.INCOME, categoryId = 1, date = 0L)
        coEvery { dao.getTransactionById(1) } returns transaction

        val result = repository.getTransactionById(1)

        assertEquals(transaction, result)
    }
}

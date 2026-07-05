package com.example.moneyflow.features.transaction.domain.usecase

import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.repository.TransactionRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TransactionUseCasesTest {

    private lateinit var addUseCase: AddTransactionUseCase
    private lateinit var deleteUseCase: DeleteTransactionUseCase
    private val repository: TransactionRepository = mockk()

    @Before
    fun setUp() {
        addUseCase = AddTransactionUseCase(repository)
        deleteUseCase = DeleteTransactionUseCase(repository)
    }

    @Test
    fun `AddTransactionUseCase ensures negative amount for expenses`() = runTest {
        coEvery { repository.insertTransaction(any()) } just Runs

        addUseCase("Coffee", 50000L, TransactionType.EXPENSE, 1, null, "wallet", "06-2026")

        coVerify { 
            repository.insertTransaction(match { it.amount == -50000L }) 
        }
    }

    @Test
    fun `AddTransactionUseCase ensures positive amount for income`() = runTest {
        coEvery { repository.insertTransaction(any()) } just Runs

        addUseCase("Salary", 10000000L, TransactionType.INCOME, 1, null, "wallet", "06-2026")

        coVerify { 
            repository.insertTransaction(match { it.amount == 10000000L }) 
        }
    }

    @Test
    fun `DeleteTransactionUseCase calls repository correctly`() = runTest {
        val tx = mockk<com.example.moneyflow.data.local.entity.TransactionEntity>()
        coEvery { repository.deleteTransaction(any()) } just Runs

        deleteUseCase(tx)

        coVerify { repository.deleteTransaction(tx) }
    }
}

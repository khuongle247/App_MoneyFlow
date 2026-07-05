package com.example.moneyflow.features.transaction.domain.usecase

import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.repository.TransactionRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AddTransactionUseCaseTest {

    private lateinit var useCase: AddTransactionUseCase
    private val repository: TransactionRepository = mockk()

    @Before
    fun setUp() {
        useCase = AddTransactionUseCase(repository)
    }

    @Test
    fun `invoke calls repository insertTransaction`() = runTest {
        coEvery { repository.insertTransaction(any()) } just Runs

        useCase("Lunch", 50L, TransactionType.EXPENSE, 1, null, "wallet", "06-2026")

        coVerify { repository.insertTransaction(match { it.title == "Lunch" && it.amount == -50L }) }
    }
}

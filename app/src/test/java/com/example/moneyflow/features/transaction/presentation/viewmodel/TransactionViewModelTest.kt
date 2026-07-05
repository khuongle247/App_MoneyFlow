package com.example.moneyflow.features.transaction.presentation.viewmodel

import app.cash.turbine.test
import com.example.moneyflow.MainDispatcherRule
import com.example.moneyflow.data.local.entity.TransactionEntity
import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.features.transaction.domain.usecase.TransactionUseCases
import com.example.moneyflow.features.transaction.presentation.model.TransactionEffect
import com.example.moneyflow.features.transaction.presentation.model.TransactionEvent
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TransactionViewModel
    private val transactionUseCases: TransactionUseCases = mockk()

    @Before
    fun setUp() {
        every { transactionUseCases.getTransactionsByMonth(any()) } returns flowOf(emptyList())
        every { transactionUseCases.getMetadata.categories } returns flowOf(emptyList())
        every { transactionUseCases.getMetadata.budgets } returns flowOf(emptyList())

        viewModel = TransactionViewModel(transactionUseCases)
    }

    @Test
    fun `OnSearchChanged updates filtered transactions in contentState`() = runTest {
        val allTx = listOf(
            TransactionEntity(id = 1, title = "Cafe", amount = -30000, type = TransactionType.EXPENSE, categoryId = 1, date = 0),
            TransactionEntity(id = 2, title = "Salary", amount = 100000, type = TransactionType.INCOME, categoryId = 2, date = 0)
        )
        every { transactionUseCases.getTransactionsByMonth(any()) } returns flowOf(allTx)

        viewModel.onEvent(TransactionEvent.OnSearchChanged("cafe"))

        viewModel.contentState.test {
            val state = awaitItem()
            assertEquals(1, state.transactions.size)
            assertEquals("Cafe", state.transactions[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnSaveTransaction emits NavigateBack effect`() = runTest {
        coEvery { transactionUseCases.addTransaction(any(), any(), any(), any(), any(), any(), any()) } just Runs
        
        viewModel.onEvent(TransactionEvent.OnTitleChanged("Test"))
        viewModel.onEvent(TransactionEvent.OnAmountChanged("100000"))

        viewModel.effect.test {
            viewModel.onEvent(TransactionEvent.OnSaveTransaction)
            val effect = awaitItem()
            assert(effect is TransactionEffect.NavigateBack)
        }
    }

    @Test
    fun `form input events update filterState correctly`() = runTest {
        viewModel.filterState.test {
            // Initial state from ViewModel creation
            assertEquals("", awaitItem().title)

            viewModel.onEvent(TransactionEvent.OnTitleChanged("New Title"))
            assertEquals("New Title", awaitItem().title)
        }
    }
}

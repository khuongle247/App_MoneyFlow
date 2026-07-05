package com.example.moneyflow.data.repository

import com.example.moneyflow.data.local.FinlyDataStore
import com.example.moneyflow.data.local.dao.FinlyDao
import com.example.moneyflow.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val dao: FinlyDao,
    private val dataStore: FinlyDataStore
) : TransactionRepository {

    override val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        dao.insertTransaction(transaction)
        dataStore.updateBalance(transaction.amount)
    }

    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
        dataStore.updateBalance(-transaction.amount)
    }

    override suspend fun getTransactionById(id: Long): TransactionEntity? = 
        dao.getTransactionById(id)

    override fun getTransactionsByCategory(catId: Long): Flow<List<TransactionEntity>> = 
        dao.getTransactionsByCategory(catId)

    override fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> =
        dao.getTransactionsInRange(startDate, endDate)
}

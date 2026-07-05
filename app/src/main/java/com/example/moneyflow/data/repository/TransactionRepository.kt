package com.example.moneyflow.data.repository

import com.example.moneyflow.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    val allTransactions: Flow<List<TransactionEntity>>
    suspend fun insertTransaction(transaction: TransactionEntity)
    suspend fun deleteTransaction(transaction: TransactionEntity)
    suspend fun getTransactionById(id: Long): TransactionEntity?
    fun getTransactionsByCategory(catId: Long): Flow<List<TransactionEntity>>
    fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>
}

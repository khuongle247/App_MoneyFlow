package com.example.moneyflow.features.dashboard.domain.usecase

import com.example.moneyflow.data.local.entity.TransactionEntity
import com.example.moneyflow.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetMonthlyTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(monthId: String): Flow<List<TransactionEntity>> {
        return repository.allTransactions.map { txs ->
            txs.filter { SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date(it.date)) == monthId }
        }
    }
}

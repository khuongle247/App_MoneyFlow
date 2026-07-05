package com.example.moneyflow.features.dashboard.domain.usecase

import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.repository.TransactionRepository
import com.example.moneyflow.features.dashboard.domain.model.DailyExpenseSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class GetDailyExpenseSummaryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<List<DailyExpenseSummary>> {
        return repository.getTransactionsInRange(startDate, endDate).map { txs ->
            txs.filter { it.type == TransactionType.EXPENSE }
                .groupBy { 
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = it.date
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    cal.timeInMillis
                }
                .map { (date, list) ->
                    DailyExpenseSummary(date, list.sumOf { Math.abs(it.amount) })
                }
        }
    }
}

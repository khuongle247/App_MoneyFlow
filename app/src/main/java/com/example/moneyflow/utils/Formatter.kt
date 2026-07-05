package com.example.moneyflow.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object Formatter {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())
    private val monthYearLabelFormat = SimpleDateFormat("'Tháng' MM, yyyy", Locale.getDefault())

    fun formatCurrency(amount: Double): String = currencyFormat.format(amount)
    fun formatCurrency(amount: Long): String = currencyFormat.format(amount)

    fun formatDate(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))

    fun formatMonthLabel(monthId: String): String {
        return try {
            val date = monthYearFormat.parse(monthId) ?: Date()
            monthYearLabelFormat.format(date)
        } catch (e: Exception) {
            monthId
        }
    }
}

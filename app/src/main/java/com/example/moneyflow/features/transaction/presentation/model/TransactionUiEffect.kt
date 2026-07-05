package com.example.moneyflow.features.transaction.presentation.model

sealed interface TransactionEffect {
    object NavigateBack : TransactionEffect
    data class ShowToast(val message: String) : TransactionEffect
}

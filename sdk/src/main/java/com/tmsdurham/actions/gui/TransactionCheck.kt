package com.tmsdurham.actions.gui

data class TransactionCheck(var type: PaymentType = PaymentType.NONE, var displayName: String = "") {
    fun isEmpty() = this == emptyTransactionCheck
    fun isNotEmpty() = !isEmpty()

    companion object {
        val emptyTransactionCheck = TransactionCheck()
    }
}

enum class PaymentType {
    PAYMENT_CARD,
    NONE
}


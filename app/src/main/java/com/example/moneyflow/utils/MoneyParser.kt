package com.example.moneyflow.utils

object MoneyParser {
    /**
     * Converts user input string to Long (VND).
     * Rule: Remove all non-numeric characters and return the number.
     * We don't care about dots or commas because VND has no subunits (cents).
     */
    fun parse(input: String): Long {
        if (input.isBlank()) return 0L
        
        // Remove everything that is not a digit
        val cleanString = input.replace(Regex("[^0-9]"), "")
        
        return cleanString.toLongOrNull() ?: 0L
    }
}

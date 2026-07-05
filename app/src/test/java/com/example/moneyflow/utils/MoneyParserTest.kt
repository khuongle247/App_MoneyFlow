package com.example.moneyflow.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyParserTest {

    @Test
    fun `parse converts various formats to Long correctly`() {
        assertEquals(1000000L, MoneyParser.parse("1000000"))
        assertEquals(1000000L, MoneyParser.parse("1.000.000"))
        assertEquals(1000000L, MoneyParser.parse("1,000,000"))
        assertEquals(1000000L, MoneyParser.parse("1 000 000"))
        assertEquals(0L, MoneyParser.parse(""))
        assertEquals(0L, MoneyParser.parse("abc"))
    }
}

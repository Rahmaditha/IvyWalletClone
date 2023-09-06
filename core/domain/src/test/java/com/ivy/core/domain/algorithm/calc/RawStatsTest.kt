package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class RawStatsTest{

    private lateinit var incomes: MutableMap<CurrencyCode, Double>
    private lateinit var tenSecondsAgo: Instant
    private lateinit var treeSecondsAgo: Instant

    private lateinit var currency1: CurrencyCode
    private lateinit var currency2: CurrencyCode

    private lateinit var type1: TransactionType
    private lateinit var type2: TransactionType

    @BeforeEach
    fun setUp(){
        currency1 = "EUR"
        currency2 = "IDR"
        type1 = TransactionType.Income
        type2 = TransactionType.Expense
        tenSecondsAgo = Instant.now().minusSeconds(10)
        treeSecondsAgo = Instant.now().minusSeconds(3)
    }

    @Test
    fun `test calculate income`(){
        val statsActual = rawStats(
            listOf<CalcTrn>(
                CalcTrn(
                    amount = 5.0,
                    currency = currency1,
                    type = type1,
                    time = tenSecondsAgo
                ),

                CalcTrn(
                    amount = 15.0,
                    currency = currency1,
                    type = type1,
                    time = treeSecondsAgo
                )
            )
        )

//        val statsExpected = listOf<CalcTrn>(
//            CalcTrn(
//                amount = 5.0,
//                currency = currency1,
//                type = type1,
//                time = tenSecondsAgo
//            ),
//
//            CalcTrn(
//                amount = 15.0,
//                currency = currency1,
//                type = type1,
//                time = treeSecondsAgo
//            )
//        )
        assertThat(statsActual.incomesCount).isEqualTo(2)
        assertThat(statsActual.expensesCount).isEqualTo(0)
        assertThat(statsActual.newestTrnTime).isEqualTo(treeSecondsAgo)
        assertThat(statsActual.incomes).isEqualTo(mapOf("EUR" to 20.0))
        assertThat(statsActual.expenses).isEqualTo(emptyMap())
    }
}
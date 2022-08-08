package io.hidro.bignumber.model

import io.hidro.bignumber.util.CompositionOperators

data class ComposedNumber(
    val unit1: Triple<Double, Double?, CompositionOperators>,
    val midOperator: CompositionOperators? = null,
    val unit2: Triple<Double, Double?, CompositionOperators>? = null,
)

fun ComposedNumber.hasSecondUnit(): Boolean {
    return unit2 != null
}

fun ComposedNumber.actualValue(): Double {
    return when (unit1.third) {
        CompositionOperators.SUM -> {
            unit1.first + (unit1.second ?: 0.0)
        }
        CompositionOperators.MULTIPLICATION -> TODO()
        CompositionOperators.SUBTRACTION -> {
            unit1.first - (unit1.second ?: 0.0)
        }
        CompositionOperators.DIVISION -> TODO()
        CompositionOperators.NONE -> {
            unit1.first
        }
    }
}
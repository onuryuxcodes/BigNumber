package io.hidro.bignumber.model

import io.hidro.bignumber.util.CompositionOperators

data class ComposedNumber(
    val unit1: Triple<Double, Double?, CompositionOperators>,
    val midOperator: CompositionOperators? = null,
    val unit2: Triple<Double, Double?, CompositionOperators>? = null
)

fun ComposedNumber.hasSecondUnit(): Boolean {
    return unit2 != null
}

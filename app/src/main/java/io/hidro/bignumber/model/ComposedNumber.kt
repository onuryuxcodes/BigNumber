package io.hidro.bignumber.model

import io.hidro.bignumber.util.CompositionOperators

data class ComposedUnit(val unit: Triple<Double, Double?, CompositionOperators>)

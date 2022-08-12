package io.hidro.bignumber.util

import io.hidro.bignumber.model.ComposedNumber
import io.hidro.bignumber.model.hasSecondUnit

class FormattingFunctions {
    companion object {
        fun formatComposedNumbersForScreen(composedNumber: ComposedNumber?): String {
            if (composedNumber != null) {
                if (!composedNumber.hasSecondUnit()) {
                    return formatSingleUnit(composedNumber.unit1)
                } else {
                    composedNumber.midOperator?.let { midOperator ->
                        return "(" + formatSingleUnit(composedNumber.unit1) + ")" + formatOperator(
                            midOperator
                        ) + "(" + formatSingleUnit(composedNumber.unit2!!) + ")"
                    }
                }
            }
            return ""
        }

        private fun formatSingleUnit(singleUnit: Triple<Double, Double?, CompositionOperators>): String {
            return "" + formatSingleDouble(singleUnit.first) +
                    formatOperator(singleUnit.third) +
                    formatSingleDouble(singleUnit.second)
        }

        private fun formatSingleDouble(singleDouble: Double?): String {
            singleDouble?.let { doubleVal ->
                return if (doubleVal % 1 == 0.0) doubleVal.toInt().toString()
                else doubleVal.toString()
            }
            return ""
        }

        private fun formatOperator(operator: CompositionOperators): String {
            return when (operator) {
                CompositionOperators.SUM -> {
                    "+"
                }
                CompositionOperators.DIVISION -> {
                    "/"
                }
                CompositionOperators.MULTIPLICATION -> {
                    "*"
                }
                CompositionOperators.SUBTRACTION -> {
                    "-"
                }
                CompositionOperators.NONE -> {
                    ""
                }
            }
        }
    }
}
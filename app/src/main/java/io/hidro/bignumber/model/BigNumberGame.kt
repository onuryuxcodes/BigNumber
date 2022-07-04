package io.hidro.bignumber.model

data class BigNumberGame(
    val started: Long,
    val ended: Long?
)

fun BigNumberGame.isOnGoing():Boolean{
    return ended == null
}
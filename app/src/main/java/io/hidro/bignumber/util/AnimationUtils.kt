package io.hidro.bignumber.util

import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

class AnimationUtils {
    companion object {
        fun createSpringAnimation(
            view: View,
            property: DynamicAnimation.ViewProperty,
            finalPosition: Float,
            stiffness: Float,
            dampingRatio: Float
        ): SpringAnimation {
            val animation = SpringAnimation(view, property)
            val spring = SpringForce(finalPosition)
            spring.stiffness = stiffness
            spring.dampingRatio = dampingRatio
            animation.spring = spring
            return animation
        }
    }
}
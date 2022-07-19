package io.hidro.bignumber.vm

import androidx.lifecycle.ViewModel
import io.hidro.bignumber.api.FeedbackApi

class FeedbackVM : ViewModel() {
    private var feedbackApi = FeedbackApi()
    fun saveUserFeedback(userComment: String) = feedbackApi.saveUserFeedback(userComment)

}
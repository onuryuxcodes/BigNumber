package io.hidro.bignumber.api

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//TODO introduce DI
class FeedbackApi {

    companion object {
        private const val USER_DIRECT_COMMENT_KEY = "user_direct_comment"
        private const val FEEDBACK_COLLECTION_ID = "Feedback"
        private const val LOG_TAG = "saveUserFeedback"
    }

    fun saveUserFeedback(userComment: String) {
        val db = Firebase.firestore
        val feedback = hashMapOf(USER_DIRECT_COMMENT_KEY to userComment)
        db.collection(FEEDBACK_COLLECTION_ID)
            .add(feedback)
            .addOnSuccessListener { documentReference ->
                Log.d(LOG_TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(LOG_TAG, "Error adding document", e)
            }
    }
}
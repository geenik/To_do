package com.example.todo

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

object NetworkIdlingResource {
    private const val RESOURCE = "NETWORK_CALLS"
    private val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }

    fun getIdlingResource(): IdlingResource {
        return countingIdlingResource
    }
}

package com.example.poe.util

import android.util.Log

object AppLogger {
    private const val DEFAULT_TAG = "POE"

    fun i(message: String, tag: String = DEFAULT_TAG) {
        Log.i(tag, message)
    }

    fun d(message: String, tag: String = DEFAULT_TAG) {
        Log.d(tag, message)
    }

    fun e(message: String, throwable: Throwable? = null, subTag: String = DEFAULT_TAG) {
        Log.e(subTag, message, throwable)
    }

    fun w(message: String, tag: String = DEFAULT_TAG) {
        Log.w(tag, message)
    }
}

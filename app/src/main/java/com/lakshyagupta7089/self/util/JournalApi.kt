package com.lakshyagupta7089.self.util

import android.app.Application

class JournalApi : Application() {
    private var username: String? = null
    private var userId: String? = null

    companion object {
        private var INSTANCE: JournalApi? = null
        val instance: JournalApi?
            get() {
                if (INSTANCE == null)
                    INSTANCE = JournalApi()

                return INSTANCE
            }
    }

    fun getUsername(): String? = username

    fun setUsername(username: String) {
        this.username = username
    }

    fun getUserId(): String? = userId

    fun setUserId(userId: String) {
        this.userId = userId
    }
}
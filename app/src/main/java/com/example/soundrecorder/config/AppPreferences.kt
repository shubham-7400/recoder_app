package com.example.soundrecorder.config

import android.content.Context
import android.content.SharedPreferences

class AppPreferences {

    private lateinit var preferences: SharedPreferences

    //SharedPreferences variables
    private val IS_LOGIN = Pair("is_login", false)
    private val EMAIL = Pair("email", "")
    private val UUID = Pair("uuid", "")
    private val TOKEN = Pair("token","")

    fun init(context: Context) {
        preferences = context.getSharedPreferences(Companion.NAME, Companion.MODE)
    }

    //an inline function to put variable and save it
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    //SharedPreferences variables getters/setters
    var isLogin: Boolean
        get() = preferences.getBoolean(IS_LOGIN.first, IS_LOGIN.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_LOGIN.first, value)
        }

    var email: String
        get() = preferences.getString(EMAIL.first, EMAIL.second) ?: ""
        set(value) = preferences.edit {
            it.putString(EMAIL.first, value)
        }

    var uuid: String
        get() = preferences.getString(UUID.first, UUID.second) ?: ""
        set(value) = preferences.edit {
            it.putString(UUID.first, value)
        }

    var token: String
        get() = preferences.getString(TOKEN.first, TOKEN.second) ?: ""
        set(value) = preferences.edit {
            it.putString(TOKEN.first, value)
        }

    companion object {
        private const val NAME = "UserDetails"
        private const val MODE = Context.MODE_PRIVATE
    }

}
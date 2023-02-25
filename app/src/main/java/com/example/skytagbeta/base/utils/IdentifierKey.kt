package com.example.skytagbeta.base.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

class IdentifierKey {

    @SuppressLint("HardwareIds")
    fun getIdentifierKey(context: Context): String {

        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ).uppercase()
    }
}
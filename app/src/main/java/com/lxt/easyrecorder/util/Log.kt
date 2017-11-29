package com.lxt.easyrecorder.util

import android.util.Log

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/11/29.
 */
class Log {

    companion object {

        const val TAG = "TAG"

        fun i(msg: String) {
            Log.i(TAG, msg)
        }
    }
}
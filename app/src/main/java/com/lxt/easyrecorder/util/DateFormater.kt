package com.lxt.easyrecorder.util

import android.text.format.DateFormat

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/11/30.
 */
object DateFormater {

    fun formatDate(time: Long): CharSequence = DateFormat.format("yyyy-MM-dd-hh:mm:ss", time)
}
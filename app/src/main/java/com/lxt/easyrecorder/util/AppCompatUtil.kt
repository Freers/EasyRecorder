package com.lxt.easyrecorder.util

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.lxt.easyrecorder.core.RecordMedia.REUQEST_CODE_PERMISSION

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/11/30.
 */
object AppCompatUtil {

    fun checkPermission(activity: Activity, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                //TODO
            }
            ActivityCompat.requestPermissions(activity, arrayOf(permission), REUQEST_CODE_PERMISSION)
            return false
        }
        return true
    }
}
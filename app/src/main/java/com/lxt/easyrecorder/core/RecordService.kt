package com.lxt.easyrecorder.core

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.media.projection.MediaProjectionManager
import android.os.Environment
import android.os.Handler
import android.view.WindowManager
import com.lxt.easyrecorder.core.RecordMedia.EXTRA_CODE
import com.lxt.easyrecorder.core.RecordMedia.EXTRA_DATA
import java.io.File

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/11/29.
 */
class RecordService : IntentService(RecordService::class.simpleName) {

    private var recorder: LazyRecorder? = null

    private var parameter: RecordParameter? = null

    override fun onCreate() {
        super.onCreate()
        calculateParameter()
        Handler().postDelayed({
            recorder?.endRecord()
        }, 10000)
    }

    private fun calculateParameter(): RecordParameter? {
        parameter = RecordParameter().apply {
            val windowService = getSystemService(WINDOW_SERVICE) as WindowManager
            val screen = Point()
            windowService.defaultDisplay.getSize(screen)
            width = screen.x
            height = screen.y
            bitRate = RecordMedia.BIT_RATE
            disPixel = RecordMedia.PIXEL_DISPLAY
            storePath = File(Environment.getExternalStorageDirectory(), "easyRecorder-"
                    + System.currentTimeMillis() + ".mp4").absolutePath
        }
        return parameter
    }

    override fun onHandleIntent(intent: Intent?) {
        val resultCode = intent?.getIntExtra(EXTRA_CODE, -10010)
        val data = intent?.getParcelableExtra<Intent>(EXTRA_DATA)
        val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        resultCode?.let { parameter?.mediaProje = projectionManager.getMediaProjection(resultCode, data) }
        if (recorder == null)
            parameter?.let { recorder = LazyRecorder(it) }
        recorder?.startRecord()
    }
}
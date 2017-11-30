package com.lxt.easyrecorder.core

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.media.projection.MediaProjectionManager
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.view.WindowManager
import com.lxt.easyrecorder.core.RecordMedia.EXTRA_CODE
import com.lxt.easyrecorder.core.RecordMedia.EXTRA_DATA
import com.lxt.easyrecorder.util.DateFormatter
import com.lxt.easyrecorder.util.Log
import com.lxt.record.IRecordService
import java.io.File

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/11/29.
 */
class RecordService : IntentService(RecordService::class.simpleName) {

    private var recorder: LazyRecorder? = null

    private var parameter: RecordParameter? = null

    private var iBinder: IRecordService.Stub = object : IRecordService.Stub() {

        override fun recording(): Boolean {
            recorder?.let {
                return it.recording
            }
            return false
        }

        override fun start() {
            recorder?.startRecord()
        }

        override fun stop() {
            recorder?.endRecord()
        }

    }

    override fun onCreate() {
        super.onCreate()
        calculateParameter()
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.i("onBind")
        return iBinder
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


    private fun calculateParameter(): RecordParameter? {
        parameter = RecordParameter().apply {
            val windowService = getSystemService(WINDOW_SERVICE) as WindowManager
            val screen = Point()
            windowService.defaultDisplay.getSize(screen)
            width = screen.x
            height = screen.y
            bitRate = RecordMedia.BIT_RATE
            disPixel = RecordMedia.PIXEL_DISPLAY
            storePath = File(Environment.getExternalStorageDirectory(), "EasyRecorder-"
                    + DateFormatter.formatDate(System.currentTimeMillis()) + ".mp4").absolutePath
        }
        return parameter
    }

}
package com.lxt.easyrecorder.core

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.media.projection.MediaProjectionManager
import android.view.WindowManager

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/11/29.
 */
class RecordService : IntentService {

    private var recorder: LazyRecorder

    constructor() : super(RecordService::class.simpleName) {
        recorder = LazyRecorder(calculateParameter())
    }

    private fun calculateParameter(): RecordParameter {
        return RecordParameter().apply {
            val windowService = getSystemService(WINDOW_SERVICE) as WindowManager
            val screen = Point()
            windowService.defaultDisplay.getSize(screen)
            width = screen.x
            height = screen.y
            bitRate = RecordMedia.BIT_RATE
            disPixel = RecordMedia.PIXEL_DISPLAY
            val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProje = projectionManager.getMediaProjection(0, null)
            storePath = ""
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        recorder.startRecord()
    }
}
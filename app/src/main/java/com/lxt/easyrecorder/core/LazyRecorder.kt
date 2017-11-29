package com.lxt.easyrecorder.core

import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaCodec
import android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
import android.media.MediaFormat
import android.media.MediaFormat.*
import android.media.MediaMuxer
import android.media.MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
import android.view.Surface
import com.lxt.easyrecorder.core.RecordMedia.FRAME_COUNT
import com.lxt.easyrecorder.core.RecordMedia.FRAME_INTERVAL
import com.lxt.easyrecorder.core.RecordMedia.MIME_TYPE
import com.lxt.easyrecorder.core.RecordMedia.NAME_DISPLAY
import com.lxt.easyrecorder.util.Log
import java.io.IOException

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/11/29.
 */
class LazyRecorder {

    private var recording: Boolean = false
        get() {
            if (field) {
                Log.i("recording")
            }
            return field
        }
        set(value) {
            Log.i("set record running " + value)
            field = value
        }


    private var recorder: RecordParameter

    private var mediaCode: MediaCodec? = null

    private var mediaMuxer: MediaMuxer? = null

    private var surface: Surface? = null

    private var virtualDispaly: VirtualDisplay? = null

    constructor(recordParamter: RecordParameter) {
        recorder = recordParamter
    }

    fun start() {
        mediaCode?.start()
    }

    fun stop() {
        mediaCode?.apply {
            stop()
            release()
        }
        mediaMuxer?.apply {
            stop()
            release()
        }
        virtualDispaly?.apply {
            release()
        }
        recorder.mediaProje?.apply {
            stop()
        }
    }

    @Throws(IOException::class)
    fun createRecordMedia() {
        val mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, recorder.width, recorder.height)
                .apply {
                    setInteger(KEY_COLOR_FORMAT, COLOR_FormatSurface)
                    setInteger(KEY_FRAME_RATE, FRAME_COUNT)
                    setInteger(KEY_I_FRAME_INTERVAL, FRAME_INTERVAL)
                    setInteger(KEY_BIT_RATE, recorder.bitRate)
                }
        mediaCode = MediaCodec.createEncoderByType(MIME_TYPE)
                .apply {
                    configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                    with(surface) {
                        surface = createInputSurface()
                    }
                }
        mediaMuxer = MediaMuxer(recorder.storePath, MUXER_OUTPUT_MPEG_4)
        virtualDispaly = recorder.mediaProje?.createVirtualDisplay(NAME_DISPLAY, recorder.width,
                recorder.height, recorder.disPixel, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface,
                null, null)
    }

    fun record() {

    }
}
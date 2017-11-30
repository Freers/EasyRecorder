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
import com.lxt.easyrecorder.core.RecordMedia.TIME_NEGATIVE_OUT
import com.lxt.easyrecorder.core.RecordMedia.TIME_RETRY_AGAIN
import com.lxt.easyrecorder.util.Log
import java.io.IOException

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/11/29.
 */
class LazyRecorder(recordParamter: RecordParameter) {

    private var recorder: RecordParameter = recordParamter

    private var mediaCode: MediaCodec? = null

    private var mediaMuxer: MediaMuxer? = null

    private var surface: Surface? = null

    private var virtualDispaly: VirtualDisplay? = null

    private var bufferInfo = MediaCodec.BufferInfo()

    private var trackIndex: Int? = -1

    private var muxerReady = false

    private var recording: Boolean = true
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

    private fun release() {
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
    private fun createRecordMedia() {
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
        mediaCode?.start()
    }

    fun startRecord() {
        recording = true
        createRecordMedia()
        while (recording) {
            val res = mediaCode?.dequeueOutputBuffer(bufferInfo, TIME_NEGATIVE_OUT)
            if (res!! >= 0) {
                if (!muxerReady) {
                    throw IllegalStateException()
                } else {
                    writeData(res)
                }
            } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                trackMuxer()
            } else if (res == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Thread.sleep(TIME_RETRY_AGAIN)
            }
        }
        release()
    }

    private fun writeData(res: Int) {
        var buffer = mediaCode?.getOutputBuffer(res)
        val flag = MediaCodec.BUFFER_FLAG_CODEC_CONFIG
        if (bufferInfo.flags.and(flag) != 0) {
            bufferInfo.size = 0
        }
        if (bufferInfo.size == 0) {
            buffer = null
        }
        buffer?.apply {
            position(bufferInfo.offset)
            limit(bufferInfo.offset + bufferInfo.size)
            trackIndex?.let { mediaMuxer?.writeSampleData(it, buffer, bufferInfo) }
        }
        mediaCode?.releaseOutputBuffer(res, false)
    }

    private fun trackMuxer() {
        if (muxerReady) {
            throw IllegalStateException()
        }
        trackIndex = mediaMuxer?.addTrack(mediaCode?.outputFormat)
        mediaMuxer?.start()
        muxerReady = true
    }

    fun endRecord() {
        recording = false
    }
}
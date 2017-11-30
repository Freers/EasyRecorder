package com.lxt.easyrecorder

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.lxt.easyrecorder.core.RecordMedia
import com.lxt.easyrecorder.core.RecordMedia.EXTRA_CODE
import com.lxt.easyrecorder.core.RecordMedia.EXTRA_DATA
import com.lxt.easyrecorder.core.RecordMedia.REQUEST_CODE_CAPTURE_SCREEN
import com.lxt.easyrecorder.core.RecordService
import com.lxt.easyrecorder.util.AppCompatUtil
import com.lxt.easyrecorder.util.Log
import com.lxt.record.IRecordService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var serviceIntent: Intent

    private var serviceBound = false

    private var recordService: IRecordService? = null

    private var projectionManager: MediaProjectionManager? = null

    private val connection: ServiceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("onServiceDisconnected")
            serviceBound = false
            recordService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("onServiceConnected")
            serviceBound = true
            recordService = IRecordService.Stub.asInterface(service)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        serviceIntent = Intent(this, RecordService::class.java)
        projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        AppCompatUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        fab.setOnClickListener {
            if (recordService?.recording()!!) {
                recordService?.stop()
            } else {
                val captureIntent = projectionManager?.createScreenCaptureIntent()
                startActivityForResult(captureIntent, REQUEST_CODE_CAPTURE_SCREEN)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings ->
                return true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!serviceBound)
            bindService(serviceIntent, connection, BIND_AUTO_CREATE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordMedia.REUQEST_CODE_PERMISSION) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAPTURE_SCREEN) {
            serviceIntent.putExtra(EXTRA_CODE, resultCode)
            serviceIntent.putExtra(EXTRA_DATA, data)
            startService(serviceIntent)
        }
    }
}

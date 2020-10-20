package com.example.imsafedemo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.imsafedemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var context: Context
    var isScanning: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this
        context = this
        checkRunTimePermission()
    }

    fun startService() {
        val bUtility = WiSeMeshBluetoothScanUtility(application)
        if (bUtility.isBluetoothEnabled) {
            actionOnService(Actions.START)
        }else{
            Toast.makeText(context, "Turn on Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopService() {
        val bUtility = WiSeMeshBluetoothScanUtility(application)
        if (bUtility.isBluetoothEnabled) {
            actionOnService(Actions.STOP)
        }else{
            Toast.makeText(context, "Turn on Bluetooth", Toast.LENGTH_SHORT).show()
        }

    }

    fun actionOnService(action: Actions, checkForSerialNo: Boolean = false) {
        if (Looper.myLooper() == null)
            Looper.prepare()
        val endlessService = EndlessService()
        if (action.name == Actions.START.name && ServiceUtility.isMyServiceRunning(this, endlessService.javaClass)) {
            Logger.i("TAG", "Endless Service Running and hence not starting it again...:)")
            return
        }
        Intent(this, endlessService.javaClass).also {
            it.action = action.name
            it.putExtra("checkForSerialNo", checkForSerialNo)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("TAG", "Starting the service in >=26 Mode")
                val comp = ContextCompat.startForegroundService(this, it)
                Log.d("TAG", "actionOnService() : Service started : " + comp.toString())
                return
            }
            Log.d("TAG", "Starting the service in < 26 Mode")
            val comp = startService(it)
            Log.d("TAG", "actionOnService() : Service started : " + comp.toString())
        }
    }

    fun checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                requestPermissions(
                        arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        10
                )
            }
        } else {
        }
    }
}
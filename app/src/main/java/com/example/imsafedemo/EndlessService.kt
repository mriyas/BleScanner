package com.example.imsafedemo

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import java.text.SimpleDateFormat
import java.util.*

class EndlessService : LifecycleService(), SensorEventListener {


    private val NORMAL_NOTIFICATION_ID = 2147483647
    private val ERROR_NOTIFICATION_BLUETOOTH_ID = 2147483645


    var mServiceStartTime: Long = 0
    var mBleScanRestartCounter: Long = 0

    lateinit var imSafeNotificationManager: IMSafeNotificationManager

    lateinit var mBleUtility: WiSeMeshBluetoothScanUtility
    var scanCallBack: ScanCallback? = null

    val TAG = javaClass.simpleName

    override fun onBind(intent: Intent): IBinder? {
        if (null != intent) {
            super.onBind(intent)
        }
        Log.d("TAG", "Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            super.onStartCommand(intent, flags, startId)
            val action = intent.action
            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> Log.d("Tag", "This should never happen. No action in the received intent")
            }
        } else {
            startService()
        }

        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        imSafeNotificationManager = IMSafeNotificationManager(this)
        mBleUtility = WiSeMeshBluetoothScanUtility(this)

        mServiceStartTime = System.currentTimeMillis()
        var notification = createServiceStartNotification()
        startForeground(NORMAL_NOTIFICATION_ID, notification)
        Log.e("TAG", "The service has been created".toUpperCase())

    }


    private fun showNotificationForMockLocation() {
        createErrorNotification(
                getString(R.string.app_name),
                "mockLocationEnabled"
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        stopBleScan()
        Log.d("TAG", "The service has been destroyed".toUpperCase())
        // Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()

        //unRegisterBleStateReciever()
    }

    private fun startService() {

        // we're starting a loop in a coroutine TODO
        startServiceActions()
    }

    private fun startServiceActions() {
        doBleScan()
    }

    private fun stopService() {
        Log.d("TAG", "Stopping the foreground service")
        mBleUtility.setScanCallback(scanCallBack)
        mBleUtility.stopBleScan()

        stopForeground(true)
        stopSelf()
    }

    var mBleScanTimer: Timer? = null
    private fun doBleScan() {


        val bleState = checkBleState()
        if (!bleState) {
            stopSelf()
            return
        }
        var temp = 1 * 1000.toLong()
        if (mBleScanRestartCounter > 1) {
            temp = 3 * 1000.toLong()
        }
        stopBleScanTimer()
        Thread.sleep(temp)

        Log.i("TAG", "New Ble Scanning started ....")
        mBleScanRestartCounter++
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (scanCallBack == null) {
                scanCallBack = object : ScanCallback() {

                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun onScanResult(callbackType: Int, result: ScanResult) {
                        Log.i("onScanResult", "rssi: "+ result.rssi +"  Mac Address:  "+ result.device.address)
                    }

                    override fun onScanFailed(errorCode: Int) {
                        super.onScanFailed(errorCode)
                        Log.i("onScanResult", "onScanFailed ....")
                    }

                    override fun onBatchScanResults(results: List<ScanResult?>?) {
                        super.onBatchScanResults(results)
                        Log.i("onScanResult", "onBatchScanResults ....")
                    }
                }
                val randomSeconds = (((Random().nextInt(29)) + 30) * 1000).toLong()
                mBleUtility.setScanCallback(scanCallBack)
                mBleUtility.startBleScan()

            }
        }

    }

    /*    mBleScanTimer = Timer()
        mBleScanTimer?.schedule(randomSeconds) {
            logW("Timer hit to stop ble scan ....!")
            stopBleScan()
        }
*/


    private fun checkBleState(): Boolean {
        val bUtility = WiSeMeshBluetoothScanUtility(application)
        if (!bUtility.isBluetoothEnabled) {
            createErrorNotification(
                    getString(R.string.app_name),
                    getString(R.string.warning_msg_enableBle),
                    ERROR_NOTIFICATION_BLUETOOTH_ID
            )
            return false
        }
        return true
    }

    private fun stopBleScan() {
        // val status = scanManager?.stopScan(scanCallBack)
        // Log.w("TAG", "stopBleScan() : Ble Scan has been Stopped and which returned $status ")
        stopBleScanTimer()

    }

    private fun stopBleScanTimer() {
        try {
            mBleScanTimer?.cancel()
            mBleScanTimer = null
            Logger.w(TAG, "stopBleScanTimer() : Ble Scan Timer has been Stopped !! ")
        } catch (ex: Exception) {

        }
    }

    @Throws(Exception::class)
    private fun processScanResult(
            bluetoothDevice: BluetoothDevice?,
            bytes: ByteArray?,
            rssi: Int
    ) {

        try {
            Log.v("TAG", "processScanResult() : BLE SCAN RESULT FOUND....!")

        } catch (ex: Exception) {
            throw ex
        }
    }

    private fun getFormattedDate(date: Long): String {
        val formatter = SimpleDateFormat("dd MMM yyyy hh:mm:ss a")
        val dateString = formatter.format(date)
        return dateString
    }

    private fun getFormattedDate(): String {

        return getFormattedDate(mServiceStartTime)
    }

    private fun createErrorNotification(
            errorDesc: String,
            messageBody: String,
            notificationId: Int = 12341
    ) {

        val subText = getFormattedDate(System.currentTimeMillis())


        val notificationManager = IMSafeNotificationManager(this)
        notificationManager.channelId = "3"
        var channelId = notificationManager.channelId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    channelId,
                    "Demo",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        notificationManager.notificationId = notificationId
        notificationManager.createNotification(errorDesc, messageBody, subText, intent)
        notificationManager.notify(notificationId)

    }

    private fun createServiceStartNotification(): Notification {
        if (mServiceStartTime <= 0) {
            mServiceStartTime = System.currentTimeMillis()
        }
        val subText = getFormattedDate()
        var messageBody = "Service Started"
        messageBody = "$messageBody  $subText"

        return createNotification(
                NORMAL_NOTIFICATION_ID,
                "Service Running",
                subText,
                "",
                getString(R.string.app_name)
        )
    }

    private fun createNotification(
            notificationId: Int,
            title: String,
            subText: String,
            messageBody: String,
            channelName: String
    ): Notification {


        val notificationChannelId = "2"
        imSafeNotificationManager.channelId = notificationChannelId

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                    notificationChannelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = channelName
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            imSafeNotificationManager.createNotificationChannel(channel)
        }


        imSafeNotificationManager.notificationId = notificationId
        val intent = Intent(this, MainActivity::class.java)
        imSafeNotificationManager.createNotification(
                title,
                messageBody,
                subText,
                intent
        )

        return imSafeNotificationManager.notificationBuilder.build()
    }


//    var mBleStateReceiver: BleStateReceiver? = null
//    private fun registerBleStateReciever() {
//        if (mBleStateReceiver == null) {
//            mBleStateReceiver = BleStateReceiver()
//        }
//        val filter2 = IntentFilter()
//        filter2.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
//        filter2.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
//        registerReceiver(mBleStateReceiver, filter2)
//    }
//
//    private fun unRegisterBleStateReciever() {
//        try {
//            unregisterReceiver(mBleStateReceiver)
//
//        } catch (ex: java.lang.Exception) {
//            ex.stackTrace
//        }
//    }

    override fun onSensorChanged(event: SensorEvent?) {
        TODO("Not yet implemented")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }
}


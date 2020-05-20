package com.example.myapplication


import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.time.OffsetTime


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var time: TextView;
    lateinit var btn_start: Button;
    lateinit var start_time: TextView;
    lateinit var status: TextView;
    lateinit var countDownTimer: CountDownTimer
    private var notificationManager: NotificationManager? = null
    private var ssid = ArrayList<String>()
    companion object {
        var mainAct = Activity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        grantPermm()
        startService(this.intent)
        mainAct = this
    }

    override fun startService(service: Intent?): ComponentName? {
        return super.startService(service)
    }

    fun init() {
        time = findViewById(R.id.tv_time);
        start_time = findViewById(R.id.tv_startTime)
        status = findViewById(R.id.tv_status)
        btn_start = findViewById(R.id.btn_start)
        btn_start.setOnClickListener {
            getStartTime()
            countDown()
            this.btn_start.isClickable = false
        }
        createNotificationChannel()
        getWifiSSID()
    }

    //get location permission to obtain ssid info
    private fun grantPermm() {
        try {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) === PackageManager.PERMISSION_GRANTED
            ) {
                init()
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }
        } catch (xx: Exception) {
        }
    }

    fun countDown(){
        countDownTimer = object : CountDownTimer(32400000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                time.setText(
                    "" + (millisUntilFinished / (1000 * 60 * 60)) + ":" +
                            ((millisUntilFinished / (1000 * 60)) % 60) + ":" +
                            ((millisUntilFinished / (1000)) % (60))
                )
                sendNotification()
            }
            override fun onFinish() {
                status.setText("Time's Up!")
                sendNotification()
            }
        }.start()
    }

    fun getWifiSSID() {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo= wifiManager.connectionInfo
        val currentSSID = wifiInfo.ssid
        if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            ssid.add(currentSSID)
            Toast.makeText(applicationContext, currentSSID + "connected", Toast.LENGTH_LONG).show()
            if(currentSSID == "\"TiMOTION - Guest\"" || currentSSID == "\"AndroidWifi\""){
                Log.d("fff", "\"TiMOTION - Guest\" connected")
            }
        }else{
            Log.d("fff", "No Connection")
            Toast.makeText(applicationContext, "No Connection", Toast.LENGTH_SHORT).show()
        }
    }

    //to connect to certain wifi i want
    fun connectToCertainWifi() {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = java.lang.String.format("\"%s\"", "TiMOTION-Guest")
        wifiConfig.preSharedKey = String.format("\"%s\"", "key")

        val wifiManager =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        //remember id
        val netId = wifiManager.addNetwork(wifiConfig)
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, true)
        wifiManager.reconnect()
    }

    fun getStartTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val offset: OffsetTime = OffsetTime.now()
            start_time.setText(
                "Started from: " + ((offset.getHour()).toString() + " : " +
                        offset.getMinute() + " : " +
                        offset.getSecond()).toString()
            )
        }
    }

    fun createNotificationChannel() {

        notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel("0", "NotifyDemo News", importance)

        channel.description = "Example News Channel"
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern =
            longArrayOf(0, 1000)
        notificationManager?.createNotificationChannel(channel)
    }


    fun sendNotification() {

        val notificationID = 101
        val channelID = "0"

        val fullScreenIntent = Intent(this, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder =
            NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.notification_icon_background)
                .setContentTitle(status.text)
                .setContentText(time.text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .build()

        notificationManager?.notify(notificationID, notificationBuilder)
    }
}

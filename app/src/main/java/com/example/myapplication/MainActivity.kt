package com.example.myapplication


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.time.OffsetTime


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var time: TextView;
    lateinit var btn_start: Button;
    lateinit var start_time: TextView;
    lateinit var status: TextView;
    lateinit var countDownTimer: CountDownTimer
    private var notificationManager: NotificationManager? = null
    lateinit var ssid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init() {
        time = findViewById(R.id.tv_time);
        start_time = findViewById(R.id.tv_startTime)
        status = findViewById(R.id.tv_status)
        btn_start = findViewById(R.id.btn_start)
        btn_start.setOnClickListener {
            getStartTime()
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
            this.btn_start.isClickable = false
        }
        createNotificationChannel()
        getWifiSSID()
    }

    fun getWifiSSID() {
        val wifiManager =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo= wifiManager.connectionInfo
        if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            ssid = wifiInfo.ssid
            Log.d("ff", ssid)
            Toast.makeText(applicationContext, ssid, Toast.LENGTH_SHORT).show()
            if(ssid.equals("TiMOTION-Guest")){
                Toast.makeText(applicationContext, "ff", Toast.LENGTH_SHORT).show()
            }
        }

    }

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

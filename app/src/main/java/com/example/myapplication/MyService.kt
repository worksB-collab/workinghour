package com.example.myapplication

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.myapplication.MainActivity.Companion.mainAct
import kotlinx.android.synthetic.main.activity_main.*

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getWifiSSID()
        Log.d("fff2", "okok")
        return super.onStartCommand(intent, flags, startId)
    }

    fun getWifiSSID() {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo= wifiManager.connectionInfo
        val currentSSID = wifiInfo.ssid
        if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            Toast.makeText(applicationContext, currentSSID + "connected", Toast.LENGTH_LONG).show()
            if(currentSSID == "\"TiMOTION - Guest\"" || currentSSID == "\"AndroidWifi\""){
                Log.d("fff", "\"TiMOTION - Guest\" connected")
                mainAct.btn_start.callOnClick()
            }
        }else{
            Log.d("fff", "No Connection")
            Toast.makeText(applicationContext, "No Connection", Toast.LENGTH_SHORT).show()
        }
    }

}

package com.example.imsafedemo

import android.app.ActivityManager
import android.content.Context


class ServiceUtility {


    companion object {
        internal val TAG = javaClass.simpleName

        fun isMyServiceRunning(ctx: Context, serviceClass: Class<*>): Boolean {
            val manager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    Logger.i(TAG, "isMyServiceRunning?=true")
                    return true
                }
            }
            Logger.i(TAG, "isMyServiceRunning?=false")
            return false
        }
    }

}

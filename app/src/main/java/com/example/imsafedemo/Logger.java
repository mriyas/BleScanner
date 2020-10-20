/*
 * WISILICA CONFIDENTIAL
 * __________________
 *
 * [2013] - [2018] WiSilica Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of WiSilica Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to WiSilica Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from WiSilica Incorporated.
 */

package com.example.imsafedemo;

import android.text.TextUtils;
import android.util.Log;


/**
 * Utility class to log.
 * @author Riyas.
 * @since 03 Sep 2015.
 */
public class Logger {

    public static  boolean IS_DEBUG_ENABLED =false;
    public static  boolean IS_FILE_WRITING_ENABLED =false;

    public static void enable(){
        IS_DEBUG_ENABLED= BuildConfig.DEBUG?true:false;
    }
    public static void enableFileWriting(){
        IS_FILE_WRITING_ENABLED=true;
    }

    public static void i(String TAG, String msg) {
        if (IS_DEBUG_ENABLED&& !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(TAG))
            Log.i(TAG, msg);
    }

    public static void v(String TAG, String msg) {
        if (IS_DEBUG_ENABLED&& !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(TAG))
            Log.v(TAG, msg);
    }

    public static void w(String TAG, String msg) {
        if (IS_DEBUG_ENABLED&& !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(TAG))
            Log.w(TAG, msg);


    }

    public static void d(String TAG, String msg) {
        if (IS_DEBUG_ENABLED&& !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(TAG))
            Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (IS_DEBUG_ENABLED&& !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(TAG))
            Log.e(TAG, msg);
    }

}

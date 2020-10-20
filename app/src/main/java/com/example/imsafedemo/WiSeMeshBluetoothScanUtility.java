package com.example.imsafedemo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.ParcelUuid;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bluetooth LE advertising and scanning utilities.
 */
@SuppressLint("NewApi")
public class WiSeMeshBluetoothScanUtility {

    /**
     * Contants
     */
    private static final int REQUEST_ENABLE_BT = 1;

    /**
     * String Constants
     */
    // private static final String BLUETOOTH_ADAPTER_NAME = "Zoku_Android_1";
    static String TAG = "WiSe SDK : WiSeMeshBluetoothScanUtility";
    Context mContext;
    /**
     * Advertising + Scanning Constants
     */
    private boolean scanning;
    private BluetoothGattServerCallback gattServerCallback; // Must implement
    // and set
    private ScanCallback scanCallback; // Must implement and set
    private List<ParcelUuid> serviceUuids;
    /**
     * Bluetooth Objects
     */
    // Activity activity;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

//	Handler _mHandler;


    /**
     * Scanning Objects
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("NewApi")
    public WiSeMeshBluetoothScanUtility(Context c) {
        scanning = false;
        mContext = c;
        //_mHandler = new Handler();
        bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        // bluetoothAdapter.setName(BLUETOOTH_ADAPTER_NAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for gingerbread and newer versions

            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            serviceUuids = new ArrayList<ParcelUuid>();
        }
        //dumb_db();

    }



    /*
     * public void cleanUp() { if (getAdvertising()) stopAdvertise(); if
     * (getScanning()) stopBleScan(); if (gattServer != null)
     * gattServer.close(); }
     */

    // Check if bluetooth is enabled, if not, then request enable
    private void enableBluetooth() {
        try {
            if (bluetoothAdapter == null) {
                // bluetoothState.setText("Bluetooth NOT supported");
            } else if (!bluetoothAdapter.isEnabled()) {
                // bluetoothAdapter.enable();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }


    public boolean isBluetoothEnabled() {
        try {
            if (bluetoothAdapter == null) {
                // bluetoothState.setText("Bluetooth NOT supported");
                return false;
            } else if (!bluetoothAdapter.isEnabled()) {
                // bluetoothAdapter.enable();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                return false;

            } else {
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }
    /*-------------------------------------------------------------------------------*/


    public void setGattServerCallback(BluetoothGattServerCallback callback) {
        gattServerCallback = callback;
    }



    /*-------------------------------------------------------------------------------*/

    public boolean getScanning() {
        // TODO check lescanning boolean
        return scanning;
    }

    public void setScanCallback(ScanCallback callback) {
        scanCallback = callback;
    }

    /**
     * BLE Scanning
     */
    @SuppressLint("NewApi")
    public void startBleScan() {
        if (getScanning())
            stopBleScan();

        enableBluetooth();
        scanning = true;
        ScanFilter.Builder filterBuilder = new ScanFilter.Builder(); // TODO
        // currently
        // devices
        //filterBuilder.setManufacturerData(0x9701,null);

        ScanSettings.Builder settingsBuilder = new ScanSettings.Builder();
        settingsBuilder.setScanMode(2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settingsBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        }
        settingsBuilder.setReportDelay(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && bluetoothAdapter.isLeCodedPhySupported()) {
            settingsBuilder.setLegacy(false);
            settingsBuilder.setPhy(BluetoothDevice.PHY_LE_1M);
        }

        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setManufacturerData(0x0197,null);
        ScanFilter filter = builder.build();
        filters.add(filter);

        builder = new ScanFilter.Builder();
        builder.setManufacturerData(0x0197,new byte[]{});
         filter = builder.build();
        filters.add(filter);

/*        builder = new ScanFilter.Builder();
        builder.setManufacturerData(0x004C, null);
        filter = builder.build();
        filters.add(filter);*/



        builder = new ScanFilter.Builder();
        builder.setManufacturerData(0x004C, new byte[]{});
        filter = builder.build();
        filters.add(filter);


        if (bluetoothLeScanner != null) {
            try {
                bluetoothLeScanner.flushPendingScanResults(scanCallback);
                bluetoothLeScanner.startScan(filters, settingsBuilder.build(), scanCallback);
            } catch (Exception e) {
                Logger.e(TAG, "SCAN FAILED || SCAN FAILED || SCAN FAILED || " + e.toString());
                e.printStackTrace();
            }
            Logger.v(TAG, "SCAN STARTED || SCAN STARTED || SCAN STARTED || " + System.currentTimeMillis());
        }
    }


    public void stopBleScan() {

        try {

            scanning = false;
            if (bluetoothLeScanner != null && scanCallback != null) {
                bluetoothLeScanner.stopScan(scanCallback);
                Logger.e(TAG, "SCAN STOPPED || SCAN STOPPED || SCAN STOPPED || " + System.currentTimeMillis());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}

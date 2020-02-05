package android.example.retrofitex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.example.retrofitex.remote_utilities.WifiList;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.security.Permissions;
import java.util.List;

public class WifiScan extends AppCompatActivity {

    private static final String TAG = WifiScan.class.getName();

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    WifiManager wifiManager;

    BroadcastReceiver wifiScanReceiver;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan);

        setTitle("Wifi Scan");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        recyclerView = findViewById(R.id.wifi_scan_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED,
                        false);

                if (success) {
                    scanSuccess();


                }
                else {
                    Log.d(TAG, "onReceive: Failure");
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
        else {
            startScan();
        }
    }

    private void startScan() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();

        if (!success){
            Log.d(TAG, "onReceive: Failure");
        }
    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();

        final WifiList adapter;
        adapter = new WifiList();
        recyclerView.setAdapter(adapter);

        for (ScanResult scanResult : results) {
            Log.d(TAG, "scanSuccess: "+scanResult.SSID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            startScan();
        }
    }
}

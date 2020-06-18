package android.example.retrofitex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.example.retrofitex.adapter.BLEScanAdapter;
import android.example.retrofitex.adapter.TaskAdapter;
import android.example.retrofitex.models.BLEDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BLEScanActivity extends AppCompatActivity {

    private static final String TAG = BLEScanActivity.class.getName();

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0;
    private static final int REQUEST_ENABLE_BT = 0;

    private boolean isVerifing = false;
    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    BLEScanAdapter adapter;
    private RecyclerView recyclerView;


    List<BLEDevice> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blescan);

        recyclerView = findViewById(R.id.blescan_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BLEScanAdapter(list);
        recyclerView.setAdapter(adapter);


        setTitle("BLE Scan");

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE notSupported", Toast.LENGTH_SHORT).show();
            finish();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        } else {
            initializeButton();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();

            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = (device, rssi, scanRecord) -> {
        Log.d(TAG, ": " + device);

        BLEDevice bleDevice = new BLEDevice();
        bleDevice.setBeaconAddress(device.getAddress());
        bleDevice.setRssi(rssi);
        bleDevice.setTimestamp(System.currentTimeMillis());

        if (!isVerifing) {
            if (!list.contains(bleDevice)) {
                list.add(bleDevice);
                adapter.notifyDataSetChanged();

            }
            else {
                list.get(list.indexOf(bleDevice)).setTimestamp(System.currentTimeMillis());
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void initializeButton() {
        Timer timer = new Timer();

        Button startButton = findViewById(R.id.start_scan);
        startButton.setOnClickListener(v -> {
            bluetoothAdapter.startLeScan(leScanCallback);

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    isVerifing = true;
                    List<BLEDevice> tempList = new ArrayList<>();

                    for (BLEDevice bleDevice : list) {
                        if ((System.currentTimeMillis() - bleDevice.getTimestamp()) > 10000) {
                            tempList.add(bleDevice);

                        }
                    }
                    for (BLEDevice bleDevice : tempList) {
                        list.remove(bleDevice);
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    }
                    isVerifing = false;
                }
            };
            timer.schedule(task, 0, 10000);
        });


        Button button1 = findViewById(R.id.stop_scan);
        button1.setOnClickListener(v -> {
            bluetoothAdapter.stopLeScan(leScanCallback);
            timer.cancel();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }
}


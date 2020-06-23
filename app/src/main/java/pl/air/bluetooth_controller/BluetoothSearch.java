package pl.air.bluetooth_controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BluetoothSearch extends AppCompatActivity {
    private final String TAG = "BL_SEARCH";
    private static UUID MY_UUID = UUID.fromString("72b4b0a8-2d88-4971-b69d-e29c2b9302b3");
    private Integer REQUEST_ENABLE_BT = 1;
    private List<String> mDeviceList;
    private ArrayAdapter<String> adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mBluetoothDeviceList;
    private BluetoothDevice mSelectedDevice;
    private ConnectThread Connection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_search);
        Button btnScan = findViewById(R.id.buttonScan);
        Button btnConnect = findViewById(R.id.buttonConnect);
        ListView listViewDevices = findViewById(R.id.ListViewDevices);
        final TextView txtViewSelectedDevice = findViewById(R.id.textViewSelectedDevice);



        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "Device doesn't support Bluetooth");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mDeviceList = new ArrayList<>();
        mBluetoothDeviceList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,R.layout.devices_list_item, mDeviceList);
        listViewDevices.setAdapter(adapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);


        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.startDiscovery();
            }
        });

        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDevice = (String) parent.getItemAtPosition(position);
                mSelectedDevice = mBluetoothDeviceList.get(position);
                if(selectedDevice != null){
                    txtViewSelectedDevice.setText(selectedDevice);
                }
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedDevice != null){
                    Connection = new ConnectThread(mSelectedDevice, MY_UUID, mBluetoothAdapter, BluetoothSearch.this);
                    Connection.start();
                }

            }
        });

    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                String deviceInfo = deviceName + " " + deviceHardwareAddress;
                if(mDeviceList.isEmpty()){
                    mDeviceList.add(deviceInfo);
                    adapter.notifyDataSetChanged();
                    mBluetoothDeviceList.add(device);
                }
                else {
                    for(int i = 0; i < mDeviceList.size(); i++){
                        if(!mDeviceList.contains(deviceInfo)) {
                            mDeviceList.add(deviceInfo);
                            adapter.notifyDataSetChanged();
                            mBluetoothDeviceList.add(device);
                        }
                    }
                }

            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}

package sloth.twotruthsonelie;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;


/**
 * Created by Daniel on 1/30/2016.
 */
public class JoinLobby extends Activity {

    private static final UUID MY_UUID = UUID.fromString("747cbf8c-fa2d-49e7-9b84-a792ca3e2123");
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int SUCCESS_CONNECT = 0;
    private static final int MESSAGE_READ = 2;
    ArrayAdapter<String> btArrayAdapter;
    ListView devicesLV;
    IntentFilter filter;
    ArrayList<BluetoothDevice> devices;
    BroadcastReceiver mReceiver;
    BluetoothAdapter mBluetoothAdapter;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_CONNECT:
                    ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket) msg.obj);
                    Toast.makeText(getApplicationContext(), "123123123", Toast.LENGTH_SHORT).show();
                    String s = "successfully connected";
                    connectedThread.write(s.getBytes());
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String string = new String(readBuf);
                    Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_lobby_layout);
        init();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(JoinLobby.this, "Bluetooth is not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            turnOnBT();
        } else {

        }

        startDiscovery();
    }

    private void startDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
    }

    private void turnOnBT() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }


    private void init() {
        devices = new ArrayList<BluetoothDevice>();
        devicesLV = (ListView) findViewById(R.id.listView);
        devicesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }

                BluetoothDevice selectedDevice = devices.get(position);
                ConnectThread connect = new ConnectThread(selectedDevice);
                connect.start();

            }
        });
        btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        devicesLV.setAdapter(btArrayAdapter);

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // Ak najde device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Volako ho getne
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Addne ho do listview
                    btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    devices.add(device);

                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    if (mBluetoothAdapter.getState() == mBluetoothAdapter.STATE_OFF) {
                        turnOnBT();
                    }
                }

            }
        };

        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }








    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;


        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {

            // Zrusit vyhladavanie lebo by to spomalilo pripojenie
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
            mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
        }

        private void manageConnectedSocket(BluetoothSocket mmSocket) {
            Intent toGame = new Intent(JoinLobby.this, NextPlayerBluetooth.class);
            startActivity(toGame);
        }


        /**
         * Will cancel an in-progress connection, and close the socket
         */

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }








    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}







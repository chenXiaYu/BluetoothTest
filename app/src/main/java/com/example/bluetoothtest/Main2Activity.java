package com.example.bluetoothtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Main2Activity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> listAdapter;
    private List<String> devices = new ArrayList<>();
    private final UUID MYSELF = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket clientSocket;
    private BluetoothDevice device;
    private OutputStream os;
    private AcceptThread acceptThread;
    private final String NAME = "Bluetooth_Socket";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listView = findViewById(R.id.list_view);

        Set<BluetoothDevice>  bondeDevices = bluetoothAdapter.getBondedDevices();
        if(bondeDevices.size()>0){
            for(BluetoothDevice device: bondeDevices){
                devices.add(device.getName()+":"+device.getAddress());
            }
        }

        listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,devices);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        acceptThread = new AcceptThread();
        acceptThread.start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String device_ = devices.get(position);
        String address = device_.substring(device_.indexOf(":")+1).trim();
        try {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            try {
                if (device == null) {
                    device = bluetoothAdapter.getRemoteDevice(address);

                }
                if (clientSocket == null) {
                    clientSocket = device
                            .createRfcommSocketToServiceRecord(MYSELF);
                    clientSocket.connect();

                    os = clientSocket.getOutputStream();

                }
            } catch (Exception e) {
                clientSocket.close();

                e.printStackTrace();
                // TODO: handle exception
            }
            if (os != null) {
                os.write("发送信息到其他蓝牙设备".getBytes("utf-8"));
            }else {

                os = clientSocket.getOutputStream();
                os.write("发送信息到其他蓝牙设备".getBytes("utf-8"));

            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            System.out.println("信息"+String.valueOf(msg.obj));
            Toast.makeText(Main2Activity.this,String.valueOf(msg.obj),Toast.LENGTH_LONG).show();

        }
    };

    private class AcceptThread extends Thread{
            private BluetoothSocket socket;
            private BluetoothServerSocket serverSocket;
            private InputStream is;
            private OutputStream os;
            public  AcceptThread(){
                try {
                    serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MYSELF);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        @Override
        public void run() {
            super.run();
            try {

                while(true){
                   if(null==socket){
                       try {
                           socket = serverSocket.accept();
                       } catch (IOException e) {
                           Log.e("TAG", "Socket's accept() method failed", e);
                           break;
                       }
                   }
                    if (socket != null) {
                        serverSocket.close();
                        is  = socket.getInputStream();
                        os = socket.getOutputStream();
                        byte[] buffer = new byte[128];
                        int count = is.read(buffer);
                        Message msg = new Message();
                        msg.obj = new String(buffer,0,count,"UTF-8");
                        handler.sendMessage(msg);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

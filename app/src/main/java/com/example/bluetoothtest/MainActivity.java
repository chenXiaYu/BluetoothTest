package com.example.bluetoothtest;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btns;
    private Button btnc;
    private Button search;
    private TextView textView;
    private BluetoothAdapter bluetoothAdapter;
    public final int REQUEST_BOOLETOOTH   = 11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btns = findViewById(R.id.start_blueTooth);
        btnc = findViewById(R.id.close_blueTooth);
        search = findViewById(R.id.search);
        textView  = findViewById(R.id.text_);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(checkSelfPermission("android.permission.BLUETOOTH")!= PackageManager.PERMISSION_DENIED){
//            requestPermissions(new String[]{"android.permission.BLUETOOTH","android.permission.BLUETOOTH_ADMIN"},REQUEST_BOOLETOOTH);
//        }
//        Set<BluetoothDevice> bondDevices = bluetoothAdapter.getBondedDevices();
//        for(BluetoothDevice bd : bondDevices){
//            textView.append(bd.getName()+":"+bd.getAddress()+"\n");
//        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver,intentFilter);


        btns.setOnClickListener(this);
        btnc.setOnClickListener(this);
        search.setOnClickListener(this);
    }


    public void Onclik_Search(){
        setTitle("正在搜索...");
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            setTitle("取消搜索");
        }
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("action:  "+action);
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               if(device!=null){
                   if(device.getBondState()!=BluetoothDevice.BOND_BONDED){
                       textView.append(device.getName()+" "+device.getAddress()+"\n");
                   }
               }
            }else  if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                setTitle("搜索完毕");
            }
        }
    };



    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode==REQUEST_BOOLETOOTH){
//            if(grantResults[0]==PackageManager.PERMISSION_DENIED){
//                Toast.makeText(this,"授权失败",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }



    @Override
    public void onClick(View v) {

//        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(intent,1);
       switch (v.getId()){

           case R.id.close_blueTooth:
               bluetoothAdapter.disable();
               break;
           case  R.id.start_blueTooth:
               bluetoothAdapter.enable();
               break;
           case R.id.search:
               Onclik_Search();
               break;
               default:
                   break;
       }

    }


}

package com.example.myapplication;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.axaet.ibeacon.A_BluetoothLeService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private static final String ACCESS_FINE_LOCATION = "2";
    private BeaconManager beaconManager;
    private int int_count = 0;
    private List<Beacon> beaconList = new ArrayList<>();
    private TextView textView;
    private TextView textcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION}, 2);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        textView = findViewById(R.id.textView);
        textcount = findViewById(R.id.count);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.bind(this);
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
               // List<Beacon> tempList = new ArrayList<>();
                int_count = beacons.size();
                //beaconList.clear();
                beaconList = (List)beacons;
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
               for(Beacon beacon : beaconList){
                   String count  = "beacon:" + int_count;

                   textcount.setText(count);
                   if(beaconList.size() == 0){
                       textView.setText("");
                   }else{
                        Log.e("",""+beacon.getBeaconTypeCode() + " " + beacon.getManufacturer());
                       String beaconinfo = "Name:" + beacon.getBluetoothName() + "\nRSSI:" + beacon.getRssi() + "\nUUID:" + beacon.getId1() + " \nMajor:" + beacon.getId2() +
                               "\nMinor:" + beacon.getId3() + " \nDistance:" + Double.parseDouble(String.format("%.3f", beacon.getDistance())) + "m\n" + beacon.getBeaconTypeCode();
                       textView.setText(beaconinfo);;
                   }
            }
            handler.sendEmptyMessageDelayed(0, 1000); //1초마다 호출
        }
    };
}




package com.example.recevierapp;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    CameraManager mCameraManagerm;
    BluetoothAdapter mBluetoothAdapter;
    WifiManager wifiManager;
    AudioManager audioManager;

    FirebaseDatabase database;
    DatabaseReference mInvoker, mAckSection;
    DatabaseReference refWifi, refFlash, refBluetooth, refRinging;
    DatabaseReference acK_Wifi, ack_Flash, ack_Bluetooth, ack_Ringing;
    @BindView(R.id.rlWIFI)
    RelativeLayout rlWIFI;
    @BindView(R.id.rlRinging)
    RelativeLayout rlRinging;
    @BindView(R.id.rlFlash)
    RelativeLayout rlFlash;
    @BindView(R.id.rlBluetooth)
    RelativeLayout rlBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();


        referenceInitialization();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        permission();


        initialization();


        wifiListener();
        bluetoothListener();
        ringListener();
        flashListener();


    }


    void referenceInitialization() {


        mInvoker = database.getReference("invoker");

        mAckSection = database.getReference("AckSection");

        refWifi = mInvoker.getRef().child("wifi");
        refFlash = mInvoker.getRef().child("flash");
        refBluetooth = mInvoker.getRef().child("bluetooth");
        refRinging = mInvoker.getRef().child("ringing");


        acK_Wifi = mAckSection.getRef().child("wifi");
        ack_Flash = mAckSection.getRef().child("flash");
        ack_Bluetooth = mAckSection.getRef().child("bluetooth");
        ack_Ringing = mAckSection.getRef().child("ringing");


    }


    void initialization() {

        mCameraManagerm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);

    }


    void wifiListener() {
        refWifi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("", "Value is: " + value);

                assert value != null;
                if (Integer.parseInt(value) == 1) {


                    wifi(true);

                }
                if (Integer.parseInt(value) == 0) {

                    wifi(false);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });


    }

    void ringListener() {

        refRinging.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("", "Value is: " + value);

                if (Integer.valueOf(value) == 1) {


                    ringing(true);

                }
                if (Integer.valueOf(value) == 0) {

                    ringing(false);

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });

    }

    void bluetoothListener() {

        refBluetooth.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("", "Value is: " + value);


                if (Integer.valueOf(value) == 1) {


                    bluetooth(true);

                }
                if (Integer.valueOf(value) == 0) {

                    bluetooth(false);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }

    void flashListener() {

        refFlash.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("", "Value is: " + value);

                if (Integer.valueOf(value) == 1) {


                    torch(true);
                }
                if (Integer.valueOf(value) == 0) {

                    torch(false);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }


    public void bluetooth(boolean status) {

        if (status) {
            mBluetoothAdapter.enable();
            setBackGround(rlBluetooth,status);
            setmAckSection(ack_Bluetooth, "1");

        } else {

            mBluetoothAdapter.disable();
            setBackGround(rlBluetooth,status);
            setmAckSection(ack_Bluetooth, "0");

        }
    }


    void permission() {


        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }

    }

    void wifi(Boolean status) {


        if (status) {
            wifiManager.setWifiEnabled(status);
            setBackGround(rlWIFI,status);

            setmAckSection(acK_Wifi, "1");

        } else {
            wifiManager.setWifiEnabled(status);

            setBackGround(rlWIFI,status);
            setmAckSection(acK_Wifi, "0");
        }


    }


    void ringing(boolean status) {
        if (status) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            setBackGround(rlRinging,status);
            setmAckSection(ack_Ringing, "1");
        } else {

            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            setBackGround(rlRinging,status);
            setmAckSection(ack_Ringing, "0");

        }
    }


    void torch(boolean status) {


        String mCameraId = null;
        try {
            mCameraId = mCameraManagerm.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        try {
            mCameraManagerm.setTorchMode(mCameraId, status);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (status) {
            setmAckSection(ack_Flash, "1");
            setBackGround(rlFlash,status);


        } else {
            setmAckSection(ack_Flash, "0");
            setBackGround(rlFlash,status);

        }


    }


    void setmAckSection(DatabaseReference databaseReference, String string) {

        databaseReference.setValue(string);


    }


    void setBackGround(RelativeLayout relativeLayout,Boolean status) {


        if(status){
            relativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.circlegray));

        }
        else{
            relativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.circle));


        }

    }

}
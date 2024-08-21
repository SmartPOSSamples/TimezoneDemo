package com.example.timezonedemo;

import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.wizarpos.wizarviewagentassistant.aidl.ISystemExtApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    private TextView text;
    private final String TAG = "myTag";
    private String timezone;
    private String result;
    private ISystemExtApi systemExtApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);
        Button set = findViewById(R.id.setTimezone);

        set.setOnClickListener(this);

        // Bind the service to disable automatic time zone mode.
        bindSystemExtService();
    }
    public void bindSystemExtService() {
        try {
            startConnectService(MainActivity.this,
                    "com.wizarpos.wizarviewagentassistant",
                    "com.wizarpos.wizarviewagentassistant.SystemExtApiService", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean startConnectService(Context mContext, String packageName, String className, ServiceConnection connection) {
        boolean isSuccess = startConnectService(mContext, new ComponentName(packageName, className), connection);
        return isSuccess;
    }

    protected boolean startConnectService(Context context, ComponentName comp, ServiceConnection connection) {
        Intent intent = new Intent();
        intent.setPackage(comp.getPackageName());
        intent.setComponent(comp);
        boolean isSuccess = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        return isSuccess;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.setTimezone){
            new Thread(() -> {
                try {
                    // Access URL to get IP information, which is a json string.
                    String urlString = "http://ip-api.com/json";
                    URL url = new URL(urlString);
                    URLConnection connection = url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    result = reader.readLine();
                    Log.d(TAG, result);
                    reader.close();

                    // Disable automatic time zone mode before setting time zone.
                    try {
                        systemExtApi.enableAutoTimezone(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Set the time zone based on the time zone information in the Ip address obtained earlier, using the method getTimezone().
                    LocationInfo location = JSONObject.parseObject(result, LocationInfo.class);
                    timezone = location.getTimezone();
                    AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    mAlarmManager.setTimeZone(timezone);

                    // Display time zone information on the screen;
                    runOnUiThread(() -> {
                        text.setText(timezone);
                        Toast toast = Toast.makeText(this, "Success to set time zone!", Toast.LENGTH_SHORT);
                        toast.show();
                    });

                } catch (Exception e) {
                    Toast toast = Toast.makeText(this, "Failed to set time zone!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            systemExtApi = ISystemExtApi.Stub.asInterface(iBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}
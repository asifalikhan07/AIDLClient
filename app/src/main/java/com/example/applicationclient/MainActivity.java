package com.example.applicationclient;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aidlpocdt.IAdd;

import java.util.List;

import com.example.aidlpocdt.Person;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText num1, num2;
    private Button btnAdd, btnNonPremitive, checkConnectivity;
    private TextView total;
    protected IAdd addService;
    private String Tag = "Client Application";
    private String serverAppUri = "com.example.applicationclient";
    private boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        num1 = (EditText) findViewById(R.id.num1);
        num2 = (EditText) findViewById(R.id.num2);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        checkConnectivity = (Button) findViewById(R.id.checkconnectivity);
        checkConnectivity.setOnClickListener(this);

        btnNonPremitive = (Button) findViewById(R.id.btnNonPremitive);
        btnNonPremitive.setOnClickListener(this);

        total = (TextView) findViewById(R.id.total);

        initConnection();
    }

    private void initConnection() {
        if (addService == null) {
            Intent intent = new Intent(IAdd.class.getName());

            /*this is service name which has been declared in the server's manifest file in service's intent-filter*/
            intent.setAction("service.calc");
            intent.setPackage("com.example.aidlpocdt");
            // binding to remote service
            bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(Tag, "Service Connected");
            addService = IAdd.Stub.asInterface((IBinder) iBinder);
            flag = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(Tag, "Service Disconnected");
            addService = null;
            flag = false;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        if (appInstalledOrNot(serverAppUri)) {
            switch (view.getId()) {

                case R.id.btnAdd:
                    if (addService == null) {
                        Toast.makeText(MainActivity.this, "Connection cannot be establish", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (num1.length() > 0 && num2.length() > 0 ) {
                        try {

                            total.setText("");
                            total.setText("Result: " + addService
                                    .addNumbers(Integer.parseInt(num1.getText().toString()),
                                            Integer.parseInt(num2.getText().toString())));


                        } catch (RemoteException e) {
                            e.printStackTrace();
                            Log.d(Tag, "Connection cannot be establish");
                            Toast.makeText(MainActivity.this, "Connection cannot be establish", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Please Enter Both the fields ", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case R.id.btnNonPremitive:
                    try {
                        if (addService != null) {
                            List<String> list = addService.getStringList();
                            for (int i = 0; i < list.size(); i++) {
                                Log.d("List Data: ", list.get(i));
                            }


                            List<Person> person = addService.getPersonList();
                            total.setText("\n" + "Custom Object Data");
                            for (int i = 0; i < person.size(); i++) {
                                total.append(
                                        "\n" + "Person Data: " + "Name:" + person.get(i).name + " Age:" + person
                                                .get(i).age);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Connection cannot be establish", Toast.LENGTH_SHORT).show();
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                        Log.d(Tag, "Connection cannot be establish");
                        Toast.makeText(MainActivity.this, "Connection cannot be establish", Toast.LENGTH_SHORT).show();

                    }
                    break;
                case R.id.checkconnectivity:

                    if (flag) {
                        Toast.makeText(MainActivity.this, "Connection is establish", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Connection is not establish", Toast.LENGTH_SHORT).show();
                    }

                    break;

            }
        } else {
            Toast.makeText(MainActivity.this, "Server App not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (addService == null) {
            initConnection();
        }

    }
}
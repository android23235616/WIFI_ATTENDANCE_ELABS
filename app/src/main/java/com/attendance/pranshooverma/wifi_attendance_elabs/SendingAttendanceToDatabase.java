package com.attendance.pranshooverma.wifi_attendance_elabs;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendingAttendanceToDatabase extends AppCompatActivity {

    WifiManager wifi;
    Context context=this;
    WifiReceiver wifireceiver;
    List<ScanResult> wifiList;
    TextView wf;
    ProgressDialog progress;
    ArrayList wifiname;
    Button exit;

    LocationManager locationManager;
    Boolean gps_flag=false;

    int wifi_flag=0;
    int register_flag=0;
    String subject="null";
    int retry_flag=0;

    List<String>  android=new ArrayList<>();
    List<String>  networking=new ArrayList<>();
    List<String>  communication=new ArrayList<>();
    List<String>  java=new ArrayList<>();
    List<String>  web=new ArrayList<>();
    List<String>  embedded=new ArrayList<>();
    List<String> iot=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sending_attendance_to_database);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        add();

        initialize();


        gettin_current_subject();

        checking_permission();

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void gettin_current_subject() {
        //Boolean flaggy=checking_internet();
       // if(flaggy==false)

        StringRequest str=new StringRequest(Request.Method.POST, Constants.url_getting_subject, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(progress.isShowing())
                {
                    progress.dismiss();
                }

                try {
                    JSONArray as = new JSONArray(response);
                    JSONObject obj=as.getJSONObject(0);


                    String fla;
                    fla=obj.getString("upload");
                    if(fla.equals("0"))
                    {
                        register_flag=0;
                        display(obj.getString("error"));
                        finish();
                    }
                    else
                    {
                        subject=obj.getString("subject");
                        display(subject);
                        register_flag=1;
                        registerbroadcastreceiver();
                        progress.setMessage("Checking Nearby Wifi Networks....");
                        if(!(progress.isShowing()))
                        {
                            progress.show();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Some Issue is there, kindly connect to Elabs wifi and turn off your data then try again", Toast.LENGTH_LONG)
                        .show();
                if(progress.isShowing())
                {
                    progress.dismiss();
                    finish();
                }

            }
        });
        RequestQueue re= Volley.newRequestQueue(getApplicationContext());
        re.add(str);

        str.setRetryPolicy(new DefaultRetryPolicy(10000,2,2));
    }

    private void add() {

        android.add("00:0a:f5:5a:43:94");
        android.add("90:68:c3:cc:c7:3a");
        android.add("88:79:7e:2c:63:5c");
        android.add("88:79:7e:bd:26:9f");
        android.add("7c:46:85:93:af:3d");
        android.add("7c:78:7e:c1:f8:b7");
        android.add("7c:91:22:9d:bd:19");



        iot.add("00:ec:0a:9b:ed:cf");
        iot.add("40:88:05:b1:f3:bd");
        iot.add("88:79:7e:bd:26:9f");
        iot.add("7c:46:85:93:af:3d");
        iot.add("7c:78:7e:c1:f8:b7");
        iot.add("b0:e2:35:54:3c:45");
        iot.add("00:0a:f5:5a:43:94");

        embedded.add("48:50:73:c0:28:2c");
        embedded.add("bc:44:34:6d:59:7d");
        embedded.add("00:23:68:17:9b:60");//router
        embedded.add("7c:78:7e:c1:f8:b7");
        embedded.add("48:88:ca:02:98:a5");
        embedded.add("48:88:ca:1d:d6:bc");
        embedded.add("5c:99:60:58:2c:66");
        embedded.add("5c:f7:c3:74:6e:c2");
        embedded.add("b6:52:7e:35:1c:a0");
        embedded.add("a0:f8:95:32:7a:60");
        embedded.add("7c:46:85:93:af:3d");
        embedded.add("a4:70:d6:3d:ad:59");
        embedded.add("40:88:05:c5:57:5c");
        embedded.add("88:79:7e:bd:26:9f");
        embedded.add("00:0a:f5:5a:43:94");


        networking.add("bc:d1:1f:35:d4:c5");
        networking.add("70:0b:c0:ea:ea:e2");
        networking.add("64:db:43:19:3b:79");
        networking.add("7c:78:7e:c1:f8:b7");
        networking.add("7c:46:85:93:af:3d");
        networking.add("00:0a:f5:5a:43:94");

        java.add("88:79:7e:bd:26:9f");
        java.add("ec:01:ee:b5:a5:49");
        java.add("38:94:96:bd:61:85");
        java.add("88:79:7e:bd:26:9f");
        java.add("c0:ee:fb:56:1a:c7");
        java.add("c8:d7:79:ca:f4:ef");
        java.add("7c:78:7e:c1:f8:b7");
        java.add("00:0a:f5:5a:43:94");
        java.add("bc:d1:1f:2f:85:fa");
        java.add("7c:46:85:93:af:3d");

        communication.add("c0:bd:d1:3b:a1:db");
        communication.add("30:0d:43:de:94:f4");
        communication.add("7c:78:7e:c1:f8:b7");

        communication.add("ac:c1:ee:ab:1a:bd");
        communication.add("00:0a:f5:5a:43:94");
        communication.add("7c:78:7e:c1:f8:b7");
        communication.add("88:79:7e:bd:26:9f");
        communication.add("7c:46:85:93:af:3d");


        web.add("74:8D:08:ab:ea:3c");
        web.add("00:0a:f5:5a:43:94");
        web.add("7c:78:7e:c1:f8:b7");
      //  web.add("00:23:68:17:9b:60");//router
        web.add("c4:3a:be:bf:78:fd");
        web.add("ac:c1:ee:a1:45:87");
        web.add("88:79:7e:bd:26:9f");
        web.add("7c:46:85:93:af:3d");

    }

    private void initialize() {
        wf=(TextView)findViewById(R.id.wifilist);
        wifi= (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
        progress=new ProgressDialog(this);
        exit=(Button)findViewById(R.id.exit) ;
        progress.setMessage("Connecting to Server.....");
        progress.setCancelable(false);
        if(!progress.isShowing())
        {progress.show();}
    }


    private void checking_permission() {

        if(!wifi.isWifiEnabled())
        {
            wifi.setWifiEnabled(true);
  //          registerbroadcastreceiver();
        }
        else{
            //registerbroadcastreceiver();
        }



        int permission1=ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE);
        if(permission1==-1)
        {
            display("Wifi permission not granted");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_WIFI_STATE},Constants.Wifi_state_key);
        }

        int permission2=ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CHANGE_WIFI_STATE);
        if(permission2==-1)
        {
            display("Permission Not granted");
        }


        int permission3=ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.INTERNET);
        if(permission3==-1)
        {
            display("Internet permission not granted");
        }
    }

    private void registerbroadcastreceiver() {

        wifireceiver=new WifiReceiver();
        registerReceiver(wifireceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case Constants.Wifi_state_key:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {

                }
                else
                {
                    display("permission not ganted");
                    finish();
                }
                break;
        }
    }

    private void display(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

     //   if(register_flag==1) {
       //     unregisterReceiver(wifireceiver);
        //}
        if(progress.isShowing())
        {
            progress.dismiss();
        }
    }


    //Making broadcast receiver class
    class WifiReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(progress.isShowing())
            {
                progress.dismiss();
            }


            wifiname=new ArrayList();
            wifiList=wifi.getScanResults();

            StringBuilder builder = new StringBuilder();
            for(int i=0;i<wifiList.size();i++)
            {
                //wifiname.add(new Integer(i+1).toString()+".");

                builder.append(wifiList.get(i).BSSID+","+wifiList.get(i).SSID+"\n");
                wifiname.add(wifiList.get(i).SSID);
                wifiname.add("\n-----------------\n");

                if(subject.equalsIgnoreCase("android")){
                    if(android.contains(wifiList.get(i).BSSID))
                    {   wifi_flag=1;
                        display("android is here");
                        break;
                    }

                }

                if(subject.equalsIgnoreCase("networking")) {
                    if(networking.contains(wifiList.get(i).BSSID)) {
                        wifi_flag = 1;
                        display("networking is here");
                        break;
                    }
                }
                if(subject.equalsIgnoreCase("iot")) {
                    if(iot.contains(wifiList.get(i).BSSID))
                    {
                        wifi_flag=1;
                        display("iot is here");
                        break;
                    }

                }
                if(subject.equalsIgnoreCase("web")) {
                    if(web.contains(wifiList.get(i).BSSID))
                    {
                        display("web is here");
                        wifi_flag = 1;
                        break;
                    }
                }

                if(subject.equalsIgnoreCase("java")) {
                    if(java.contains(wifiList.get(i).BSSID)) {
                        wifi_flag = 1;
                        display("Java is here");
                        break;
                    }
                }
                if(subject.equalsIgnoreCase("embedded")) {
                    if(embedded.contains(wifiList.get(i).BSSID))
                    {   wifi_flag=1;
                        display("embedded is here");
                        break;
                    }
                }
                if(subject.equalsIgnoreCase("communication")) {
                    if(communication.contains(wifiList.get(i).BSSID)) {
                        wifi_flag = 1;
                        display("communication is here");
                        break;
                    }
                }

            }
            wifiList.clear();

            if(wifi_flag==0)
            {
                unregisterReceiver(wifireceiver);
                wf.setText("No devices Found,Kindly connect to  Elabs Wifi,turn off your data.then Try");

            }
            else {
           //     unregisterReceiver(wifireceiver);
                updating_attendance_main_database();
                wifi_flag=0;
            }
            display(builder.toString());

        }
    }

    private Boolean checking_internet()
    {
        ConnectivityManager cm= (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network=cm.getActiveNetworkInfo();
        Boolean check=(network!=null && network.isConnected());
        if(check!=true)
        {
            if(progress.isShowing())
            {progress.dismiss();}

            return false;
        }
        else
        {
            return true;
        }
    }


    private void updating_attendance_main_database()
    {
        progress.setMessage("Updating your attendance....\n Please Wait");
        if(!progress.isShowing())
        {
            progress.show();
        }
        StringRequest str_rq=new StringRequest(Request.Method.POST, Constants.urlo_main_attendance, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(progress.isShowing())
                    progress.dismiss();

                try {
                    display(response);
                    JSONArray as = new JSONArray(response);
                    JSONObject obj=as.getJSONObject(0);
                    String upload_flag=obj.getString("upload");
                    if(upload_flag.equals("0"))
                    {
                        String error=obj.getString("error");
                        display("Sorry There is some error :\n"+error);
                        finish();
                    }
                    else {
                       String time_flag=obj.getString("time");
                        if(time_flag.equalsIgnoreCase("true"))
                        {
                            display("Your Attendance Successfully Uploaded..\nEnjoy");
                            if(progress.isShowing())
                            {
                                progress.dismiss();
                            }
                            wf.setText("Your Attendance Successfully Uploaded..\nEnjoy");

                 //           finish();
                        }
                        else
                        {
                            if(progress.isShowing())
                            {
                                progress.dismiss();
                            }
                            display("You Have already Given your attendance...");
                            wf.setText("You Have already Given your attendance...");
                            //         finish();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progress.isShowing())
                {
                    progress.dismiss();
                }
                display("Error is there: "+error);
                finish();
            }

        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> maps=new HashMap<String,String>();
                maps.put("mac",Constants.MAC);
                return maps;
            }
        };

        RequestQueue as=Volley.newRequestQueue(getApplicationContext());
        as.add(str_rq);
    }


}

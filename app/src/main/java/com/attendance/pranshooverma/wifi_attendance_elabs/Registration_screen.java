package com.attendance.pranshooverma.wifi_attendance_elabs;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;


public class Registration_screen extends AppCompatActivity {

    Button signup,contact_admin;
    EditText rolln;
    Context context=this;
    String MacAddress;
    WifiManager wifi;
    Handler mHandler=new Handler();
    ProgressDialog progress;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration_screen);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        permission_for_ReadPhone();

        initialize();

        int che=sharedPreferences.getInt("flag",0);

        if(che==1)
        {
            Intent next=new Intent(getApplicationContext(),SendingAttendanceToDatabase.class);
            startActivity(next);
            finish();
        }



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             //   checking_internet_permission();

                progress.setMessage("Reaching Server . . . ");

                progress.setCancelable(false);

                progress.show();

                final String roll=rolln.getText().toString();


                if(roll.length()!=7 || roll.trim().equals(0))
                {
                    display("Enter ur roll number Correctly");
                    if(progress.isShowing())
                    {
                        progress.dismiss();
                    }
                }
                else
                {
                    StringRequest str=new StringRequest(Request.Method.POST, Constants.url_registration, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(progress.isShowing())
                            {
                                progress.dismiss();
                            }

                            try {
                                JSONArray a=new JSONArray(response);
                                JSONObject jo=a.getJSONObject(0);

                                int one=Integer.parseInt(String.valueOf(jo.get("upload")));

                                if(one==1)
                                {
                                    display("Registered Successfully");

                                    //setting the flag to be one after the successfully registartion
                                    SharedPreferences.Editor flag=sharedPreferences.edit();
                                    flag.putInt("flag",1);
                                    flag.commit();

                                    Intent next=new Intent(Registration_screen.this,SendingAttendanceToDatabase.class);
                                    startActivity(next);
                                    finish();
                                }
                                else
                                {
                                    display(String.valueOf(jo.get("error")));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            display("Kindly Connect to Elabs Wifi,turn off your data and then try it again");
                            if(progress.isShowing())
                            {
                                progress.dismiss();
                            }
                        }

                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params=new HashMap<String,String>();

                            params.put("roll",roll);
                            params.put("mac",MacAddress);
                            return params;
                        }
                    };


                    RequestQueue re= Volley.newRequestQueue(getApplicationContext());
                    re.add(str);

                    str.setRetryPolicy(new DefaultRetryPolicy(10000,2,2));
                }
            }
        });

        contact_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL,new String[]{"talk02pa@gmail.com","majumdartanmay68@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT,"Kindly reset my account and allow me to login");
                i.putExtra(Intent.EXTRA_TEXT,"Hello student, \n This email ll go to the admin so that they can reset your " +
                        "account and can help you to login \n IDENTIFICATION NUMBER IS  "+MacAddress+"\n Pranshoo Verma 7064002332\n Tanmay Majumdar 7077855182" );
                try{startActivity(Intent.createChooser(i,"Send mail..."));
                }
                catch (android.content.ActivityNotFoundException ex)
                {
                    display("There is no email Client installed in this mobile");
                }

            }
        });
    }

    private void getting_Mac_address() {
            MacAddress = getMacAddr_for_rest();
        Constants.MAC=MacAddress;
  //     display(Constants.MAC);
    }



    public String getMacAddr_for_rest()
    {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }




    private void checking_internet_permission() {
        ConnectivityManager cm= (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network=cm.getActiveNetworkInfo();
        Boolean check=((network!=null && network.isConnected()) );
        if(check!=true)
        {
            display("Kindly on Mobile data or have Internet Connection");
            if(progress.isShowing())
            {
                progress.dismiss();
            }
        }

    }

    private void permission_for_ReadPhone()
    {
        int permission1= ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
        if(permission1==-1)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION},Constants.Read_phone_key);
        }
        else
        {
            getting_Mac_address();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
                {
                    case (Constants.Read_phone_key):
                        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                        {
                            getting_Mac_address();
                        }
                        else
                        {
                            display("Phone Permission not granted");
                            finish();
                        }
                        break;

                }
    }

    private void display(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }


    private void initialize() {

        signup=(Button)findViewById(R.id.sign_up_button);

        contact_admin=(Button)findViewById(R.id.btn_reset_password);

        rolln=(EditText) findViewById(R.id.email);

        sharedPreferences=getSharedPreferences("Elabs_attendance",MODE_PRIVATE);

        progress=new ProgressDialog(this);



    }
}

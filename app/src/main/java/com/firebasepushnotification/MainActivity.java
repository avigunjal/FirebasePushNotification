/***
 * Name: Avinash Gunjal
 * Description: Firebase push notification full demo project
 * Technology Used: Android, FCM, Java, PHP, Volley
 *
 */

package com.firebasepushnotification;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.firebasepushnotification.MyFirebaseMessagingService.latitude;
import static com.firebasepushnotification.MyFirebaseMessagingService.longitude;
import static com.firebasepushnotification.MyFirebaseMessagingService.pref_noti_data;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mSendtokenButton;
    public static String address;
    Boolean isLocationEnable = false;
    TextView locationTV;
    SharedPreferences sp;

    public String token;
    private static final String SERVER_URL = "http://192.168.1.111/fcmDemo/send_notification.php";
    LocationTracker locationTracker;
    Geocoder geocoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(getApplicationContext());

        FirebaseMessaging.getInstance().subscribeToTopic("security");

       /* sp = getApplicationContext().getSharedPreferences(pref_noti_data,MODE_PRIVATE);
         if(sp.contains("latitude_key") && sp.contains("longitude_key")) {
             String mLongitude = sp.getString("latitude_key", "");
             String mLatitude = sp.getString("longitude_key", "");
             if (!mLatitude.isEmpty()) {
                 Toast.makeText(this, mLatitude + mLongitude, Toast.LENGTH_LONG).show();
             }
         }*/

        mSendtokenButton = (Button) findViewById(R.id.btn_sendtoken);
        mSendtokenButton.setOnClickListener(this);
        locationTV = (TextView) findViewById(R.id.tv_location);
        try {
            checkPermission();
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public void checkPermission() {
        //Runtime permission for location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                isLocationEnable = true;
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "Location permission needed to send security alert.", Toast.LENGTH_SHORT).show();
                }

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            100);
                }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLocationEnable==false)
        {
            checkPermission();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_sendtoken) {

            locationTracker = new LocationTracker(this);
            if (locationTracker.canGetLocation) {
                double latitude = locationTracker.getLatitude();
                double longitude = locationTracker.getLongitude();
                String lat = String.valueOf(latitude);
                String lon = String.valueOf(longitude);
                Log.d("Latitude", lat);
                Log.d("Longitude", lon);

                 List<Address> addresses = new ArrayList<>();
                geocoder = new Geocoder(this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    if(latitude!=0.0 && longitude!=0.0) {
                        locationTV.append("Latitude: " + latitude + "\n");
                        locationTV.append("Longitude: " + longitude + "\n");

                        if (addresses.size() != 0) {
                            address = addresses.get(0).getAddressLine(0);
                            locationTV.append("Address Details:\n" + address);
                            sendToken(latitude, longitude, address, "avinash");

                        }
                    }
                    else {
                        Toast.makeText(this, "Location is not available! \nPlease wait for some time!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                } else {
                locationTracker.showSettingsAlert();
            }

        }
    }


    public void sendToken(final double latitude, final double longitude, String address, final String uname){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.fcm_pref), Context.MODE_PRIVATE);
        token = sharedPreferences.getString(getString(R.string.fcm_token),"");

        Log.d("TOKEN",token);
        // volley request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null){
                    Log.d("Alert Sent Success",response.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Alert Sent Error",error.toString());
                Toast.makeText(MainActivity.this, "Error while send notification", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
               // params.put("fcm_token",token);
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("username",uname);

                return params;
            }
        };

        MySingleton.getmInstance(MainActivity.this).addToRequestque(stringRequest);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
       {
            switch (requestCode) {
                case 100: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted, yay! Do the

                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request.
            }
    }
}


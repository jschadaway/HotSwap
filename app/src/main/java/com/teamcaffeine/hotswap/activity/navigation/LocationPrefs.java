package com.teamcaffeine.hotswap.activity.navigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.teamcaffeine.hotswap.R;

import java.io.IOException;



/**
 * Created by Tkixi on 11/25/17.
 */

public class LocationPrefs extends AppCompatActivity
        implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener {
    Button buttonLocation;
    EditText zip;
    TextView msg, result, cancel, save;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationprefs);

        buttonLocation = (Button) findViewById(R.id.buttonLocale);
        zip = (EditText) findViewById(R.id.zipText);
        msg = (TextView) findViewById(R.id.msgLocale);
        result = (TextView) findViewById(R.id.result);
        cancel = (TextView) findViewById(R.id.cancel);
        save = (Button) findViewById(R.id.saveButton);

        // Underlines "Cancel"
        SpannableString content = new SpannableString(cancel.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        cancel.setText(content);


        zip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                zip.setHint("");
            }
        });
        buildGoogleApiClient();
        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sets EditText to current area zip code

                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location= LocationServices.FusedLocationApi.getLastLocation(client);

                if (location == null) {
                    Toast.makeText(getApplicationContext(), "GPS signal not found", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Reasoning: Geocoder kept throwing errors even though properly implemented,
                    // so resorted to sending the coords through Google to get a Json result, and
                    // parsed the Json using Gson to get zipcode
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    Log.i("Lat", Double.toString(lat));
                    Log.i("Lng", Double.toString(lng));

                    String key = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
                    String latitude = Double.toString(lat);
                    String longitude = Double.toString(lng);
                    String api = "&key=AIzaSyCdD6V_pMev1dl8LAsoJ6PLG5JLnR-OiUc";
                    String stringUrl = key+latitude+","+longitude+api;
                    System.out.println(stringUrl);

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(stringUrl).get().build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            // do nothing, POC
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            String jsonData = response.body().string();
                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
                            final String zipcode = jsonObject.getAsJsonArray("results").get(0)
                                    .getAsJsonObject().getAsJsonArray("address_components").get(6)
                                    .getAsJsonObject().get("long_name")
                                    .getAsString();
                            final String city = jsonObject.getAsJsonArray("results").get(0)
                                    .getAsJsonObject().getAsJsonArray("address_components").get(2)
                                    .getAsJsonObject().get("long_name")
                                    .getAsString();
                            LocationPrefs.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    zip.setText(zipcode);
                                    result.setText(city);
                                }
                            });
                        }
                    });
                }
            }
        });

        TextWatcher inputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String area = zip.getText().toString();
                    String key = "https://maps.googleapis.com/maps/api/geocode/json?address=";
                    String api = "&key=AIzaSyCdD6V_pMev1dl8LAsoJ6PLG5JLnR-OiUc";
                    String stringUrl = key + area + api;
                    System.out.println(stringUrl);

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(stringUrl).get().build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            // do nothing, POC
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            String jsonData = response.body().string();
                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
                            final String region = jsonObject.getAsJsonArray("results").get(0)
                                    .getAsJsonObject().getAsJsonArray("address_components").get(1)
                                    .getAsJsonObject().get("long_name")
                                    .getAsString();
                            LocationPrefs.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    result.setText(region);
                                }
                            });
                        }
                    });
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        zip.addTextChangedListener(inputTextWatcher);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sends location preferences back to search page
                Intent goHome = new Intent(LocationPrefs.this, NavigationActivity.class);
                goHome.putExtra("city", result.getText().toString());
                goHome.putExtra("zip", zip.getText().toString());
                startActivity(goHome);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goHome = new Intent(LocationPrefs.this, NavigationActivity.class);
                startActivity(goHome);
            }
        });

    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        client.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, (LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }
}


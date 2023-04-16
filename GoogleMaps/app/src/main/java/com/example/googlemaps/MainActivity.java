package com.example.googlemaps;
import com.google.android.gms.location.LocationResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.LocationCallback;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationRequest locationRequest;
    private RelativeLayout home;
    private RecyclerView recycler;
    private ArrayList<model> itemsList = new ArrayList<>();
    private MyAdapter adapter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int permissionCode = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        home = findViewById(R.id.idHome);

        recycler = findViewById(R.id.recycler_view);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, permissionCode);
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String pagetoken = "";

                    getData(longitude, latitude, pagetoken);
                    adapter = new MyAdapter(MainActivity.this, itemsList);
                    recycler.setAdapter(adapter);
                    recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                    // check for location permission
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // request location updates
                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000); // update location every 10 seconds
                        locationRequest.setFastestInterval(5000); // get the location as fast as possible
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // set the priority of location updates

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                if (locationResult == null) {
                                    return;
                                }
                                for (Location location : locationResult.getLocations()) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();
                                    String pagetoken = "";

                                    getData(longitude, latitude, pagetoken);

                                }
                            }
                        }, null);
                    } else {
                        // request location permission
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, permissionCode);
                    }
                }
            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==permissionCode){
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Allow permissions to view location", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getData(double longitude, double latitude, String pagetoken) {
        home.setVisibility(View.VISIBLE);
        //latitude = 30.288765;
        //longitude = -97.748341;
//        latitude = 32.93119;
//        longitude = -96.71530;
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=100&key=AIzaSyDo4yW0rEgo-VB222CADT3PdxQhCsWMQgE"+ "&pagetoken="+pagetoken;
        new PlaceTask().execute(url);
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.connect();

        InputStream stream = connection.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        StringBuilder builder = new StringBuilder();

        String line = "";

        while((line = reader.readLine()) != null){
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>>{

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String,String>> mapList = null;
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            for(int i = 0; i <hashMaps.size(); i++){
                HashMap<String,String> hashMapList = hashMaps.get(i);
                String name = hashMapList.get("name");
                String latitude = hashMapList.get("latitude");
                String longitude = hashMapList.get("longitude");
                if(name != null || latitude != null || longitude != null) {
                    itemsList.add(new model(name, "lat: " + latitude, "lng: " + longitude));
                }
            }
            create(itemsList);
        }
    }
    public void create(ArrayList<model> list){
        adapter = new MyAdapter(this, itemsList);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }
}

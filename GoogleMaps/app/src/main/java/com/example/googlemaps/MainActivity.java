package com.example.googlemaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout home;
    private RecyclerView recycler;
    private LocationManager locationManager;
    private ArrayList<model> itemsList = new ArrayList<>();
    private MyAdapter adapter;
    private int permissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        home = findViewById(R.id.idHome);

        recycler = findViewById(R.id.recycler_view);

        double latitude = 30.288765;
        double longitude = -97.748341;

        getData(longitude, latitude);
        adapter = new MyAdapter(this, itemsList);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));


    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == permissionCode) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Allow permissions to view location", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }

    private void getData(double longitude, double latitude) {
        home.setVisibility(View.VISIBLE);
        latitude = 30.288765;
        longitude = -97.748341;
//        latitude = 32.93119;
//        longitude = -96.71530;
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=500&key=AIzaSyBsTG0CHdXfAe9q-9gpP34zKxwn_wpiEnM";
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
                String type = hashMapList.get("type");
                String rating = hashMapList.get("rating");
                if(name != null || type != null || rating != null) {
                    itemsList.add(new model(name, rating));
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
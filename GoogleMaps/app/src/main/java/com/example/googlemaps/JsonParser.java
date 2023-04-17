package com.example.googlemaps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private double lat;
    private double lng;
    private double fangle;

    private HashMap<String, String> parseJsonObject(JSONObject object){
        HashMap<String, String> dataList = new HashMap<>();

        try {
            String name = object.getString("name");
//            String type = object.getString("types");
//            String[] split = type.split(",");
            //String rating = object.getString("rating");
            JSONObject geometry = object.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            String latitude = location.getString("lat");
            String longitude = location.getString("lng");

            double x1 = lat; // replace with your origin x value
            double y1 = lng; // replace with your origin y value
            double x2 = Double.parseDouble(latitude); // replace with your new x value
            double y2 = Double.parseDouble(longitude); // replace with your new y value
            double facingAngle = fangle; // replace with your angle value in degrees

            double positiveAngle = facingAngle % 360;
            if (positiveAngle < 0) {
                positiveAngle += 360;
            }

            double dx = x2 - x1; // -0.0008685
            double dy = y2 - y1; // 0.0001338
            double theta = Math.atan2(dy, dx);
            double angle = Math.toDegrees(theta);
            double angleDiff = Math.abs(angle - positiveAngle);

//            System.out.println(angle + "\n" + "difference: " +angleDiff + "\n");
            if (angleDiff <= 40 || angleDiff >= 320) {
//                System.out.println("Within the degrees");
                dataList.put("name", name);
                dataList.put("latitude", latitude);
                dataList.put("longitude", longitude);
            } else {
//                System.out.println("Not within the degrees");
            }

//            dataList.put("name", name);
//            dataList.put("latitude", latitude);
//            dataList.put("longitude", longitude);
            //dataList.put("rating", rating);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }
    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray) throws JSONException {
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for(int i = 0; i <jsonArray.length(); i++){
            HashMap<String,String> data = parseJsonObject((JSONObject) jsonArray.get(i));
            dataList.add(data);
        }
        return dataList;
    }

    public List<HashMap<String, String>> parseResult(JSONObject object, double latitude, double longitude, double angle) throws JSONException {
        JSONArray jsonArray = null;
        lat = latitude;
        lng = longitude;
        fangle = angle;
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }
}

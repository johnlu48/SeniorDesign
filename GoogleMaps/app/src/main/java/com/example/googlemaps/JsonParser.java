package com.example.googlemaps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String, String> parseJsonObject(JSONObject object){
        HashMap<String, String> dataList = new HashMap<>();

        try {
            String  name = object.getString("name");
//            String type = object.getString("types");
//            String[] split = type.split(",");
            String rating = object.getString("rating");
            dataList.put("name", name);
            dataList.put("rating", rating);
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

    public List<HashMap<String, String>> parseResult(JSONObject object) throws JSONException {
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }
}

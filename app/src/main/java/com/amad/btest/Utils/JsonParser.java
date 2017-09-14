package com.amad.btest.Utils;

import android.util.Log;

import com.amad.btest.Entity.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pushparajparab on 9/11/17.
 */

public class JsonParser {

    public static class JsonParse{

        public static ArrayList<Product> Parse(String s) throws JSONException {
            ArrayList<Product> toReturn = new ArrayList<Product>();
            JSONObject jsonObject =new JSONObject(s);
            JSONArray products = jsonObject.getJSONArray("results");


            for(int i =0 ;i<products.length();i++){
                Product toAdd = new Product();
                JSONObject eachObj = products.getJSONObject(i);
                toAdd.setDiscount(eachObj.getInt("Discount"));
                toAdd.setName(eachObj.getString("Name"));
                toAdd.setPhoto(eachObj.getString("Photo"));
                toAdd.setPrice(eachObj.getDouble("Price"));
                toAdd.setRegion(eachObj.getString("Region"));

                toReturn.add(toAdd);
            }


            return  toReturn;
        }
    }
}

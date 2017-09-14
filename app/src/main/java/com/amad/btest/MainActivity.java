package com.amad.btest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.amad.btest.Entity.Product;
import com.amad.btest.Entity.TypeSelect;
import com.amad.btest.Utils.Helper;
import com.amad.btest.Utils.JsonParser;
import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;


import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private String LAST_CALL  = "";
    private BeaconRegion region;
    private final OkHttpClient client = new OkHttpClient();
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private String currentState = "";
    private double lastMin = 2.0;
    long lasttime= System.currentTimeMillis() ;
    private static Map<String, List<Double>> VALUES = new HashMap<>();

    private static final Map<String, String> PLACES_BY_BEACONS;

    static {
        Map<String, String> placesByBeacons = new HashMap<>();
        placesByBeacons.put("15212:31506", "Woodward 3rd floor, in Woodward 332");
        placesByBeacons.put("48071:25324", "Woodward 3rd floor, in front of elevators in trophy stand");
        placesByBeacons.put("26535:44799", "Woodward 3rd floor, in front of elevators in books stand");
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);


        VALUES.put("31506", new ArrayList<Double>());
        VALUES.put("25324", new ArrayList<Double>());
        VALUES.put("44799", new ArrayList<Double>());
    }




    private String placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return "";
    }


    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }


    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }



    private static Map<String, List<Double>> sortByValue(Map<String, List<Double>> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, List<Double>>> list =
                new LinkedList<Map.Entry<String, List<Double>>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String,List<Double>>>() {
            public int compare(Map.Entry<String, List<Double> > o1,
                               Map.Entry<String, List<Double> > o2) {
                return ((Integer)o2.getValue().size()).compareTo((Integer)o1.getValue().size());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, List<Double>> sortedMap = new LinkedHashMap<String, List<Double>>();
        for (Map.Entry<String, List<Double>> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
    }



    public String GetType(String min)
    {
        switch (min)
        {
            case "31506":
                return "grocery";
            case "25324":
                return "lifestyle";
            case "44799":
                return "produce";
            default:
                return "";

        }

    }


    public  String printMap(Map<String, List<Double>> map) {
        String toReturn = "";
        boolean changeList = false;
        String changeFor = "";


        ArrayList<TypeSelect> mList =  new ArrayList<TypeSelect>();

        for (Map.Entry<String, List<Double>> entry : map.entrySet()) {


            toReturn = toReturn + ("Key : " + entry.getKey()
                    + " Value : " + average(entry.getValue()));

            TypeSelect temp = new TypeSelect(average(entry.getValue()),entry.getKey());
            mList.add(temp);

            long time= System.currentTimeMillis();

            if(  time - lasttime > 2000)
            {
                changeList = true;
                lasttime = time;

            }

            if(entry.getValue().size() > 5)
            {
                List<Double> val = VALUES.get(entry.getKey());
                // VALUES.put(entry.getKey(), new ArrayList<Double>());
                VALUES.put("31506", new ArrayList<Double>());
                VALUES.put("25324", new ArrayList<Double>());
                VALUES.put("44799", new ArrayList<Double>());
            }
        }

         Collections.sort(mList, new Comparator<TypeSelect>() {
            @Override
            public int compare(TypeSelect o1, TypeSelect o2) {
                return o1.getAvg().compareTo(o2.getAvg());
            }
        });



        changeFor = mList.get(0).getMinor();
        double dis = mList.get(0).getAvg();



        if(changeList && LAST_CALL != changeFor ) {

            String type = GetType(changeFor);
            // make a call

//            Toast.makeText(MainActivity.this, type
//                  ,Toast.LENGTH_SHORT).show();


            if(dis  == 1000)
            {
                getAllProducts();
                LAST_CALL = changeFor;

            } else if(dis < lastMin) {

                lastMin  = dis;
                LAST_CALL = changeFor;

                Log.d("call", "making call");
                Request request = new Request.Builder()
                        .url(Helper.Api_url.GROCERY_PRODUCTS_TYPE.toString() + type)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        final String responseData = response.body().string();
                        Log.d("Response", responseData);
                        //Run view-related code back on the main thread
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    ArrayList<Product> products = new ArrayList<Product>();
                                    products = JsonParser.JsonParse.Parse(responseData);
                                    Log.d("test", products.toString());
                                    productAdapter.clear();
                                    productAdapter.SetProducts(products);
                                    productAdapter.notifyDataSetChanged();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });


            }
            else {
                lastMin = lasttime  + 1.7;
            }
        }
//        else if( changeList & LAST_CALL != changeFor){
//            LAST_CALL = changeFor;
//            getAllProducts();
//        }



        return toReturn;
    }



    private double average(List<Double> toAvg){

        if(toAvg.size() == 0) return 1000.0;
        double total = 0.0;
        for(int i=0;i<toAvg.size();i++)
        {
            total =  total + toAvg.get(i);
        }

        return  total/toAvg.size();
    }


    private void getAllProducts()
    {
        Log.d("URL",Helper.Api_url.GROCERY_PRODUCTS_TYPE.toString() + "lifestyle");


        Request request = new Request.Builder()
                .url(Helper.Api_url.ALL_PRODUCTS.toString())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                final String responseData = response.body().string();
                Log.d("Response",responseData);
                //Run view-related code back on the main thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ArrayList<Product> products = new ArrayList<Product>();
                            products = JsonParser.JsonParse.Parse(responseData);
                            Log.d("test",products.toString());
                            recyclerView = (RecyclerView) findViewById(R.id.view_products);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
                            recyclerView.getItemAnimator().setRemoveDuration(200);
                            productAdapter = new ProductAdapter(MainActivity.this,R.layout.item_each_product);
                            productAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(productAdapter);
                            productAdapter.SetProducts(products);






                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {

            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
                if (!beacons.isEmpty()  ) {
                    Beacon nearestBeacon = beacons.get(0);
                    String pw ="";
                    for(int i =0; i<beacons.size();i++){

                        double t ;
                        if(beacons.get(i).getMinor() == 31506 && beacons.get(i).getMajor() == 15212)
                        {
                            t =  RegionUtils.computeAccuracy(beacons.get(i));
                            if(VALUES.containsKey("31506"))
                            {
                               List<Double> val = VALUES.get("31506");
                               val.add(t);
                                VALUES.put("31506",val);
                            }
                        }
                        else if((beacons.get(i).getMinor() == 25324) && beacons.get(i).getMajor() == 48071)
                        {
                            t =  RegionUtils.computeAccuracy(beacons.get(i));
                            if(VALUES.containsKey("25324"))
                            {
                                List<Double> val = VALUES.get("25324");
                                val.add(t);
                                VALUES.put("25324",val);
                            }
                        }
                        else if((beacons.get(i).getMinor() == 44799) && beacons.get(i).getMajor() == 26535)
                        {
                            t =  RegionUtils.computeAccuracy(beacons.get(i));
                            if(VALUES.containsKey("44799"))
                            {
                                List<Double> val = VALUES.get("44799");
                                val.add(t);
                                VALUES.put("44799",val);
                            }
                        }
                        else if(beacons.size() == 0)
                        {
                            getAllProducts();
                        }

                        VALUES = sortByValue(VALUES);

//                        if(VALUES.size() == 0)
//                            getAllProducts();
                        printMap(VALUES);
                       // Toast.makeText(MainActivity.this, printMap(VALUES)
                         //       ,Toast.LENGTH_SHORT).show();

                        //


                        if((beacons.get(i).getMinor() == 31506 )
                                ||(beacons.get(i).getMinor() == 25324)
                                ||(beacons.get(i).getMinor() == 44799)
                                )
                        {
                             t =  RegionUtils.computeAccuracy(beacons.get(i));


                            String place = placesNearBeacon( beacons.get(i));
                            pw = pw + place.toString() + " Distance: " +  t + " / ";

                            nearestBeacon = beacons.get(i);



                            int s = beacons.size();

                            // TODO: update the UI here
                            // if(nearestBeacon.getProximityUUID())

                        }
                         else{
                    }       //
                         }


                    //Log.d("Airport", "Nearest places: " + places);




                }
                else
                {
                     getAllProducts();
                }

            }
        });

        region = new BeaconRegion("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

                     getAllProducts();















    }
}

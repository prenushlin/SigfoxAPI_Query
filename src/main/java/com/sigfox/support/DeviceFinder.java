package com.sigfox.support;

import java.lang.Object;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.java.html.leaflet.Map;
import net.java.html.leaflet.event.MouseEvent;
import net.java.html.leaflet.event.MouseListener;
import org.junit.Test;
import org.junit.internal.matchers.Each;
import net.java.html.leaflet.*;
import net.java.html.BrwsrCtx;
import org.netbeans.html.context.impl.CtxImpl;

import javax.swing.text.html.HTML;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;



public class DeviceFinder {
    private static HttpURLConnection conn;
    private String url;

public DeviceFinder(){

}

    public void run() {


        //init vars
        BufferedReader br, reader;
        List<BaseStation> csvBaseStationList = new ArrayList<>();
        List<BaseStation> apiList;
        String deviceID; //Make ArrayList
        List<Device> devices = new ArrayList<>();
        StringBuilder objectInfo;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();


            //Map initialization
              //  BrwsrCtx bc = new BrwsrCtx(new CtxImpl());

           /*  MapOptions mo = new MapOptions()
                      .setCenter(new LatLng( -33.00607,27.89621))
                      .setZoom(15);
            final Map map = new Map("map",mo);

        // add a tile layer to the map
        TileLayerOptions tlo = new TileLayerOptions();
        tlo.setAttribution("Map data &copy; <a href='http://www.thunderforest.com/opencyclemap/'>OpenCycleMap</a> contributors, "
                + "<a href='http://creativecommons.org/licenses/by-sa/2.0/'>CC-BY-SA</a>, "
                + "Imagery © <a href='http://www.thunderforest.com/'>Thunderforest</a>");
        tlo.setMaxZoom(18);
        TileLayer layer = new TileLayer("http://{s}.tile.thunderforest.com/cycle/{z}/{x}/{y}.png", tlo);
        map.addLayer(layer);



// Add a polygon. When you click on the polygon a popup shows up
        Polygon polygonLayer = new Polygon(new LatLng[] {
                new LatLng(48.335067, 14.320660),
                new LatLng(48.337335, 14.323642),
                new LatLng(48.335238, 14.328942),
                new LatLng(48.333883, 14.327612)
        });
        polygonLayer.addMouseListener(MouseEvent.Type.CLICK, new MouseListener() {
            @Override
            public void onEvent(MouseEvent ev) {
                PopupOptions popupOptions = new PopupOptions().setMaxWidth(400);
                Popup popup = new Popup(popupOptions);
                popup.setLatLng(ev.getLatLng());
                popup.setContent("The Leaflet API for Java has been created here!");
                popup.openOn(map);
            }
        });
        map.addLayer(polygonLayer);
*/
/*TODO:
   Add feedback
   Add display map functionality
*/

        //Reading Base Station List from csv provided into ArrList
        try {
            br = new BufferedReader(new FileReader("C:\\Users\\User\\IdeaProjects\\BaseStationAPILocator\\src\\main\\resources\\baseStationSites.csv"));
            br.readLine();
            br.readLine();
            //skip first two lines
            String row;
            while ((row = br.readLine()) != null) {
                br.readLine(); //ignoring ,, lines
                String[] data = row.split(",");

                BaseStation baseStation = new BaseStation(data[0], Double.parseDouble(data[1]), Double.parseDouble(data[2]));
                csvBaseStationList.add(baseStation);
            }
            br.close();



            //Connecting to the sigfox API
            url = "https://api.sigfox.com/v2/devices/";
            API devFind = new API();
            conn = devFind.getAPI(url);


            int status = conn.getResponseCode();
            //If response good
            if (status == 200) {
                objectInfo = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));


                while ((row = reader.readLine()) != null) {
                    objectInfo.append(row);

                }
                reader.close();


                JsonObject jo = gson.fromJson(String.valueOf(objectInfo), JsonObject.class);
                JsonArray ja = jo.getAsJsonArray("data");
                devices = gson.fromJson(ja, new TypeToken<List<Device>>() {}.getType());
                conn.disconnect();
            }
            else{ System.out.println("Error code " + status);
                System.exit(1);
            }

                //***********************************************************//
                //Iterating each device to get their closest base stations
            int dynamicSize = 3; //devices.size();
                for (int i = 0; i < dynamicSize; i++) {

                    //reinitializing apilist and giving some data, apilist is per device
                    apiList = new ArrayList<>();
                    apiList.add(new BaseStation("ZYXZYX", 0, 0));
                    apiList.get(0).setRssi(-1000);


                    //Base Station API query

                    deviceID = devices.get(i).getId(); //add functionality
                    url = "https://api.sigfox.com/v2/devices/" + deviceID + "/messages";
                    devFind = new API();
                    conn = devFind.getAPI(url);
                    status = conn.getResponseCode();

                    if (status == 200) {
                        System.out.println("Response good");
                        objectInfo = new StringBuilder();
                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        while ((row = reader.readLine()) != null) {
                            objectInfo.append(row);
                        }
                        reader.close();


                        JsonObject jsonObject = new JsonObject();


                        jsonObject = gson.fromJson(String.valueOf(objectInfo), JsonObject.class);


                        JsonArray data = jsonObject.getAsJsonArray("data"); //Entire API response under data [ {


                        //Looping through each Data Packet (about 100)

                        for (int k = 0; k < data.size(); k++) {

                            //~100 data elements so grabbing the most recent packet [0] and converting into a jsonObj
                            JsonObject dataObj = data.get(k).getAsJsonObject();

                            JsonArray rinfos = dataObj.getAsJsonArray("rinfos"); //Getting the array of Rinfos data


                            //    System.out.println("RINFOS Array data\n\n"+gson.toJson(rinfos));

                            BaseStation[] messageBs = gson.fromJson(rinfos, BaseStation[].class);

                            //Sort Base Station Array by RSSI -> 0
                            //Arrays.sort(bs, Comparator.comparingDouble(BaseStation::getRssi).reversed());

                            boolean exists =false;

                            //If base stations are repeated keep the RSSI closest to 0 or add it to the list
                            for (BaseStation bs: messageBs) {

                                for (int m=0;m<apiList.size();m++) {  //apiList is not null bc we gave it dummy data
                                    BaseStation api = apiList.get(m);
                                    if (bs.getId().equals(api.getId())) //if base station is already been added
                                    {
                                        if (bs.getRssi()>api.getRssi()) //update RSSI
                                        {
                                            apiList.get(m).setRssi(bs.getRssi());
                                        }
                                        exists=true;
                                        break;
                                    }

                                }
                                //If its a new base station add it to the apilist
                                if (exists==false)
                                {
                                    apiList.add(bs);
                                }
                                //if it was on the list reset the boolean flag
                                exists = false;
                            }

                        }


                        FileWriter fw = new FileWriter(new File("gpsdata_"+deviceID+".txt"));
                        fw.write("name,desc, latitude,longitude,circle_radius\n");
                        //     for each basestation in this packet
                        for (int j = 0; j < apiList.size(); j++) {
                            for (BaseStation csv : csvBaseStationList) {

                                if (apiList.get(j).getId().equals(csv.getId())) {
                                    //Filling in Lat and Lon Co-ords for each base station attached to this device
                                    apiList.get(j).setLat(csv.getLat());
                                    apiList.get(j).setLon(csv.getLon());
                                    break;

                                }
                            }
                            fw.write(apiList.get(j).toStringVis());

                        }
                        System.out.println("File has been written to");
                        fw.close();

                    }
                    else {
                        System.out.println("Status 200 then error code " + status);
                        System.exit(1);
                    }

                }


            } catch(FileNotFoundException e){
                System.out.println("File not found" + Arrays.toString(e.getStackTrace()));
            } catch(MalformedURLException f){
                System.out.println("Malformed url " + Arrays.toString(f.getStackTrace()));
            } catch(IOException e){
                System.out.println("IO exception! ");
                e.printStackTrace();
            }

    }

}
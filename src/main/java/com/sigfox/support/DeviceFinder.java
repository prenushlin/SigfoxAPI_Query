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



    public static void main(String[] args) {


        //init vars
        BufferedReader br, reader;
        List<BaseStation> csvBaseStationList = new ArrayList<>();
        String username = "5e4680733e09fa0d7bf8baba";
        String password = "cf05091ce34c55cf4298c48d1d41c28f";
        String deviceID; //Make ArrayList
        List<Device> devices = new ArrayList<>();



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

        //Reading Base Station List from csv provided into ArrList
        try {
            br = new BufferedReader(new FileReader("C:\\Users\\User\\IdeaProjects\\BaseStationAPILocator\\src\\main\\java\\com\\sigfox\\support\\baseStationSites.csv"));
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

            //Querying list of device IDs
            URL urlDevice = new URL("https://api.sigfox.com/v2/devices/");
            conn = (HttpURLConnection) urlDevice.openConnection();


            //request setup
            conn.setRequestMethod("GET");
         //   System.out.println(conn.getRequestProperty("Limit"));


            //Basic Auth

            conn.setReadTimeout(5000);
            String encoding = Base64.getEncoder().encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty  ("Authorization", "Basic " + encoding);
            conn.connect();


            int status = conn.getResponseCode();
//If response good
            if (status == 200) {
                StringBuilder objectInfo = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));


                while ((row = reader.readLine()) != null) {
                    objectInfo.append(row);

                }
                reader.close();

                System.out.println("\n\nJSON!\n\n");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
               /* JsonObject jo = gson.fromJson(String.valueOf(objectInfo),JsonObject.class);
                JsonArray ja = jo.getAsJsonArray("data");
                devices = gson.fromJson(ja, new TypeToken<List<Device>>(){}.getType());*/
                devices.add(new Device());
                devices.get(0).setId("00338244");
                //***********************************************************//
                //Iterating each device to get their closest base stations
                for (int i = 0; i < devices.size(); i++) {
                }
                //Base Station API query
                conn.disconnect();

                deviceID="00338244"; //devices.get(i).getId(); //add functionality

                URL urlBase = new URL("https://api.sigfox.com/v2/devices/"+deviceID+"/messages"); //https://api.sigfox.com/v2/devices/2F2239/messages");
                conn = (HttpURLConnection) urlBase.openConnection();

                //request setup
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setRequestProperty  ("Authorization", "Basic " + encoding);
                conn.connect();

                status = conn.getResponseCode();

                if (status == 200) {
                    objectInfo = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    while ((row = reader.readLine()) != null) {
                        objectInfo.append(row);
                    }
                    reader.close();


                    JsonObject jsonObject = new JsonObject();

                    gson = new GsonBuilder().setPrettyPrinting().create();
                    jsonObject = gson.fromJson(String.valueOf(objectInfo), JsonObject.class);

                    //  System.out.println(jsonObject.get("data")); //JSON Data


                    JsonArray data = jsonObject.getAsJsonArray("data"); //Entire API response under data [ {

                    FileWriter fw = new FileWriter(new File("gpsdataCompleteRSSIManip.txt"));
                    fw.write("name,desc, latitude,longitude,circle_radius\n");
                    //Looping through each Data Packet
                    for (int i = 0; i < data.size(); i++) {


                        JsonObject dataObj = data.get(i).getAsJsonObject(); //~100 data elements so grabbing the most recent packet [0] and converting into a jsonObj


                        JsonArray rinfos = dataObj.getAsJsonArray("rinfos"); //Getting the array of Rinfos data


                        //    System.out.println("RINFOS Array data\n\n"+gson.toJson(rinfos));

                        BaseStation[] bs = gson.fromJson(rinfos, BaseStation[].class);


                        Arrays.sort(bs, Comparator.comparingDouble(BaseStation::getRssi).reversed());
                        //Sort Base Station Array by RSSI -> 0

                        //Filling in Lat and Lon Co-ords for T3 Base Stations via CSV Base Station List
                        BaseStation[] top3 = Arrays.copyOf(bs, 3);

                    /*for (int j = 0; j < top3.length; j++) {
                        for (BaseStation csv : csvBaseStationList
                        ) {
                            //**improve - exit early after found
                            if (top3[j].getId().equals(csv.getId())) {
                                top3[j].setLat(csv.getLat());
                                top3[j].setLon(csv.getLon());
                            }
                        }
                    }*/

                        BaseStation[] write = bs;




                   //     for each basestation in this packet
                        for (int j = 0; j < write.length; j++) {
                            for (BaseStation csv : csvBaseStationList) {
                                //**improve - exit early after found
                                if (write[j].getId().equals(csv.getId())) {
                                    write[j].setLat(csv.getLat());
                                    write[j].setLon(csv.getLon());

                                }
                            }
                            fw.write(write[j].toStringVis());
                         }



                        //testing one device at a time - commented out for csv writing
                        //   devices.get(0).setBaseStations(top3);


                    }
                    fw.close();
                }
                else{
                        System.out.println("Status 200 then error code " + status);
                    }



                //Printing all Device info
                for (Device de : devices) {
                    System.out.println("de.toString()");
                }



            } else {
                System.out.println("Error code " + status);
            }


        } catch (FileNotFoundException e) {
            System.out.println("File not found" + Arrays.toString(e.getStackTrace()));
        } catch (MalformedURLException f) {
            System.out.println("Malformed url " + Arrays.toString(f.getStackTrace()));
        } catch (IOException e) {
            System.out.println("IO exception! ");
            e.printStackTrace();
        }/* catch (ParseException e) {
            System.out.println("Parsing issue ");
            e.printStackTrace();
        }*/

//API


    }

}

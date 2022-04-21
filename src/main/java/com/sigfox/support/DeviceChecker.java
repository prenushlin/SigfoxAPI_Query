package com.sigfox.support;

//Check if a device is performing normally and if not flag it and display its attributes

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class DeviceChecker {
    private static HttpURLConnection conn;
    private String url;

    //init vars
    BufferedReader reader;
    String deviceID;
    List<Device> devices, flagged = new ArrayList<>();
    StringBuilder objectInfo;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    int year, month;

    public DeviceChecker()
    {

    }

    public void run() throws IOException {

        //Connecting to the sigfox API
        url = "https://api.sigfox.com/v2/devices/";
        API devCheck = new API();

        //Get a list of all devices
        conn = devCheck.getAPI(url);


        int status = conn.getResponseCode();
        //If response good
        if (status == 200) {
            objectInfo = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String row;
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

        for (int i = 0; i < devices.size(); i++) {
            devCheck = new API();
            url = "https://api.sigfox.com/v2/devices/"+devices.get(i).getId()+"/messages/metric";
            conn = devCheck.getAPI(url);
            if (conn.getResponseCode()==200)
            {
              //check the number of messages sent
                objectInfo = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String row;
                while ((row = reader.readLine()) != null) {
                    objectInfo.append(row);

                }
                reader.close();


                Messages message = gson.fromJson(String.valueOf(objectInfo), Messages.class);
                devices.get(i).setMessages(message);
                checkAbnormalMessages(devices.get(i));

                conn.disconnect();
            }
            else{
                System.out.println(conn.getResponseCode());
                System.exit(1);
            }

         //   url = "https://api.sigfox.com/v2/devices/"+devices.get(i).getId()+"/consumption/"+year+"/"+month;
        }

        for (Device de:flagged) {
            int [] cons = de.getMessages();
            System.out.println("\nID: "+de.getId()+"\nConsumption\nDay: "+cons[0]+"\nWeek: "+cons[1]+"\nMonth: "+cons[2]);
        }

    }

    public void checkAbnormalMessages(Device device)
    {
        int month=device.getMessages()[2];
        if (month<50 && month>0)
        {
            flagged.add(device);
        }
    }

}

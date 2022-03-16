
package com.sigfox.support;


//Class for resubscribing a list of sigfox devices that are/will be active and (will) need to be billed.
//Query for Franklin Francisco as per 04/03/22

import java.lang.Object;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.*;

public class DeviceResubscribe {
    private static HttpURLConnection conn;

//class used to resubscribe devices based on a list of their IDs

    public static void main(String [] args) throws IOException {
        //init vars
        BufferedReader br, reader;
        String deviceID; //Make ArrayList
        List<Device> devicesList = new ArrayList<>();
        String url, jsonInput;


        try {

            br = new BufferedReader(new FileReader("C:\\Users\\User\\IdeaProjects\\BaseStationAPILocator\\src\\main\\java\\com\\sigfox\\support\\Devices-to-be-resubscribed.csv"));
            //skip first line for this particular file
            br.readLine();

            String row;
            while ((row = br.readLine()) != null) {

                String[] data = row.split(";");

                Device device = new Device(data[0]); //add vals
                devicesList.add(device);
            }
            br.close();
          //  System.out.println(devicesList.size()+"\n"+devicesList.toString());



            Gson gson = new GsonBuilder().setPrettyPrinting().create();

           ArrayList<String>jsonInput2 = new ArrayList<>();

           //hard coded the start of the json object for the POST API
           jsonInput="{\n" + "\"data\": ";


            for (int i = 0; i < devicesList.size(); i++) {
                jsonInput2.add(devicesList.get(i).getId());
            }
            String finalInput=(jsonInput+gson.toJson(jsonInput2)+"\n}");
            System.out.println(finalInput);

            url = "https://api.sigfox.com/v2/devices/bulk/restart";
            API postAPI = new API();
            conn = postAPI.postAPI(url,finalInput);

            int status = conn.getResponseCode();
            //If response good
            if (status == 200) {
                StringBuilder objectInfo = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((row = reader.readLine()) != null) {
                    objectInfo.append(row);

                }
                reader.close();

                //use Gson to output
                System.out.println(String.valueOf(objectInfo));
                System.out.println(gson.fromJson(String.valueOf(objectInfo),Object.class));



/*
62220272b30c0d7e4616aab2


JobID bulk restart: "jobId":"6225c8caa4da5e3db4e7a074"
 */




            }
            conn.disconnect();


        }
        catch (FileNotFoundException e) {
            System.out.println("File not found" + Arrays.toString(e.getStackTrace()));
        } catch (MalformedURLException f) {
            System.out.println("Malformed url " + Arrays.toString(f.getStackTrace()));
        } catch (IOException e) {
            System.out.println("IO exception! ");
            e.printStackTrace();
        }


    }




}

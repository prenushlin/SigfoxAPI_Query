package com.sigfox.support;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class API {


    private static HttpURLConnection conn;
    final private String username = "5e4680733e09fa0d7bf8baba";
    final private String password = "cf05091ce34c55cf4298c48d1d41c28f";


    public API()
    {

    }

    //Connecting to the sigfox API
    public HttpURLConnection getAPI(String url) throws IOException {


        //Querying list of device IDs
        URL urlDevice = new URL(url);
        conn = (HttpURLConnection) urlDevice.openConnection();


        //request setup
        conn.setRequestMethod("GET");



        //Basic Auth
        conn.setReadTimeout(5000);
        String encoding = Base64.getEncoder().encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8));
        conn.setRequestProperty  ("Authorization", "Basic " + encoding);
        conn.connect();

        return conn;
    }
    public HttpURLConnection postAPI(String url, String jsonInputString) throws IOException {


        //1. Creating URL object
        URL urlDevice = new URL(url);

        //2. Open connection
        conn = (HttpURLConnection) urlDevice.openConnection();

        //3. Set request method
        conn.setRequestMethod("POST");

        //4. Basic Auth API key details
        String encoding = Base64.getEncoder().encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8));
        conn.setRequestProperty  ("Authorization", "Basic " + encoding);

        //5. Set the Request Content-Type Header Parameter
        conn.setRequestProperty("Content-Type", "application/json; utf-8");

        //6. Set the response format type
        conn.setRequestProperty("Accept", "application/json");

        //7. Enable doOutput to ensure the connection will be used to send content
        conn.setDoOutput(true);

        //8. Create request body (payload we're sending)
        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }



        conn.setReadTimeout(5000);

        conn.connect(); //might be unneccessary if operations above implicitly called the connection
        return conn;
    }


}












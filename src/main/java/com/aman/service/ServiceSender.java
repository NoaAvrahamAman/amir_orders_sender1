package com.aman.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.modelmapper.ModelMapper;

import com.aman.dto.DtoOrder;
import com.aman.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.aman.App;

public class ServiceSender implements IServiceSender {
    private final ModelMapper modelMapper;
    private final Gson gson;
    private static final String CONFIG_FILE_NAME = "np.properties";
    private Properties properties;

    public ServiceSender() {
        loadProperties();
        this.modelMapper = new ModelMapper();
        this.gson = new Gson();
    }

    @Override
    public String obtainOrders() {
        
        String jsonData = runAPIgetOrders();
        Type orderListType = new TypeToken<List<DtoOrder>>() {
        }.getType();
        List<DtoOrder> dtoOrders = gson.fromJson(jsonData, orderListType);
        List<DtoOrder> ApprovedDtoOrders=new ArrayList<DtoOrder>();

        for (DtoOrder dtoOrder : dtoOrders) {
            if (dtoOrder.getSTATDES().equals("מאושר כרמל")) {
                ApprovedDtoOrders.add(dtoOrder);
            }
            if (dtoOrder.getSTATDES().equals("מאושר")) {
                ApprovedDtoOrders.add(dtoOrder);
            }
            runAPIupdStatus(dtoOrder.getSUPORDNUM());
        }

        List<Order> orders = ApprovedDtoOrders.stream().map(e -> modelMapper.map(e, Order.class)).toList();

        Order.createXMLFile(properties.getProperty("AmirOrdersfileName"), properties.getProperty("AmirOrdersINFolder"), orders);
        return "";
    }


       
    private  String runAPIgetOrders(){

        String url = properties.getProperty("URLGetAmirOrders");  // Replace with your URL
        String user = properties.getProperty("userAmirOrders");  // Replace with your username
        String password = properties.getProperty("passwordAmirOrders");  // Replace with your password

        try {
            // Create URL object
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set request method
            con.setRequestMethod("GET");

            // Add Authorization header
            String credentials = user + ":" + password;
            String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
            String basicAuth = "Basic " + encodedCredentials;
            con.setRequestProperty("Authorization", basicAuth);

            // Get response code
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        
    }

    private void loadProperties() {
        properties = new Properties();
        try {
            String jarPath = App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            jarPath = "file:" + jarPath;
            String configFilePath = Paths.get(new URI(jarPath)).getParent().resolve(CONFIG_FILE_NAME).toString();

            try (InputStream input = new FileInputStream(configFilePath)) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("Unable to load properties file: " + e.getMessage());
            }
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private  void runAPIupdStatus(String orderNum){

        String url = properties.getProperty("URLupdStatusAmirOrders");  // Replace with your URL
        String user = properties.getProperty("userAmirOrders");  // Replace with your username
        String password = properties.getProperty("passwordAmirOrders");  // Replace with your password

        String jsonInput="{\"STATDES\":\"נשלח לכרמל\"}";
    

        try {
            // Create URL object
            url+="('"+orderNum+"')";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set request method
            con.setRequestMethod("GET");

            // Add Authorization header
            String credentials = user + ":" + password;
            String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
            String basicAuth = "Basic " + encodedCredentials;
            con.setRequestProperty("Authorization", basicAuth);
            OutputStream outputStream = con.getOutputStream();
            outputStream.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            

            // Get response code
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            
            System.out.println("response code: "+responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}

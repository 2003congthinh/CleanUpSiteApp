package mypackage.apitesting;
// All of POST and GET functions are from Week 4
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpHandler {
    public static String loginEmail = "";
//    public static String homeName = Home.name;
    static String URL = "https://elegant-dove-sombrero.cyclic.app";
//GET
    public static String getRequest(){
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(URL + "/printAllData");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null){
                builder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String getSite(){
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(URL + "/printAllSites");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null){
                builder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

//POST
    public static String postRequestLogin(String email, String password){
        String status = "";
        try {
            // Step 1 - prepare the connection
            URL url = new URL(URL + "/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            // Step 2 - prepare the JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            // Step 3 - Writing data to the web service
            try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                os.writeBytes(jsonObject.toString());
                os.flush();
            }
            // Step 4 - Read the response code and message
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();
            // Handle the response appropriately
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully connected
                loginEmail = email;
                status = "Success: " + responseMessage;
            } else {
                // Handle other response codes
                status = "Error - " + responseCode + ": " + responseMessage;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            status = "Error - MalformedURLException: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            status = "Error - IOException: " + e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            status = "Error - JSONException: " + e.getMessage();
        }
        return status;
    }

    public static String postRequestSignup(String email, String password, String name){
        String status = "";
        try {
            // Step 1 - prepare the connection
            URL url = new URL(URL + "/signup");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            // Step 2 - prepare the JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("name", name);
            // Step 3 - Writing data to the web service
            try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                os.writeBytes(jsonObject.toString());
                os.flush();
            }
            // Step 4 - Read the response code and message
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();
            // Handle the response appropriately
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully connected
                status = "Success: " + responseMessage;
            } else {
                // Handle other response codes
                status = "Error - " + responseCode + ": " + responseMessage;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            status = "Error - MalformedURLException: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            status = "Error - IOException: " + e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            status = "Error - JSONException: " + e.getMessage();
        }
        return status;
    }

    public static String postRequestCreateSite(String siteName, double siteLatitude, double siteLongitude, String owner){
        String status = "";
        try {
            // Step 1 - prepare the connection
            URL url = new URL(URL + "/createSite");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            // Step 2 - prepare the JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", siteName);
            jsonObject.put("site_latitude", siteLatitude);
            jsonObject.put("site_longitude", siteLongitude);
            jsonObject.put("owner", owner);
            // Step 3 - Writing data to the web service
            try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                os.writeBytes(jsonObject.toString());
                os.flush();
            }
            // Step 4 - Read the response code and message
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();
            // Handle the response appropriately
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully connected
                status = "Success: " + responseMessage;
            } else {
                // Handle other response codes
                status = "Error - " + responseCode + ": " + responseMessage;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            status = "Error - MalformedURLException: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            status = "Error - IOException: " + e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            status = "Error - JSONException: " + e.getMessage();
        }
        return status;
    }

    public static String getMySite(String owner) {
        StringBuilder builder = new StringBuilder();
        try {
            // Step 1 - prepare the connection
            URL url = new URL(URL + "/printMySites");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Step 2 - prepare the JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("owner", owner);

            // Step 3 - Writing data to the web service
            try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                os.writeBytes(jsonObject.toString());
                os.flush();
            }

            // Step 4 - Read the response code and message
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();

            // Handle the response appropriately
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully connected
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                // Handle other response codes
                builder.append("Error - ").append(responseCode).append(": ").append(responseMessage);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            builder.append("Error - MalformedURLException: ").append(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            builder.append("Error - IOException: ").append(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            builder.append("Error - JSONException: ").append(e.getMessage());
        }

        return builder.toString();
    }

    public static String getOneSite(String name) {
        StringBuilder builder = new StringBuilder();
        try {
            // Step 1 - prepare the connection
            URL url = new URL(URL + "/findSearchSites");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Step 2 - prepare the JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);

            // Step 3 - Writing data to the web service
            try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                os.writeBytes(jsonObject.toString());
                os.flush();
            }

            // Step 4 - Read the response code and message
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();

            // Handle the response appropriately
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully connected
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                // Handle other response codes
                builder.append("Error - ").append(responseCode).append(": ").append(responseMessage);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            builder.append("Error - MalformedURLException: ").append(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            builder.append("Error - IOException: ").append(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            builder.append("Error - JSONException: ").append(e.getMessage());
        }

        return builder.toString();
    }

    public static String joinInSite(String id, String name){
        String status = "";
        try {
            // Step 1 - prepare the connection
            URL url = new URL(URL + "/joinInSite");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            // Step 2 - prepare the JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            // Step 3 - Writing data to the web service
            try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                os.writeBytes(jsonObject.toString());
                os.flush();
            }
            // Step 4 - Read the response code and message
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();
            // Handle the response appropriately
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully connected
                status = "Success: " + responseMessage;
            } else {
                // Handle other response codes
                status = "Error - " + responseCode + ": " + responseMessage;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            status = "Error - MalformedURLException: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            status = "Error - IOException: " + e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            status = "Error - JSONException: " + e.getMessage();
        }
        return status;
    }

    public static String getAccount(String email) {
        StringBuilder builder = new StringBuilder();
        try {
            // Step 1 - prepare the connection
            URL url = new URL(URL + "/getAccount");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Step 2 - prepare the JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);

            // Step 3 - Writing data to the web service
            try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                os.writeBytes(jsonObject.toString());
                os.flush();
            }

            // Step 4 - Read the response code and message
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();

            // Handle the response appropriately
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully connected
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                // Handle other response codes
                builder.append("Error - ").append(responseCode).append(": ").append(responseMessage);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            builder.append("Error - MalformedURLException: ").append(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            builder.append("Error - IOException: ").append(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            builder.append("Error - JSONException: ").append(e.getMessage());
        }

        return builder.toString();
    }

    public static String getSiteDetails(String id) {
        StringBuilder builder = new StringBuilder();
        try {
            // Step 1 - prepare the connection
            URL url = new URL(URL + "/getSiteDetails");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Step 2 - prepare the JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);

            // Step 3 - Writing data to the web service
            try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                os.writeBytes(jsonObject.toString());
                os.flush();
            }

            // Step 4 - Read the response code and message
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();

            // Handle the response appropriately
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully connected
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                // Handle other response codes
                builder.append("Error - ").append(responseCode).append(": ").append(responseMessage);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            builder.append("Error - MalformedURLException: ").append(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            builder.append("Error - IOException: ").append(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            builder.append("Error - JSONException: ").append(e.getMessage());
        }

        return builder.toString();
    }
}

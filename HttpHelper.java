package com.network;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    private static final int TIMEOUT_MILLISECONDS = 5000;

    // Send a GET request and return the HTTP status code (-1 if it failed)
    public static int getStatusCode(String urlAddress) {

        try {
            URL url = new URL(urlAddress);

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MILLISECONDS);
            connection.setReadTimeout(TIMEOUT_MILLISECONDS);

            // Some sites refuse requests without a browser-like user agent
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int statusCode = connection.getResponseCode();

            connection.disconnect();

            return statusCode;

        } catch (Exception exception) {

            return -1;
        }
    }

    // Short explanation of the status codes used in the lab
    public static String getStatusMessage(int statusCode) {

        switch (statusCode) {
            case 200:
                return "200 OK";
            case 301:
                return "301 Moved Permanently (redirect)";
            case 302:
                return "302 Found (redirect)";
            case 400:
                return "400 Bad Request";
            case 401:
                return "401 Unauthorized - authentication is required";
            case 403:
                return "403 Forbidden";
            case 404:
                return "404 Not Found";
            case 500:
                return "500 Internal Server Error";
            case -1:
                return "No response (no connection or timeout)";
            default:
                return "HTTP status code " + statusCode;
        }
    }
}

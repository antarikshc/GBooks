package com.antarikshc.gbooks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ConnectAPI {

    private static final String LOG_TAG = ConnectAPI.class.getSimpleName();

    /**
     * Constructor to prevent making object of class
     **/
    private ConnectAPI() {
    }

    /**
     * to be called from other activities, returns BookData object
     **/
    public static ArrayList<BookData> fetchBookData(String urls) {

        if (urls.isEmpty() || urls == null) {
            return null;
        }

        // Create URL object
        URL url = createUrl(urls);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        ArrayList<BookData> fetchedData = extractBooks(jsonResponse);

        return fetchedData;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error while creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Get JSON response from server as InputSteam and store as String
     **/
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Parse JSON response and extract items to be stored in BookData
     **/
    private static ArrayList<BookData> extractBooks(String jsonResponse) {

        // Create an empty ArrayList that we can start adding books to
        ArrayList<BookData> books = new ArrayList<>();

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject root = new JSONObject(jsonResponse);

            JSONArray items = root.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {

                JSONObject itemsObject = items.getJSONObject(i);
                JSONObject volumeInfo = itemsObject.getJSONObject("volumeInfo");

                String title = volumeInfo.getString("title");

                //Initialize String author as null and add if statement
                //In some countries, API is returning no authors for books.
                String author = null;
                if (volumeInfo.has("authors") && !volumeInfo.isNull("authors")) {
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                    author = authorsArray.getString(0);
                }

                String publisher = null;
                //check before adding
                if (volumeInfo.has("publisher") && !volumeInfo.isNull("publisher")) {
                    publisher = volumeInfo.getString("publisher");
                }

                //validate if book has description
                String desc;
                if (volumeInfo.isNull("description")) {
                    desc = "Description not available.";
                } else {
                    desc = volumeInfo.getString("description");
                }

                String previewUrl = volumeInfo.getString("previewLink");

                JSONObject sale = itemsObject.getJSONObject("saleInfo");

                //Check if book is on sale or not, else give values and handle it in customAdapter
                String onSale = sale.getString("saleability");

                Double price;
                String buyUrl;
                String currency = null;
                if (onSale.equals("FOR_SALE")) {
                    JSONObject retail = sale.getJSONObject("retailPrice");
                    currency = retail.getString("currencyCode");
                    price = retail.getDouble("amount");
                    buyUrl = sale.getString("buyLink");
                } else {
                    price = null;
                    buyUrl = "N/A";
                }


                //getting the thumbnail if available
                String imgUrl = null;
                if (volumeInfo.has("imageLinks") && !volumeInfo.isNull("imageLinks")) {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    imgUrl = imageLinks.getString("smallThumbnail");
                }

                Bitmap coverImage = null;
                // Create URL object
                if (imgUrl != null) {
                    URL url = createUrl(imgUrl);
                    try {
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        InputStream inputStream = connection.getInputStream();
                        coverImage = BitmapFactory.decodeStream(inputStream);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Problem encountered getting image from HTTP url.", e);
                    }
                }

                books.add(new BookData(title, author, desc, price, publisher, previewUrl, imgUrl, buyUrl, currency, coverImage, "dummy"));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the books JSON results", e);
        }

        // Return the list of books
        return books;
    }

}
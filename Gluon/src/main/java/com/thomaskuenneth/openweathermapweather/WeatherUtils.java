/*
 * WeatherUtils.java
 * Copyright 2016 Thomas Kuenneth
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thomaskuenneth.openweathermapweather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provides access to OpenWeatherMap (<a
 * href="http://openweathermap.org/">http://openweathermap.org/</a>). You need
 * to obtain an api key and insert it in a new resource file called
 * /com/thomaskuenneth/openweathermapweather/api_key.properties. It consists of
 * one key-value-par <code>api_key=&lt;your api key&gt;</code>. Please note that
 * this file should not be pushed to a remote repo.
 *
 * @author Thomas Kuenneth
 */
public class WeatherUtils {

    private static final Logger LOGGER = Logger.getLogger(WeatherUtils.class.getName());

    private static final Pattern PATTERN_CHARSET
            = Pattern.compile(".*charset\\s*=\\s*(.*)$");

    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?q={0}&lang=de&APPID=" + getAPIKey();

    private static final String NAME = "name";
    private static final String WEATHER = "weather";
    private static final String DESCRIPTION = "description";
    private static final String ICON = "icon";
    private static final String MAIN = "main";
    private static final String TEMP = "temp";

    public static WeatherData getWeather(String city) throws JSONException,
            MalformedURLException, IOException {
        String name = null;
        String description = null;
        String icon = null;
        Double temp = null;
        JSONObject jsonObject = new JSONObject(
                WeatherUtils.getFromServer(MessageFormat.format(URL, city)));
        if (jsonObject.has(NAME)) {
            name = jsonObject.getString(NAME);
        }
        if (jsonObject.has(WEATHER)) {
            JSONArray jsonArrayWeather = jsonObject.getJSONArray(WEATHER);
            if (jsonArrayWeather.length() > 0) {
                JSONObject jsonWeather = jsonArrayWeather.getJSONObject(0);
                if (jsonWeather.has(DESCRIPTION)) {
                    description = jsonWeather.getString(DESCRIPTION);
                }
                if (jsonWeather.has(ICON)) {
                    icon = jsonWeather.getString(ICON);
                }
            }
        }
        if (jsonObject.has(MAIN)) {
            JSONObject main = jsonObject.getJSONObject(MAIN);
            temp = main.getDouble(TEMP);
        }
        return new WeatherData(name, description, icon, temp);
    }

    public static String getFromServer(String url)
            throws MalformedURLException, IOException {
        StringBuilder sb = new StringBuilder();
        URL _url = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) _url
                .openConnection();
        String contentType = httpURLConnection.getContentType();
        String charSet = "ISO-8859-1";
        if (contentType != null) {
            Matcher m = PATTERN_CHARSET.matcher(contentType);
            if (m.matches()) {
                charSet = m.group(1);
            }
        }
        final int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    httpURLConnection.getInputStream(), charSet);
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "getFromServer()", ex);
            }
        }
        httpURLConnection.disconnect();
        return sb.toString();
    }

    private static String getAPIKey() {
        ResourceBundle bundle = ResourceBundle.getBundle("com.thomaskuenneth.openweathermapweather.api_key");
        return bundle.getString("api_key");
    }
}

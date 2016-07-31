/*
 * MainActivity.java
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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText city;
    private ImageView image;
    private TextView temperatur, beschreibung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = (EditText) findViewById(R.id.city);
        city.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                doIt();
                return true;
            }
        });
        image = (ImageView) findViewById(R.id.image);
        temperatur = (TextView) findViewById(R.id.temperatur);
        beschreibung = (TextView) findViewById(R.id.beschreibung);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doIt();
            }
        });
    }

    private void doIt() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final WeatherData weather = WeatherUtils
                            .getWeather(city.getText().toString());
                    final Bitmap bitmapWeather = getImage(weather);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            image.setImageBitmap(bitmapWeather);
                            beschreibung.setText(weather.description);
                            Double temp = weather.temp - 273.15;
                            temperatur.setText(getString(R.string.temp_template, temp.intValue()));
                            city.setSelection(0, city.getText().length());
                        }

                    });
                } catch (IOException | JSONException ex) {
                    Log.e(TAG, "doIt()", ex);
                }
            }
        }).start();
    }

    private Bitmap getImage(WeatherData w) throws IOException {
        URL req = new URL("http://openweathermap.org/img/w/" + w.icon + ".png");
        return BitmapFactory.decodeStream(req.openConnection()
                .getInputStream());
    }
}

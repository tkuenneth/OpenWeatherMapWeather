/*
 * BasicView.java
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

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONException;

public class BasicView extends View {

    private static final Logger LOGGER = Logger.getLogger(BasicView.class.getName());

    private final ResourceBundle bundle;
    private final TextField city;
    private final ImageView image;
    private final Text beschreibung;
    private final Text temperatur;

    public BasicView(String name) {
        super(name);
        bundle = ResourceBundle.getBundle("com.thomaskuenneth.openweathermapweather.strings");
        city = new TextField();
        city.setFloatText(bundle.getString("hint"));
        Button show = new Button(bundle.getString("anzeigen"));
        image = new ImageView();
        temperatur = new Text();
        beschreibung = new Text();
        VBox texts = new VBox(temperatur, beschreibung);
        HBox hb1 = new HBox(10, image, texts);
        hb1.setPadding(new Insets(10, 0, 0, 0));
        hb1.setAlignment(Pos.TOP_LEFT);
        show.setOnAction(e -> doIt());
        VBox controls = new VBox(10, city, show, hb1);
        controls.setPadding(new Insets(14, 14, 14, 14));
        controls.setAlignment(Pos.TOP_LEFT);
        setCenter(controls);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setTitleText("OpenWeatherMapWeather");
    }

    private void doIt() {
        Task<Void> t = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    final WeatherData weather = WeatherUtils.getWeather(city.getText());
                    Platform.runLater(() -> {
                        Image i = new Image("http://openweathermap.org/img/w/" + weather.icon + ".png");
                        image.setImage(i);
                        beschreibung.setText(weather.description);
                        Double temp = weather.temp - 273.15;
                        temperatur.setText(MessageFormat.format("{0} \u2103", temp.intValue()));
                    });
                } catch (JSONException | IOException ex) {
                    LOGGER.log(Level.SEVERE, "handleButtonAction()", ex);
                }
                return null;
            }
        };
        new Thread(t).start();
    }
}

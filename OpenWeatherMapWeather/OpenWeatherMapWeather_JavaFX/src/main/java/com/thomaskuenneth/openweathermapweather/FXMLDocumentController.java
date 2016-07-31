/*
 * FXMLDocumentController.java
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

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.json.JSONException;

/**
 * This is the main controller of OpenWeatherMapWeather.
 *
 * @author Thomas Kuenneth
 */
public class FXMLDocumentController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(FXMLDocumentController.class.getName());

    @FXML
    private ResourceBundle resources;

    @FXML
    private Button show;

    @FXML
    private URL location;

    @FXML
    private ImageView image;

    @FXML
    private TextField city;

    @FXML
    private Text beschreibung;

    @FXML
    private Text temperatur;

    @FXML
    void handleButtonAction(ActionEvent event) {
        Task<Void> t = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try {
                    final WeatherData weather = WeatherUtils.getWeather(city.getText());
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            Image i = new Image("http://openweathermap.org/img/w/" + weather.icon + ".png");
                            image.setImage(i);
                            beschreibung.setText(weather.description);
                            Double temp = weather.temp - 273.15;
                            temperatur.setText(MessageFormat.format("{0} \u2103", temp.intValue()));
                        }

                    });
                } catch (JSONException | IOException ex) {
                    LOGGER.log(Level.SEVERE, "handleButtonAction()", ex);
                }
                return null;
            }
        };
        new Thread(t).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        show.disableProperty().bind(city.textProperty().isEmpty());
    }
}

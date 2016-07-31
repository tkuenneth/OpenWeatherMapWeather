/*
 * WeatherData.java
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

/**
 * This class represents weather data.
 *
 * @author Thomas Kuenneth
 */
public class WeatherData {

    /**
     * name of city
     */
    public String name;

    /**
     * description
     */
    public String description;

    /**
     * weather icon
     */
    public String icon;

    /**
     * temperature in Kelvin: for Celsius subtract 273.15
     */
    public Double temp;

    public WeatherData(String name, String description, String icon, Double temp) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.temp = temp;
    }
}

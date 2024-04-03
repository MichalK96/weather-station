package michal.api.weatherstationapi.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WeatherDataAverages {

    private double avgTemperature;
    private double avgHumidity;
    private double avgPressure;
    private double avgLightIntensity;
    private double avgUvLevel;
    private double avgPrecipitation;
    private double avgWindSpeed;

}

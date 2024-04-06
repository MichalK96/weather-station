package michal.api.weatherstationapi.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeatherDataAverages {

    private BigDecimal hour;
    private Double avgTemperature;
    private BigDecimal avgHumidity;
    private BigDecimal avgPressure;
    private BigDecimal avgLightIntensity;
    private BigDecimal avgUvLevel;
    private BigDecimal avgPrecipitation;
    private BigDecimal avgWindSpeed;

}

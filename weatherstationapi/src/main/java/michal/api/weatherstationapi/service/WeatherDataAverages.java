package michal.api.weatherstationapi.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeatherDataAverages {

    private Date date;
    private BigDecimal hour;
    private Double avgTemperature;
    private BigDecimal avgHumidity;
    private BigDecimal avgPressure;
    private BigDecimal avgLightIntensity;
    private BigDecimal avgUvLevel;
    private BigDecimal avgPrecipitation;
    private BigDecimal avgWindSpeed;

}

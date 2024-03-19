package michal.api.weatherstationapi.htmlresponse.controller;

import michal.api.weatherstationapi.dao.WeatherReadingDAO;
import michal.api.weatherstationapi.htmlresponse.service.WeatherReadingHtmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("weather-reading-html")
public class WeatherReadingHtmlController {

    private final WeatherReadingHtmlService weatherReadingService;

    @Autowired
    public WeatherReadingHtmlController(WeatherReadingHtmlService weatherReadingService) {
        this.weatherReadingService = weatherReadingService;
    }

    @GetMapping("/{weatherStationName}")
    public String getLastReadingByWeatherStationName(@PathVariable String weatherStationName) {
        return weatherReadingService.getLastReadingByWeatherStationName(weatherStationName);

    }

    @GetMapping("/list/{weatherStationName}")
    public String listByWeatherStationName(@PathVariable String weatherStationName) {
        return weatherReadingService.listByWeatherStationName(weatherStationName);
    }

}

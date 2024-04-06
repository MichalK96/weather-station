package michal.api.weatherstationapi.htmlresponse.controller;

import michal.api.weatherstationapi.htmlresponse.htmlgenerator.WeatherReadingHtmlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("html/weather-reading")
public class WeatherReadingHtmlController {

    private final WeatherReadingHtmlGenerator weatherReadingService;

    @Autowired
    public WeatherReadingHtmlController(WeatherReadingHtmlGenerator weatherReadingService) {
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

    @GetMapping("/hours-summary/{weatherStationName}")
    public String getHoursSummaryByWeatherStationName(@PathVariable String weatherStationName) {
        return weatherReadingService.listHoursSummaryByWeatherStationName(weatherStationName);
    }

}

package michal.api.weatherstationapi.controller;

import michal.api.weatherstationapi.dao.WeatherReadingDAO;
import michal.api.weatherstationapi.service.WeatherReadingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("weather-reading")
public class WeatherReadingController {

    private final WeatherReadingService weatherReadingService;

    public WeatherReadingController(WeatherReadingService weatherReadingService) {
        this.weatherReadingService = weatherReadingService;
    }

    @PostMapping()
    public ResponseEntity<?> save(@RequestBody WeatherReadingDAO weatherReadingDAO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(weatherReadingService.save(weatherReadingDAO));
    }

    @GetMapping("/{weatherStationName}")
    public WeatherReadingDAO getLastReadingByWeatherStationName(@PathVariable String weatherStationName) {
        return weatherReadingService.getLastReadingByWeatherStationName(weatherStationName);
    }

    @GetMapping("/list/{weatherStationName}")
    public List<WeatherReadingDAO> listByWeatherStationName(@PathVariable String weatherStationName) {
        return weatherReadingService.listByWeatherStationName(weatherStationName);
    }

}

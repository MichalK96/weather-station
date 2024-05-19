package michal.api.weatherstationapi.controller;

import michal.api.weatherstationapi.dao.WeatherReadingDAO;
import michal.api.weatherstationapi.service.WeatherDataAverages;
import michal.api.weatherstationapi.service.WeatherReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("weather-reading")
public class WeatherReadingController {

    private final WeatherReadingService weatherReadingService;

    @Autowired
    public WeatherReadingController(WeatherReadingService weatherReadingService) {
        this.weatherReadingService = weatherReadingService;
    }

    @PostMapping()
    public ResponseEntity<?> save(@RequestBody List<WeatherReadingDAO> weatherReadingDAO) {
        try {
        var httpCode = weatherReadingService.save(weatherReadingDAO);
        return ResponseEntity.status(HttpStatus.valueOf(httpCode)).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.valueOf(500)).body(e.getMessage());
        }
    }

    @GetMapping("/{weatherStationName}")
    public ResponseEntity<?> getLastReadingByWeatherStationName(@PathVariable String weatherStationName) {
        try {
            var result = weatherReadingService.getLastReadingByWeatherStationName(weatherStationName);
            if (result == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.valueOf(500)).body(e.getMessage());
        }
    }

    @GetMapping("/list/{weatherStationName}")
    public List<WeatherReadingDAO> listByWeatherStationName(@PathVariable String weatherStationName) {
        return weatherReadingService.listByWeatherStationName(weatherStationName);
    }

    @GetMapping("hours-summary/{weatherStationName}/{hours}")
    public List<WeatherDataAverages> hoursSummary(@PathVariable String weatherStationName, @PathVariable int hours) {
        return weatherReadingService.calculateHourlyAverages(weatherStationName, hours);
    }

    @GetMapping("/current-time")
    public long getCurrentTime() {
        return weatherReadingService.getCurrentTime();
    }

}

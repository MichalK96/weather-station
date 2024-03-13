package michal.api.weatherstationapi.controller;

import michal.api.weatherstationapi.dao.WeatherStationUnitDAO;
import michal.api.weatherstationapi.service.WeatherStationUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("weather-station")
public class WeatherStationUnitController {

    private final WeatherStationUnitService weatherStationUnitService;

    @Autowired
    public WeatherStationUnitController(WeatherStationUnitService weatherStationUnitService) {
        this.weatherStationUnitService = weatherStationUnitService;
    }

    @GetMapping()
    public List<WeatherStationUnitDAO> list() {
        return weatherStationUnitService.list();
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody WeatherStationUnitDAO weatherStationUnitDAO) {
        try {
            var savedWeatherStation = weatherStationUnitService.save(weatherStationUnitDAO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedWeatherStation);
        } catch (Exception e) {
            if (e.getMessage().contains("already exist")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(e.getMessage());
        }
    }

}

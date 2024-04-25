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

    @GetMapping("/{name}")
    public WeatherStationUnitDAO getByName(@PathVariable String name) {
        return weatherStationUnitService.getByNameWithoutPassword(name);
    }

    @PostMapping()
    public ResponseEntity<?> save(@RequestBody WeatherStationUnitDAO weatherStationUnit) {
        try {
            var savedWeatherStation = weatherStationUnitService.save(weatherStationUnit);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedWeatherStation);
        } catch (Exception e) {
            if (e.getMessage().contains("already exist")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } else if (e.getMessage().contains("illegal characters")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

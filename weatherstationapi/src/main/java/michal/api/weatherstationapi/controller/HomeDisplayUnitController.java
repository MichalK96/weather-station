package michal.api.weatherstationapi.controller;

import michal.api.weatherstationapi.dao.HomeDisplayUnitDAO;
import michal.api.weatherstationapi.service.HomeDisplayUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("home-display")
public class HomeDisplayUnitController {

    private final HomeDisplayUnitService homeDisplayUnitService;

    @Autowired
    public HomeDisplayUnitController(HomeDisplayUnitService homeDisplayUnitService) {
        this.homeDisplayUnitService = homeDisplayUnitService;
    }

    @GetMapping()
    public List<HomeDisplayUnitDAO> list() {
        return homeDisplayUnitService.list();
    }

    @PostMapping()
    public ResponseEntity<?> save(@RequestBody HomeDisplayUnitDAO homeDisplayUnit) {
        try {
            var savedHomeDisplayUnit = homeDisplayUnitService.save(homeDisplayUnit);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedHomeDisplayUnit);
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

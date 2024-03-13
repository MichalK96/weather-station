package michal.api.weatherstationapi.controller;

import michal.api.weatherstationapi.dao.HomeDisplayUnitDAO;
import michal.api.weatherstationapi.service.HomeDisplayUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("home-display/")
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

}

package michal.api.weatherstationapi.service;

import michal.api.weatherstationapi.dao.HomeDisplayUnitDAO;
import michal.api.weatherstationapi.dao.WeatherStationUnitDAO;
import michal.api.weatherstationapi.exception.ApiErrorException;
import michal.api.weatherstationapi.repository.HomeDisplayUnitRepository;
import michal.api.weatherstationapi.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HomeDisplayUnitService {

    private final HomeDisplayUnitRepository homeDisplayUnitRepository;
    private final WeatherStationUnitService weatherStationUnitService;

    @Autowired
    public HomeDisplayUnitService(HomeDisplayUnitRepository homeDisplayUnitRepository, WeatherStationUnitService weatherStationUnitService) {
        this.homeDisplayUnitRepository = homeDisplayUnitRepository;
        this.weatherStationUnitService = weatherStationUnitService;
    }

    public List<HomeDisplayUnitDAO> list() {
        return homeDisplayUnitRepository.findAll();
    }

    public HomeDisplayUnitDAO save(HomeDisplayUnitDAO homeDisplayUnit) {
        if (!Util.validateName(homeDisplayUnit.getName())) {
            throw new ApiErrorException("Home display name contains illegal characters or long is not between 2-25 characters");
        }
        var weatherStationUnit = weatherStationUnitService.getByNameWithPassword(homeDisplayUnit.getName());
        homeDisplayUnit.setPassword(weatherStationUnit.getPassword());
        homeDisplayUnit.setWeatherStationUnit(weatherStationUnit);
        homeDisplayUnit.setCreated(LocalDateTime.now());
        if (homeDisplayUnit.getRefreshTimeSec() == 0) {
            homeDisplayUnit.setRefreshTimeSec(60);
        }
        return homeDisplayUnitRepository.save(homeDisplayUnit);
    }

}

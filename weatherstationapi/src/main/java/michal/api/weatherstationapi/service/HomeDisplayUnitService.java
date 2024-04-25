package michal.api.weatherstationapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    @PersistenceContext
    private EntityManager entityManager;

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

    public HomeDisplayUnitDAO getByNameWithoutPassword(String name) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(HomeDisplayUnitDAO.class);
        var root = query.from(HomeDisplayUnitDAO.class);
        query.where(criteriaBuilder.equal(root.get("name"), name));
        query.select(criteriaBuilder.construct(HomeDisplayUnitDAO.class,
                root.get("id"),
                root.get("name"),
                root.get("created"),
                root.get("refreshTimeSec"),
                root.get("brightness")));
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (Exception e) {
            if (e.getMessage().contains("No result found")) {
                return null;
            }
            throw e;
        }
    }

}

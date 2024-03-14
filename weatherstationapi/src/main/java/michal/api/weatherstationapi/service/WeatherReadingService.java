package michal.api.weatherstationapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import michal.api.weatherstationapi.dao.WeatherReadingDAO;
import michal.api.weatherstationapi.repository.WeatherReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherReadingService {

    private final WeatherReadingRepository weatherReadingRepository;
    private final WeatherStationUnitService weatherStationUnitService;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public WeatherReadingService(WeatherReadingRepository weatherReadingRepository, WeatherStationUnitService weatherStationUnitService) {
        this.weatherReadingRepository = weatherReadingRepository;
        this.weatherStationUnitService = weatherStationUnitService;
    }

    public WeatherReadingDAO getLastReadingByWeatherStationName(String weatherStationName) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(WeatherReadingDAO.class);
        var root = query.from(WeatherReadingDAO.class);
        query.orderBy(criteriaBuilder.desc(root.get("created")));
        var typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        return typedQuery.getSingleResult();
    }

    public List<WeatherReadingDAO> listByWeatherStationName(String weatherStationName) {
        return weatherReadingRepository.findAllDistinctByWeatherStationName(weatherStationName);
    }

    public WeatherReadingDAO save(WeatherReadingDAO weatherReading) {
        var weatherStationUnit = weatherStationUnitService.getByNameWithPassword(weatherReading.getWeatherStationName());
        weatherReading.setWeatherStationUnit(weatherStationUnit);
        if (weatherReading.getCreated() == null) {
            weatherReading.setCreated(LocalDateTime.now());
        }
        return weatherReadingRepository.save(weatherReading);
    }

}

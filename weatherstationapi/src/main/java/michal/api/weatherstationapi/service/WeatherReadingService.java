package michal.api.weatherstationapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import michal.api.weatherstationapi.dao.WeatherReadingDAO;
import michal.api.weatherstationapi.dao.WeatherStationUnitDAO;
import michal.api.weatherstationapi.repository.WeatherReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        Join<WeatherReadingDAO, WeatherStationUnitDAO> weatherStationJoin = root.join("weatherStationUnit");
        query.where(criteriaBuilder.equal(weatherStationJoin.get("name"), weatherStationName));

        query.orderBy(criteriaBuilder.desc(root.get("created")));
        var typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        return typedQuery.getSingleResult();
    }

    public List<WeatherReadingDAO> listByWeatherStationName(String weatherStationName) {
        var weatherStationUnit = weatherStationUnitService.getByNameWithoutPassword(weatherStationName);
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(WeatherReadingDAO.class);
        var root = query.from(WeatherReadingDAO.class);
        Join<WeatherReadingDAO, WeatherStationUnitDAO> weatherStationJoin = root.join("weatherStationUnit");
        query.where(criteriaBuilder.equal(weatherStationJoin.get("name"), weatherStationName));
        query.orderBy(criteriaBuilder.desc(root.get("created")));
        var typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    public int save(List<WeatherReadingDAO> weatherReadings) {
        var weatherStationUnit = weatherStationUnitService.getByNameWithPassword(weatherReadings.get(0).getWeatherStationName());
        if (weatherStationUnit == null) {
            return 404;
        }
        for (var weatherReading : weatherReadings) {
            if (!weatherReading.getWeatherStationPassword().equals(weatherStationUnit.getPassword())) {
                return 401;
            }
            weatherReading.setWeatherStationUnit(weatherStationUnit);
            if (weatherReading.getCreatedMillis() > 1710624653298L) {
                weatherReading.setCreated(createLocalDateTime(weatherReading.getCreatedMillis()));
            } else {
                weatherReading.setCreated(LocalDateTime.now());
            }
            weatherReadingRepository.save(weatherReading);
        }
        return 201;
    }

    private LocalDateTime createLocalDateTime(long millis) {
        var instant = Instant.ofEpochMilli(millis);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

}

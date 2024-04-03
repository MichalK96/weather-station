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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if (weatherReading.getApiResponseCode() == 0) {
                weatherReading.setApiResponseCode(201);
            }
            if (weatherReading.getCreatedMillis() > 1710624653298L) {
                weatherReading.setCreated(createLocalDateTime(weatherReading.getCreatedMillis()));
            } else {
                weatherReading.setCreated(LocalDateTime.now());
            }
            weatherReadingRepository.save(weatherReading);
        }
        return 201;
    }

    public Map<Integer, WeatherDataAverages> calculateHourlyAverages(String weatherStationName) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createTupleQuery();
        var root = query.from(WeatherReadingDAO.class);

        // Tworzenie wyrażenia dla filtra na ostatnie 24 godziny
        var twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        var predicate = criteriaBuilder.greaterThan(root.get("created"), twentyFourHoursAgo);

        // Definiowanie funkcji agregujących dla temperatury, wilgotności, ciśnienia i nasłonecznienia
        var hour = criteriaBuilder.function("hour", Integer.class, root.get("created"));
        var avgTemperature = criteriaBuilder.avg(root.get("temperature"));
        var avgHumidity = criteriaBuilder.avg(root.get("humidity"));
        var avgPressure = criteriaBuilder.avg(root.get("pressure"));
        var avgLightIntensity = criteriaBuilder.avg(root.get("lightIntensity"));
        var avgUvLevel = criteriaBuilder.avg(root.get("uvLevel"));
        var avgPrecipitation = criteriaBuilder.avg(root.get("precipitation"));
        var avgWindSpeed = criteriaBuilder.avg(root.get("precipitation"));

        // Wybieranie kolumn do zapytania
        query.multiselect(hour, avgTemperature, avgHumidity, avgPressure, avgLightIntensity);
        query.where(predicate);
        query.groupBy(hour);

        // Wykonanie zapytania
        var resultList = entityManager.createQuery(query).getResultList();

        // Tworzenie mapy wyników (godzina -> średnie wartości)
        Map<Integer, WeatherDataAverages> hourlyAveragesMap = new HashMap<>();
        for (var tuple : resultList) {
            var hourOfDay = tuple.get(hour);
            var averageTemperature = tuple.get(avgTemperature);
            var averageHumidity = tuple.get(avgHumidity);
            var averagePressure = tuple.get(avgPressure);
            var averageLightIntensity = tuple.get(avgLightIntensity);
            var averageUvLevel = tuple.get(avgUvLevel);
            var averagePrecipitation = tuple.get(avgPrecipitation);
            var averageWindSpeed = tuple.get(avgWindSpeed);

            var averages = new WeatherDataAverages(averageTemperature, averageHumidity, averagePressure,
                                            averageLightIntensity, averageUvLevel, averagePrecipitation, averageWindSpeed);
            hourlyAveragesMap.put(hourOfDay, averages);
        }

        return hourlyAveragesMap;
    }
    private LocalDateTime createLocalDateTime(long millis) {
        var instant = Instant.ofEpochMilli(millis);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

}

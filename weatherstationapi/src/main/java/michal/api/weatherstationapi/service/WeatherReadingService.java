package michal.api.weatherstationapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import michal.api.weatherstationapi.dao.WeatherReadingDAO;
import michal.api.weatherstationapi.dao.WeatherStationUnitDAO;
import michal.api.weatherstationapi.repository.WeatherReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;

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

    public List<WeatherDataAverages> calculateHourlyAverages(String weatherStationName, int hours) {
        if (!WeatherStationUnitService.validateName(weatherStationName) ||
                weatherStationUnitService.getByNameWithoutPassword(weatherStationName) == null) {
            return List.of();
        }
        var sqlQuery = String.format("""
                    SELECT
                        DATE(weather_reading.created) AS date,
                        EXTRACT(HOUR FROM weather_reading.created) AS hour,
                        AVG(temperature) AS avg_temperature,
                        AVG(humidity) AS avg_humidity,
                        AVG(pressure) AS avg_pressure,
                        AVG(light_intensity) AS avg_light_intensity,
                        AVG(uv_level) AS avg_uv_level,
                        AVG(precipitation) AS avg_precipitation,
                        AVG(wind_speed) AS avg_wind_speed
                    FROM
                        weather_reading
                    JOIN
                        weather_station_unit ON weather_reading.weather_station_unit_id=weather_station_unit.id
                    WHERE
                        weather_station_unit.name = '%s' AND
                        weather_reading.created > NOW() - INTERVAL '%s HOUR'
                    GROUP BY
                        date, hour
                    ORDER BY
                        date DESC, hour DESC
                """, weatherStationName, hours);
        var query = entityManager.createNativeQuery(sqlQuery);
        return mapResult(query.getResultList(), hours);                // TODO refactor
    }

    private List<WeatherDataAverages> mapResult(List<Object[]> result, int hours) {
        var mappedResult = new ArrayList<WeatherDataAverages>();
        if (result.size() > hours) {
            result.remove(result.size() - 1);
        }
        for (Object[] object : result) {
            var weatherDataAverages = new WeatherDataAverages();
            weatherDataAverages.setDate((Date) object[0]);
            weatherDataAverages.setHour((BigDecimal) object[1]);
            weatherDataAverages.setAvgTemperature((Double) object[2]);
            weatherDataAverages.setAvgHumidity((BigDecimal) object[3]);
            weatherDataAverages.setAvgPressure((BigDecimal) object[4]);
            weatherDataAverages.setAvgLightIntensity((BigDecimal) object[5]);
            weatherDataAverages.setAvgUvLevel((BigDecimal) object[6]);
            weatherDataAverages.setAvgPrecipitation((BigDecimal) object[7]);
            weatherDataAverages.setAvgWindSpeed((BigDecimal) object[8]);

            mappedResult.add(weatherDataAverages);
        }
        return mappedResult;
    }
    private LocalDateTime createLocalDateTime(long millis) {
        var instant = Instant.ofEpochMilli(millis);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

}

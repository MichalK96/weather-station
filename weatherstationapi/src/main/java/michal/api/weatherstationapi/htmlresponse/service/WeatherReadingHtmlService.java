package michal.api.weatherstationapi.htmlresponse.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import michal.api.weatherstationapi.dao.WeatherReadingDAO;
import michal.api.weatherstationapi.dao.WeatherStationUnitDAO;
import michal.api.weatherstationapi.repository.WeatherReadingRepository;
import michal.api.weatherstationapi.service.WeatherStationUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherReadingHtmlService {

    private final WeatherReadingRepository weatherReadingRepository;
    private final WeatherStationUnitService weatherStationUnitService;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public WeatherReadingHtmlService(WeatherReadingRepository weatherReadingRepository, WeatherStationUnitService weatherStationUnitService) {
        this.weatherReadingRepository = weatherReadingRepository;
        this.weatherStationUnitService = weatherStationUnitService;
    }

    public String getLastReadingByWeatherStationName(String weatherStationName) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(WeatherReadingDAO.class);
        var root = query.from(WeatherReadingDAO.class);
        Join<WeatherReadingDAO, WeatherStationUnitDAO> weatherStationJoin = root.join("weatherStationUnit");
        query.where(criteriaBuilder.equal(weatherStationJoin.get("name"), weatherStationName));

        query.orderBy(criteriaBuilder.desc(root.get("created")));
        var typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        try {
            var result = typedQuery.getSingleResult();
            return generateLastReading(result, weatherStationName);
        } catch (Exception e) {
            if (e.getMessage().contains("No result")) {
                return "Nie zneleziono odczytów";
            }
            return e.getMessage();
        }
    }

    public String listByWeatherStationName(String weatherStationName) {
        var weatherStationUnit = weatherStationUnitService.getByNameWithoutPassword(weatherStationName);
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(WeatherReadingDAO.class);
        var root = query.from(WeatherReadingDAO.class);
        Join<WeatherReadingDAO, WeatherStationUnitDAO> weatherStationJoin = root.join("weatherStationUnit");
        query.where(criteriaBuilder.equal(weatherStationJoin.get("name"), weatherStationName));
        query.orderBy(criteriaBuilder.desc(root.get("created")));
        var typedQuery = entityManager.createQuery(query);
        var result = typedQuery.getResultList();
        return generateListReadings(result, weatherStationName);
    }

    private String generateListReadings(List<WeatherReadingDAO> weatherReading, String name) {
        var body = "<body>" +
                        "<br><h1>" +
                            "Historia odczytów: " + name +
                        "</h1><br>" +
                        "<table style=\"width:100%\">" +
                            "<tr>" +
                                "<th>Odczyt</th>" +
                                "<th>Temp (°C)</th>" +
                                "<th>Wilgotność (%)</th>" +
                                "<th>Barometr (hPa)</th>" +
                                "<th>Światło (lux)</th>" +
                                "<th>Kod http</th>" +
                            "</tr>" +
                                generateTableBody(weatherReading) +
                        "</table>" +
                    "</body>";

        return generateHtml("Podsumowanie " + name, body);
    }

    private String generateTableBody(List<WeatherReadingDAO> weatherReading) {
        var tableBody = new StringBuilder();
        for (var reading : weatherReading) {
            tableBody.append(String.format("""
                                <tr>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                </tr>
                                """,
                    getDate(reading.getCreated()),
                    reading.getTemperature(),
                    reading.getHumidity(),
                    reading.getPressure() / 100,
                    reading.getLightIntensity(),
                    reading.getApiResponseCode()));
        }
        return tableBody.toString();
    }

    private String generateLastReading(WeatherReadingDAO weatherReading, String name) {
        var body = String.format("""
                <body>
                    <h1>Aktualna temperatura: %s</h1><br>
                    <p>Odczyt: %s</p><br>
                    <p>Temperatura: %s °C</p>
                    <p>Wilgotność: %s %s</p>
                    <p>Barometr: %s hPa</p>
                    <p>Ilość światła: %s lux</p>
                </body>
                """,
                name,
                getDate(weatherReading.getCreated()),
                weatherReading.getTemperature(),
                weatherReading.getHumidity(), "%",
                weatherReading.getPressure(),
                weatherReading.getLightIntensity());
        return generateHtml(name, body);
    }

    private String generateHtml(String name, String body) {
        return String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                        <style>
                            table, th, td {
                                font-size: 20px;
                                border: 2px solid black;
                                border-collapse: collapse;
                                padding: 8px;
                            }
                            p {
                                font-size: 20px;
                               }
                        </style>
                </head>
                %s
                </html>
                                
                """, name, body);
    }

    private String getDate(LocalDateTime created) {
        var date = new StringBuilder();
        date.append(created.getDayOfMonth()).append(".");
        date.append(created.getMonthValue()).append(".");
        date.append(created.getYear());
        date.append(" ");
        date.append(created.getHour()).append(":");
        date.append(created.getMinute());
        return date.toString();
    }

}

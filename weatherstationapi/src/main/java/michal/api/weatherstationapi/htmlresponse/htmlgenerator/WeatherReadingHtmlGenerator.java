package michal.api.weatherstationapi.htmlresponse.htmlgenerator;

import michal.api.weatherstationapi.dao.WeatherReadingDAO;
import michal.api.weatherstationapi.service.WeatherReadingService;
import michal.api.weatherstationapi.service.WeatherStationUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static michal.api.weatherstationapi.htmlresponse.htmlgenerator.HtmlUtil.generateHtml;

@Service
public class WeatherReadingHtmlGenerator {

    private final WeatherReadingService weatherReadingService;
    private final WeatherStationUnitService weatherStationUnitService;

    private final String listWeatherReadingUrl = "http://" + HomePage.host + ":8080/api/html/weather-reading/list/";
    private final int listReadingsLimit = 5000;

    @Autowired
    public WeatherReadingHtmlGenerator(WeatherReadingService weatherReadingService, WeatherStationUnitService weatherStationUnitService) {
        this.weatherReadingService = weatherReadingService;
        this.weatherStationUnitService = weatherStationUnitService;
    }

    public String getLastReadingByWeatherStationName(String weatherStationName) {
        try {
            var result = weatherReadingService.getLastReadingByWeatherStationName(weatherStationName);
            return generateLastReading(result, weatherStationName);
        } catch (Exception e) {
            if (e.getMessage().contains("No result")) {
                return "Nie zneleziono odczytów";
            }
            return e.getMessage();
        }
    }

    public String listByWeatherStationName(String weatherStationName) {
        var result = weatherReadingService.listByWeatherStationName(weatherStationName);
        return generateListReadings(result, weatherStationName);
    }


    private String generateListReadings(List<WeatherReadingDAO> weatherReading, String name) {
        var body = "<body>" +
                        "<br><h1>" +
                            "Historia odczytów: " + name +
                        "</h1><br>" +
                        "<table style=\"width:100%\">" +
                            "<tr>" +
                                "<th></th>" +
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
        var count = 1;
        for (var reading : weatherReading) {
            if (count >= listReadingsLimit) {
                break;
            }
            tableBody.append(String.format("""
                                <tr>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                </tr>
                                """,
                    count,
                    getDate(reading.getCreated()),
                    reading.getTemperature(),
                    reading.getHumidity(),
                    reading.getPressure() / 100,
                    reading.getLightIntensity(),
                    reading.getApiResponseCode()));
            count++;
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
                    <br>
                    <a href=%s%s>%s</a><br>
                </body>
                """,
                name,
                getDate(weatherReading.getCreated()),
                weatherReading.getTemperature(),
                weatherReading.getHumidity(),
                "%",
                weatherReading.getPressure(),
                weatherReading.getLightIntensity(),
                listWeatherReadingUrl,
                name,
                "Historia odczytów");
        return generateHtml(name, body);
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

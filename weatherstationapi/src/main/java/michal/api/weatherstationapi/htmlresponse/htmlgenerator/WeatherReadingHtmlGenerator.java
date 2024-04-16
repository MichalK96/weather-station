package michal.api.weatherstationapi.htmlresponse.htmlgenerator;

import michal.api.weatherstationapi.dao.WeatherReadingDAO;
import michal.api.weatherstationapi.service.WeatherDataAverages;
import michal.api.weatherstationapi.service.WeatherReadingService;
import michal.api.weatherstationapi.service.WeatherStationUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static michal.api.weatherstationapi.htmlresponse.htmlgenerator.HtmlUtil.generateHtml;

@Service
public class WeatherReadingHtmlGenerator {

    private final WeatherReadingService weatherReadingService;
    private final WeatherStationUnitService weatherStationUnitService;

    private final String listWeatherReadingUrl = "http://" + HomePage.host + ":8080/api/html/weather-reading/list/";
    private final String hoursSummary = "http://" + HomePage.host + ":8080/api/html/weather-reading/hours-summary/";
    private final int listReadingsLimit = 5000;
    private final String[] months = {"styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec", "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"};

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

    public String listHoursSummaryByWeatherStationName(String weatherStationName, int hours) {
        var weatherDataAverages = weatherReadingService.calculateHourlyAverages(weatherStationName, hours);
        if (weatherDataAverages.isEmpty()) {
            return "Nie znaleziono stacji o nazwie " + weatherStationName;
        }
        return generateHoursSummary(weatherDataAverages, weatherStationName, hours);
    }

    private String generateHoursSummary(List<WeatherDataAverages> weatherData, String name, int hours) {
        var body = "<body>" +
                        "<br><h1>" +
                            "Podsumowanie godzinowe (wyniki uśrednione z " + hours + " ostatnich godzin) dla stacji : " + name +
                        "</h1><br>" +
                        "<table style=\"width:100%\">" +
                            "<tr>" +
                                "<th></th>" +
                                "<th>Odczyt</th>" +
                                "<th>Temp (°C)</th>" +
                                "<th>Wilgotność (%)</th>" +
                                "<th>Barometr (hPa)</th>" +
                                "<th>Światło (lux)</th>" +
                            "</tr>" +
                            generateTableBodyHoursSummary(weatherData) +
                        "</table>" +
                    "</body>";

        return generateHtml("Podsumowanie godzinowe" + name, body);
    }

    private String generateTableBodyHoursSummary(List<WeatherDataAverages> weatherReading) {
        var tableBody = new StringBuilder();
        var count = 1;
        for (var reading : weatherReading) {
            if (count >= listReadingsLimit) {
                break;
            }
            tableBody.append(String.format("""
                                <tr%s>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                </tr>
                                """,
                    count % 2 == 0 ?  " style=\"background-color: #e6e6e6;\"" : "",
                    count,
                    reading.getDate().getDay() + " " + parseMonth(reading.getDate().getMonth()) + ", godz. " + reading.getHour(),
                    BigDecimal.valueOf(reading.getAvgTemperature()).setScale(1, RoundingMode.HALF_UP),
                    reading.getAvgHumidity().setScale(0, RoundingMode.HALF_UP),
                    reading.getAvgPressure().setScale(0, RoundingMode.HALF_UP),
                    reading.getAvgLightIntensity().setScale(0, RoundingMode.HALF_UP)));
            count++;
        }
        return tableBody.toString();
    }

    private String parseMonth(int month) {
        return month >= 0 && month <= 12 ? months[month - 1] : "----";
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
                                generateTableBodyListReadings(weatherReading) +
                        "</table>" +
                    "</body>";

        return generateHtml("Podsumowanie " + name, body);
    }

    private String generateTableBodyListReadings(List<WeatherReadingDAO> weatherReading) {
        var tableBody = new StringBuilder();
        var count = 1;
        for (var reading : weatherReading) {
            if (count >= listReadingsLimit) {
                break;
            }
            tableBody.append(String.format("""
                                <tr%s>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                    <th>%s</th>
                                </tr>
                                """,
                    count % 2 == 0 ?  " style=\"background-color: #e6e6e6;\"" : "",
                    count,
                    getDate(reading.getCreated()),
                    reading.getTemperature(),
                    reading.getHumidity(),
                    reading.getPressure_hPa(),
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
                    <a href=%s%s/24 class="button">Podsumowanie z ostatnich 24 godzin</a><br><br>
                    <a href=%s%s class="button">Pełna historia odczytów</a>
                </body>
                """,
                name,
                getDate(weatherReading.getCreated()),
                weatherReading.getTemperature(),
                weatherReading.getHumidity(),
                "%",
                weatherReading.getPressure_hPa(),
                weatherReading.getLightIntensity(),
                hoursSummary,
                name,
                listWeatherReadingUrl,
                name);
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

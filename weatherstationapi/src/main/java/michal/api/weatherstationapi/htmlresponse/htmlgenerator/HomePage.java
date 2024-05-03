package michal.api.weatherstationapi.htmlresponse.htmlgenerator;

import michal.api.weatherstationapi.htmlresponse.HtmlUtil;
import michal.api.weatherstationapi.service.WeatherReadingService;
import michal.api.weatherstationapi.service.WeatherStationUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static michal.api.weatherstationapi.htmlresponse.HtmlUtil.generateHtml;

@Service
public class HomePage {

    private final WeatherStationUnitService weatherStationUnitService;
    private final WeatherReadingService weatherReadingService;

    static final String host = "localhost";
    private final String weatherReadingUrl = "http://" + host + ":8080/api/html/weather-reading/";
    private final String weatherStationNameToDisplayReadingOnHomePage = "name";

    @Autowired
    public HomePage(WeatherStationUnitService weatherStationUnitService, WeatherReadingService weatherReadingService) {
        this.weatherStationUnitService = weatherStationUnitService;
        this.weatherReadingService = weatherReadingService;
    }

    public String prepareHomePage() {

        return generateHtml("Stacja pogodowa", generateBody());
    }

    private String generateBody() {
        var body = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Podział na dwie kolumny</title>" +
                "    <style>" +
                "        /* Styl dla wiersza */" +
                "        .row {" +
                "            display: flex; /* Użyj flexbox */" +
                "        }" +
                "        /* Styl dla kolumn */" +
                "        .column {" +
                "            flex: 1; /* Rozciągnij kolumny, aby zajmowały dostępną przestrzeń */" +
                "            padding: 10px; /* Wewnętrzne marginesy dla tekstu */" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                    "<h1>" +
                        "Stacja pogodowa" +
                    "</h1>" +
                "<div class=\"row\">" +
                "    <div class=\"column\">" +
                "        <h2>Dostępne punkty pomiaru</h2>" +
                "        <p>" + listWeatherStations() + "</p>" +
                "    </div>" +
                "    <div class=\"column\">" +
                "        <h2>Pogoda w Golcowej</h2>" +
                "        <p>" + generateSimplifiedWeatherReading() + "</p>" +
                "    </div>" +
                "</div>" +
                "</body>" +
                "</html>";
        return body;
    }

    private String generateSimplifiedWeatherReading() {
        var weatherReading = weatherReadingService.getLastReadingByWeatherStationName(weatherStationNameToDisplayReadingOnHomePage);
        if (!isReadingCurrent(weatherReading.getCreated())) {
            return "Aktualny odczyt nie jest dostępny";
        }
        var body = String.format("""
                <p>Temperatura: %s °C</p>
                <p>Wilgotność: %s %s</p>
                <p>Barometr: %s hPa</p>
                <p>Ilość światła: %s lux</p>
                """,
                HtmlUtil.roundToNearestHalf(weatherReading.getTemperature()),
                weatherReading.getHumidity(),
                "%",
                weatherReading.getPressure_hPa(),
                weatherReading.getLightIntensity());
        return generateHtml(weatherStationNameToDisplayReadingOnHomePage, body);
    }

    private boolean isReadingCurrent(LocalDateTime creationDate) {
        var minutesDifference = ChronoUnit.MINUTES.between(creationDate, LocalDateTime.now());
        return minutesDifference < 45;
    }

    private String listWeatherStations() {
        var weatherStationsHtml = new StringBuilder();
        var stations = weatherStationUnitService.list();
        for (var station : stations) {
            weatherStationsHtml.append(String.format("<a href=%s%s>%s</a><br>", weatherReadingUrl, station.getName(), station.getName()));
        }
        return weatherStationsHtml.toString();
    }

}

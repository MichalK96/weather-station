package michal.api.weatherstationapi.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Repository
@NoArgsConstructor
@Table(name = "weather_reading")
public class WeatherReadingDAO {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    private LocalDateTime dateTime;
    private int temperature;
    private int humidity;
    private int pressure;
    private int sunlight;
    private int uvLevel;
    private int precipitation;
    private int windSpeed;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "weather_station_unit_id")
    private WeatherStationUnitDAO weatherStationUnit;

}

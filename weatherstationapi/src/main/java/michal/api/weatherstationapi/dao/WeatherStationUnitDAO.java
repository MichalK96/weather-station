package michal.api.weatherstationapi.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Entity
@Data
@Repository
@NoArgsConstructor
@Table(name = "weather_station_unit")
public class WeatherStationUnitDAO {

    public WeatherStationUnitDAO(Long id, String name, LocalDateTime created, int refreshTimeSec, double tempOffset,
                                 int humidityOffset, int pressureOffset) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.refreshTimeSec = refreshTimeSec;
        this.tempOffset = tempOffset;
        this.humidityOffset = humidityOffset;
        this.pressureOffset = pressureOffset;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String password;
    private LocalDateTime created;
    private int refreshTimeSec;
    private double tempOffset;
    private int humidityOffset;
    private int pressureOffset;
}

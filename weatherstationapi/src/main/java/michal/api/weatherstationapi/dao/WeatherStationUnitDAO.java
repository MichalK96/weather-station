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

    public WeatherStationUnitDAO(Long id, String name, LocalDateTime created, int refreshTimeSec) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.refreshTimeSec = refreshTimeSec;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String password;
    private LocalDateTime created;
    @Column(columnDefinition = "INT DEFAULT 300")
    private int refreshTimeSec;
}

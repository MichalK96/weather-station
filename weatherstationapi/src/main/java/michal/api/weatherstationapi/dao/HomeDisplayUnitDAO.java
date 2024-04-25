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
@Table(name = "home_display_unit")
public class HomeDisplayUnitDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private LocalDateTime created;
    private int refreshTimeSec;
    private int brightness;
    private String password;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "weather_station_unit_id", referencedColumnName = "id")
    private WeatherStationUnitDAO weatherStationUnit;

}

package michal.api.weatherstationapi.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Set;

@Entity
@Data
@Repository
@NoArgsConstructor
@Table(name = "weather_station_unit")
public class WeatherStationUnitDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String password;
    @JsonManagedReference
    @OneToMany(mappedBy = "weatherStationUnit")
    private Set<WeatherReadingDAO> weatherReadings;
    @JsonManagedReference
    @OneToMany(mappedBy = "weatherStationUnit", cascade = CascadeType.ALL)
    private Set<WeatherStationStatusDTO> weatherStationStatus;
}

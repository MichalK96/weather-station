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
@Table(name = "weather_station_status")
public class WeatherStationStatusDTO {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    private LocalDateTime dateTime;
    private int internalHumidity;
    private int internalTemperature;
    private int accumulatorVoltage;
    private int solarPanelVoltage;
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "weather_station_unit_id")
    private WeatherStationUnitDAO weatherStationUnit;


}

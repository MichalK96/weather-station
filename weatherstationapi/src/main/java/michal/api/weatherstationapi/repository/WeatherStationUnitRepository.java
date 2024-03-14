package michal.api.weatherstationapi.repository;

import michal.api.weatherstationapi.dao.WeatherStationUnitDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherStationUnitRepository extends JpaRepository<WeatherStationUnitDAO, Long> {

    WeatherStationUnitDAO findByName(String name);

}

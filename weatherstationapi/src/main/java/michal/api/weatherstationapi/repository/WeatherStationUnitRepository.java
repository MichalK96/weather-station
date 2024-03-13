package michal.api.weatherstationapi.repository;

import michal.api.weatherstationapi.dao.WeatherStationUnitDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherStationUnitRepository extends JpaRepository<WeatherStationUnitDAO, Long> {
}

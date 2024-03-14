package michal.api.weatherstationapi.repository;

import michal.api.weatherstationapi.dao.WeatherReadingDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WeatherReadingRepository extends JpaRepository<WeatherReadingDAO, UUID> {

    List<WeatherReadingDAO> findAllDistinctByWeatherStationName(String name);

}

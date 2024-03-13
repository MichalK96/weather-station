package michal.api.weatherstationapi.repository;

import michal.api.weatherstationapi.dao.HomeDisplayUnitDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeDisplayUnitRepository extends JpaRepository<HomeDisplayUnitDAO, Long> {
}

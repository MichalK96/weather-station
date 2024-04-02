package michal.api.weatherstationapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import michal.api.weatherstationapi.dao.WeatherStationUnitDAO;
import michal.api.weatherstationapi.exception.ApiErrorException;
import michal.api.weatherstationapi.repository.WeatherStationUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherStationUnitService {

    @PersistenceContext
    private EntityManager entityManager;
    private final WeatherStationUnitRepository weatherStationUnitRepository;

    @Autowired
    public WeatherStationUnitService(WeatherStationUnitRepository weatherStationUnitRepository) {
        this.weatherStationUnitRepository = weatherStationUnitRepository;
    }

    public List<WeatherStationUnitDAO> list() {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(WeatherStationUnitDAO.class);
        var root = query.from(WeatherStationUnitDAO.class);
        query.select(criteriaBuilder.construct(WeatherStationUnitDAO.class,
                root.get("id"),
                root.get("name"),
                root.get("created"),
                root.get("refreshTimeSec"),
                root.get("tempOffset"),
                root.get("humidityOffset"),
                root.get("pressureOffset")));
        return entityManager.createQuery(query).getResultList();
    }

    public WeatherStationUnitDAO save(WeatherStationUnitDAO weatherStationUnit) {
        if (!validateName(weatherStationUnit.getName())) {
            throw new ApiErrorException("Station name contains illegal characters or long is not between 2-25 characters");
        }
        weatherStationUnit.setCreated(LocalDateTime.now());
        if (weatherStationUnit.getRefreshTimeSec() == 0) {
            weatherStationUnit.setRefreshTimeSec(300);
        }
        return weatherStationUnitRepository.save(weatherStationUnit);
    }

    public WeatherStationUnitDAO getByNameWithPassword(String name) {
        return weatherStationUnitRepository.findByName(name);
    }

    public WeatherStationUnitDAO getByNameWithoutPassword(String name) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(WeatherStationUnitDAO.class);
        var root = query.from(WeatherStationUnitDAO.class);
        query.where(criteriaBuilder.equal(root.get("name"), name));
        query.select(criteriaBuilder.construct(WeatherStationUnitDAO.class,
                root.get("id"),
                root.get("name"),
                root.get("created"),
                root.get("refreshTimeSec"),
                root.get("tempOffset"),
                root.get("humidityOffset"),
                root.get("pressureOffset")));
        return entityManager.createQuery(query).getSingleResult();
    }

    private boolean validateName(String name) {
        String regex = "^[a-zA-Z0-9-_\\/! ]{2,25}$";
        return name.matches(regex);
    }

}

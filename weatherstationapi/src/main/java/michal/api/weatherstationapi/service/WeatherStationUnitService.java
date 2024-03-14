package michal.api.weatherstationapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import michal.api.weatherstationapi.dao.WeatherStationUnitDAO;
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
                root.get("created")));
        return entityManager.createQuery(query).getResultList();
    }

    public WeatherStationUnitDAO save(WeatherStationUnitDAO weatherStationUnit) {
        weatherStationUnit.setCreated(LocalDateTime.now());
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
                root.get("created")));
        return entityManager.createQuery(query).getSingleResult();
    }

}

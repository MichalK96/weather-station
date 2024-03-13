package michal.api.weatherstationapi.service;

import michal.api.weatherstationapi.dao.HomeDisplayUnitDAO;
import michal.api.weatherstationapi.repository.HomeDisplayUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeDisplayUnitService {

    private final HomeDisplayUnitRepository homeDisplayUnitRepository;

    @Autowired
    public HomeDisplayUnitService(HomeDisplayUnitRepository homeDisplayUnitRepository) {
        this.homeDisplayUnitRepository = homeDisplayUnitRepository;
    }

    public List<HomeDisplayUnitDAO> list() {
        return homeDisplayUnitRepository.findAll();
    }

}

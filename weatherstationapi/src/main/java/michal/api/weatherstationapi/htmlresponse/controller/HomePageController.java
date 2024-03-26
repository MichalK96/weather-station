package michal.api.weatherstationapi.htmlresponse.controller;

import michal.api.weatherstationapi.htmlresponse.htmlgenerator.HomePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HomePageController {

    private final HomePage homePage;

    @Autowired
    public HomePageController(HomePage homePage) {
        this.homePage = homePage;
    }

    @GetMapping
    public String homePage() {
        return homePage.prepareHomePage();
    }

}

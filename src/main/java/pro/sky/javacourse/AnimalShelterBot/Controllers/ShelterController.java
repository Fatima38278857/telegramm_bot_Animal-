package pro.sky.javacourse.AnimalShelterBot.Controllers;

import org.springframework.web.bind.annotation.RestController;
import pro.sky.javacourse.AnimalShelterBot.intermediary.QueryDefinitionClass;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;

@RestController("shelter")
public class ShelterController {
    private ShelterService shelterService;
    private QueryDefinitionClass queryDefinitionClass;

    public ShelterController(ShelterService shelterService, QueryDefinitionClass queryDefinitionClass) {
        this.shelterService = shelterService;
        this.queryDefinitionClass = queryDefinitionClass;
    }

    public String giveInfoAboutShelter(String name) {
        return queryDefinitionClass.getInfoAboutShelter(name);
    }
}

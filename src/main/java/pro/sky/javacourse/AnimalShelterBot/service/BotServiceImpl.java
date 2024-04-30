package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.stereotype.Service;

@Service
public class BotServiceImpl implements BotService{
    public Boolean isVolunteer(Long chatId){
        return chatId == 1722853186L; // Здесь будет метод по проверке chatId по базе данных волонтеров.
    };
}

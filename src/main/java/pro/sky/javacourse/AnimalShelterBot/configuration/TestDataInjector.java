package pro.sky.javacourse.AnimalShelterBot.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import pro.sky.javacourse.AnimalShelterBot.model.*;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerServiceImpl;
import pro.sky.javacourse.AnimalShelterBot.service.PetService;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterServiceImpl;
import pro.sky.javacourse.AnimalShelterBot.service.VolunteerServiceImpl;

@Component
public class TestDataInjector {
    private final ShelterServiceImpl shelterService;
    private final VolunteerServiceImpl volunteerService;
    private final PetService petService;
    private final CaretakerServiceImpl caretakerService;

    public TestDataInjector(ShelterServiceImpl shelterService, VolunteerServiceImpl volunteerService, PetService petService, CaretakerServiceImpl caretakerService) {
        this.shelterService = shelterService;
        this.volunteerService = volunteerService;
        this.petService = petService;
        this.caretakerService = caretakerService;
    }

    // UNCOMMENT METHODS TO FILL DATABASE WITH TEST DATA
    @PostConstruct
    private void injectTestData() {
//        initShelters();
//        initVolunteers();
//        setMainVolunteer();
//        setOtherVolunteers();
//        initPets();
//        initCaretakers();
    }

    private void initShelters() {
        Shelter shelter = new Shelter();
        shelter.setName("Солнышко");
        shelter.setAddress("Москва, ул. Академика Королева, 13.");
        shelter.setRegime("Понедельник - пятница: с 7-00 до 20-00 без обеда и выходных.");
        String howTo = "Чтобы взять животное из приюта необходимо будет посетить приют и выбрать питомца. Адрес и режим работы приюта можно узнать, просмотрев информацию из предыдущих меню.\n" +
                "Краткую информацию о содержащихся в приюте питомцах можно узнать, проследовав в предыдущем меню по кнопке «Выбрать питомца».\n" +
                "Также в нашем боте можно получить рекомендации специалистов по общим вопросам содержания отдельных видов и пород животных. \n" +
                "Для оформления документов от нам потребуется Ваш паспорт гражданина РФ.\n" +
                "В приюте Вы познакомитесь с питомцем и сможете вместе провести время, например, сходить на прогулку.\n" +
                "В отдельных случаях, от Вас потребуется подтвердить наличие условий содержания животного, либо наличие специальных предметов для транспортировки животного. С данными условиями Вы можете также кратко ознакомиться, перейдя в предыдущем меню по кнопке «Выбрать питомца».\n" +
                "После заключения договора Вы сможете забрать питомца в его новый дом. Вам будет назначен испытательный срок, в течение которого Вы обязаны ежедневно присылать отчет о состоянии питомца с фотографиями. Отчет также можно передать через данного бота в Телеграмм. Замечания или пожелания от волонтеров, просматривающих отчеты, также поступят в Ваш чат с ботом.\n" +
                "И главное, Вам могут отказать в передаче питомца или обязать вернуть питомца в приют без объяснения причин. Таково наше условие, с которым необходимо согласиться.\n" +
                "После прохождения испытательного срока от Вас потребуется посетить приют вместе с питомцем для завершения оформления документов.\n" +
                "По всем дополнительным вопросам Вы также можете связаться с нашими волонтерами используя чат бота либо можете оставить свои контактные данные, и мы Вам перезвоним.\n";
        shelter.setHowTo(howTo);
        shelterService.add(shelter);
        shelter = new Shelter();
        shelter.setName("Дружок");
        shelter.setAddress("Ижевск, ул. Боевой славы, 5.");
        shelter.setRegime("Понедельник - пятница: с 8-00 до 21-00 без обеда и выходных.");
        shelter.setHowTo(howTo);
        shelterService.add(shelter);
        shelter = new Shelter();
        shelter.setName("На Невском");
        shelter.setAddress("Санкт-Петербург, Невский проспект, 18.");
        shelter.setRegime("Понедельник - пятница: с 6-00 до 20-00 без обеда и выходных.");
        shelter.setHowTo(howTo);
        shelterService.add(shelter);
    }

    private void initVolunteers() {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Павел Фомченков");
        volunteer.setAddress("г. Великий Новгород");
        volunteer.setPassport("0123 123456");
        volunteer.setPhoneNumber("+77776665555");
        volunteer.setChatId(1722853186L);
        volunteerService.add(volunteer);
    }

    private void setMainVolunteer() {
        Volunteer volunteer = volunteerService.findByChatId(1722853186L);

        Shelter shelter = shelterService.findShelterByName("Солнышко");
        shelter.setMainVolunteer(volunteer);
        shelterService.edit(shelter);

        shelter = shelterService.findShelterByName("Дружок");
        shelter.setMainVolunteer(volunteer);
        shelterService.edit(shelter);

        shelter = shelterService.findShelterByName("На Невском");
        shelter.setMainVolunteer(volunteer);
        shelterService.edit(shelter);
    }

    private void setOtherVolunteers() {
        Long volunteerId = volunteerService.findByChatId(1722853186L).getId();

        Shelter shelter = shelterService.findShelterByName("Солнышко");
        volunteerService.addToShelter(volunteerId, shelter.getId());

        shelter = shelterService.findShelterByName("Дружок");
        volunteerService.addToShelter(volunteerId, shelter.getId());

        shelter = shelterService.findShelterByName("На Невском");
        volunteerService.addToShelter(volunteerId, shelter.getId());
    }

    private void initPets() {
        Shelter shelter = shelterService.findShelterByName("Солнышко");
        petService.add(new Pet("Шарик", 10, PetType.СОБАКА, "Добрый, отзывчивый пес.", "Почтенный возраст.", "Может жить в будке.", shelter));
        petService.add(new Pet("Барбос", 3, PetType.СОБАКА, "Надежный охранник", "Может представлять опасность для посторонних.", "Не для содержания в квартире.", shelter));
        petService.add(new Pet("Василий", 4, PetType.КОТ, "Ответственный и важный кот.", "Отсутствуют.", "Обычные.", shelter));
        petService.add(new Pet("Машка", 3, PetType.КОТ, "Общительная кошка.", "Отсутствуют.", "Обычные.", shelter));
        petService.add(new Pet("Кеша", 3, PetType.ОСТАЛЬНЫЕ, "Волнистый попугайчик.", "Отсутствуют.", "Нужна клетка.", shelter));
        shelter = shelterService.findShelterByName("Дружок");
        petService.add(new Pet("Тортилла", 20, PetType.ОСТАЛЬНЫЕ, "Мудрая черепаха.", "Отсутствуют.", "Основной рацион это овощи и фрукты. Нужна клетка.", shelter));
        petService.add(new Pet("Пеппа", 1, PetType.ОСТАЛЬНЫЕ, "Чистоплотная морская свинка.", "Отсутствуют.", "Нужна клетка.", shelter));
        petService.add(new Pet("Матроскин", 3, PetType.КОТ, "Кот. Очень деловой.", "Отсутствуют.", "Обычные.", shelter));
        petService.add(new Pet("Мурка", 4, PetType.КОТ, "Замурчательная кошка.", "Отсутствуют.", "Обычные.", shelter));
        petService.add(new Pet("Марсик", 5, PetType.КОТ, "Большой, пушистый кот.", "Отсутствуют.", "Обычные.", shelter));
        shelter = shelterService.findShelterByName("На Невском");
        petService.add(new Pet("Тайга", 6, PetType.СОБАКА, "Тренированная охотничья собака. Девочка.", "Отсутствуют.", "Не может содержаться в квартире.", shelter));
        petService.add(new Pet("Ипполит", 4, PetType.СОБАКА, "Комнатный пес, очень общительный.", "Отсутствуют.", "Обычные.", shelter));
        petService.add(new Pet("Пуля", 5, PetType.СОБАКА, "Смесь пуделя и болонки, очень активная.", "Отсутствуют.", "Обычные.", shelter));
        petService.add(new Pet("Элвис", 1, PetType.ОСТАЛЬНЫЕ, "Обаятельный хомяк", "Отсутствуют.", "Нужна клетка.", shelter));
        petService.getAll().forEach(petService::available);
        petService.ill(petService.find("Тортилла").stream().findAny().orElse(null));
    }

    private void initCaretakers() {
        Caretaker caretaker = new Caretaker("Павел Павел", "Великий Новгород",
                "4321 654321", "+7555444333", 6725110697L);
        caretakerService.add(caretaker);
    }
}


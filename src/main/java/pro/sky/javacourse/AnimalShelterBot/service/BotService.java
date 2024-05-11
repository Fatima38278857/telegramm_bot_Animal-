package pro.sky.javacourse.AnimalShelterBot.service;

import org.telegram.telegrambots.meta.api.objects.Contact;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.PetType;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.TelegramContact;

import java.util.List;

public interface BotService {
    // List<Shelter> getShelters(); // возвращает список или Set приютов
    // Shelter getShelterById(Long shelterId); // возвращает приют по Id
    // List<String> getPetTypes(Long shelterId); // возвращает список видов животных (Собаки, Кошки, Остальные) без повторений (DISTINCT) отсортированный в таком порядке
    // List<Pet> getPetsByShelterIdAndTypeAndStatus(Long shelterId, String petType, String petStatus); // Возвращает животных отдельного типа, например собак, из отдельного приюта, но только тех которые не на испытательном сроке, не забраны опекунами и живые
    // List<Pet> getPetsByPetStatus(String petStatus); // Возвращает список животных по статусу, например выданных на испытательный срок
    // Pet getPetById(Long petId); // возвращает питомца по Id
    // List<Volunteer> getVolunteers(); // возвращает список всех волонтеров
    // Volunteer getVolunteerById(Long VolunteerId); // Возвращает волонтера по Id;
    // List<Caretaker> getCaretakers(); // возвращает список всех опекунов
    // List<Caretaker> getActualCaretakers(); // Возвращает тех опекунов, на которых числятся питомцы у которых дата окончания испытательного срока больше LocalDateTime.now();
    // Caretaker getCaretakerById(Long CaretakerId); // Возвращает опекуна по Id;
    Boolean isVolunteer(Long chatId, Long shelterId);
    List<Shelter> findShelters();
    Shelter findShelter(Long id);
    Shelter findShelterByName(String name);
    boolean caretakerHasPets(Long chatId);
    boolean caretakerHasPets(Long chatId, Long shelterId);
    TelegramContact saveContact(Contact contact, Shelter shelter);
    Pet findPet(Long petId);
    List<PetType> getAvailablePetTypes(Long shelterId);
    List<Pet> findAvailableByShelterId(Long shelterId);
    List<Pet> caretakerPets(Long chatId);
    List<Pet> caretakerPets(Long chatId, Long shelterId);
    Long findChatIdByPetId(Long petId);

    // Нужно в приют добавить поле с дежурным волонтером в класс приют
}

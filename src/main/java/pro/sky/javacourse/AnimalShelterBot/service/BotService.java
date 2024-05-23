package pro.sky.javacourse.AnimalShelterBot.service;

import org.telegram.telegrambots.meta.api.objects.Contact;
import pro.sky.javacourse.AnimalShelterBot.model.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BotService {
    String getReportsDir();
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
    Caretaker findCaretakerByChatId(Long chatId);
    LocalDateTime toLocalDateTime(Integer date);
    String getExtensions(String fileName);
    Report findReport(Long id);
    Report saveReport(Report report);
    Report approveReport(Long reportId, Long volunteerChatId);
    Report declineReport(Long reportId, Long volunteerChatId);
    Collection<Report> findByStatus(ReportStatus status);

    List<ReportMessage> findReportMessages(Long reportId);

    // Нужно в приют добавить поле с дежурным волонтером в класс приют
}

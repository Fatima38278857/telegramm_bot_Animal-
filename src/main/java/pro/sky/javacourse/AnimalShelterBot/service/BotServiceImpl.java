package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;
import pro.sky.javacourse.AnimalShelterBot.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

@Service
public class BotServiceImpl implements BotService {
    private final TelegramContactService telegramContactService;
    private final ShelterService shelterService;
    private final CaretakerService caretakerService;
    private final PetService petService;
    private final ReportMessageService reportMessageService;
    private final ReportService reportService;

    private final Logger logger = LoggerFactory.getLogger(BotServiceImpl.class);
    @Value("${reports.dir.path}")
    private String reportsDir;

    public BotServiceImpl(TelegramContactService telegramContactService, ShelterService shelterService, CaretakerService caretakerService, PetService petService, ReportMessageService reportMessageService, ReportService reportService) {
        this.telegramContactService = telegramContactService;
        this.shelterService = shelterService;
        this.caretakerService = caretakerService;
        this.petService = petService;
        this.reportMessageService = reportMessageService;
        this.reportService = reportService;
    }

    @Override
    public String getReportsDir() {
        return reportsDir;
    }

    @Override
    public Boolean isVolunteer(Long chatId, Long shelterId) {
        return chatId == 1722853186L; // Здесь будет метод по проверке chatId по базе данных волонтеров.
    }

    @Override
    public List<Shelter> findShelters() {
        return shelterService.getAll();
    }

    @Override
    public Shelter findShelter(Long id) {
        return shelterService.find(id);
    }

    @Override
    public Shelter findShelterByName(String name) {
        return shelterService.findShelterByName(name);
    }

    @Override
    public boolean caretakerHasPets(Long chatId) {
        Caretaker caretaker = caretakerService.findByChatId(chatId);
        if (caretaker == null) return false;
        return !petService.findByCaretakerId(caretaker.getId()).isEmpty();
    }

    @Override
    public boolean caretakerHasPets(Long chatId, Long shelterId) {
        Caretaker caretaker = caretakerService.findByChatId(chatId);
        if (caretaker == null) return false;
        return !petService.findByCaretakerIdAndShelterId(caretaker.getId(), shelterId).isEmpty();
    }

    @Override
    public TelegramContact saveContact(Contact contact, Shelter shelter) {
        TelegramContact telegramContact = new TelegramContact(contact.getUserId(), contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber(), shelter);
        logger.info("Was invoked method telegramContactService.add({})", contact);
        return telegramContactService.add(telegramContact);
    }

    @Override
    public Pet findPet(Long petId) {
        return petService.find(petId);
    }

    @Override
    public List<PetType> getAvailablePetTypes(Long shelterId) {
        return petService.getAvailablePetTypes(shelterId);
    }

    @Override
    public List<Pet> findAvailableByShelterId(Long shelterId) {
        return petService.findAvailableByShelterId(shelterId);
    }

    @Override
    public List<Pet> caretakerPets(Long chatId) {
        Caretaker caretaker = caretakerService.findByChatId(chatId);
        return petService.findByCaretakerId(caretaker.getId()).stream().toList();
    }

    @Override
    public List<Pet> caretakerPets(Long chatId, Long shelterId) {
        Caretaker caretaker = caretakerService.findByChatId(chatId);
        return petService.findByCaretakerIdAndShelterId(caretaker.getId(), shelterId).stream().toList();
    }

    @Override
    public Long findChatIdByPetId(Long petId) {
        return petService.findChatIdByPetId(petId);
    }

    @Override
    public Caretaker findCaretakerByChatId(Long chatId) {
        return caretakerService.findByChatId(chatId);
    }

    @Override
    public LocalDateTime toLocalDateTime(Integer date) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(date), ZoneId.systemDefault());
    }

    @Override
    public String getExtensions(String fileName) {
        return reportMessageService.getExtensions(fileName);
    }

    @Override
    public Report saveReport(Report report) {
        return reportService.saveReport(report);
    }

    @Override
    public Report approveReport(Long reportId, Long volunteerChatId) {
        return reportService.approveReport(reportId, volunteerChatId);
    }

    @Override
    public Report declineReport(Long reportId, Long volunteerChatId) {
        return reportService.declineReport(reportId, volunteerChatId);
    }

    @Override
    public Report findReport(Long id) {
        return reportService.findById(id);
    }

    @Override
    public Collection<Report> findByStatus(ReportStatus status) {
        return reportService.findByStatus(status);
    }

    @Override
    public List<ReportMessage> findReportMessages(Long reportId) {
        return reportMessageService.findReportMessages(reportId);
    }

}

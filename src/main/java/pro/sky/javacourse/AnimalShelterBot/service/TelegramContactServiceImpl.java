package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.javacourse.AnimalShelterBot.model.TelegramContact;
import pro.sky.javacourse.AnimalShelterBot.repository.ShelterRepository;
import pro.sky.javacourse.AnimalShelterBot.repository.TelegramContactRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TelegramContactServiceImpl implements TelegramContactService{
    private final TelegramContactRepository telegramContactRepository;
    private final Logger logger = LoggerFactory.getLogger(TelegramContactServiceImpl.class);

    public TelegramContactServiceImpl(TelegramContactRepository telegramContactRepository) {
        this.telegramContactRepository = telegramContactRepository;
    }

    @Override
    public TelegramContact add(TelegramContact telegramContact) {
        logger.info("Was invoked method TelegramContactService.add({})", telegramContact);
        return telegramContactRepository.save(telegramContact);
    }

    @Override
    public List<TelegramContact> getAll() {
        logger.info("Was invoked method TelegramContactService.getAll()");
        return telegramContactRepository.findAll();
    }

    @Override
    public Collection<TelegramContact> getUnsolved() {
        logger.info("Was invoked method TelegramContactService.getUnsolved()");
        return telegramContactRepository.findAll().stream()
                .filter(c -> c.getStatus().equals("unsolved"))
                .toList();
    }

    @Override
    @Transactional
    public TelegramContact findById(String phoneNumber) {
        logger.info("Was invoked method TelegramContactService.findById({})", phoneNumber);
        return telegramContactRepository.findById(phoneNumber).orElse(null);
    }

    @Override
    @Transactional
    public TelegramContact solve(TelegramContact contact) {
        logger.info("Was invoked method TelegramContactService.solve({})", contact);
        TelegramContact contactToSolve = findById(contact.getPhoneNumber());
        contactToSolve.setStatus("solved");
        telegramContactRepository.save(contactToSolve);
        return contactToSolve;
    }
}

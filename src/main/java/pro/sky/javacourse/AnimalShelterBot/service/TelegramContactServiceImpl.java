package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.javacourse.AnimalShelterBot.model.TelegramContact;
import pro.sky.javacourse.AnimalShelterBot.repository.TelegramContactRepository;

import java.util.Collection;
import java.util.List;

@Service
public class TelegramContactServiceImpl implements TelegramContactService{
    private final TelegramContactRepository telegramContactRepository;

    public TelegramContactServiceImpl(TelegramContactRepository telegramContactRepository) {
        this.telegramContactRepository = telegramContactRepository;
    }

    @Override
    public TelegramContact add(TelegramContact telegramContact) {
        return telegramContactRepository.save(telegramContact);
    }

    @Override
    public List<TelegramContact> getAll() {
        return telegramContactRepository.findAll();
    }

    @Override
    public Collection<TelegramContact> getUnsolved() {
        return telegramContactRepository.findAll().stream()
                .filter(c -> c.getStatus().equals("unsolved"))
                .toList();
    }

    @Override
    @Transactional
    public TelegramContact findById(String phoneNumber) {
        return telegramContactRepository.findById(phoneNumber).orElse(null);
    }

    @Override
    @Transactional
    public TelegramContact solve(TelegramContact contact) {
        TelegramContact contactToSolve = findById(contact.getPhoneNumber());
        contactToSolve.setStatus("solved");
        telegramContactRepository.save(contactToSolve);
        return contactToSolve;
    }
}

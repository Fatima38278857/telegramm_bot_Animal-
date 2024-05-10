package pro.sky.javacourse.AnimalShelterBot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.javacourse.AnimalShelterBot.model.TelegramContact;
import pro.sky.javacourse.AnimalShelterBot.service.TelegramContactService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("contacts")
public class TelegramContactController {
    private final TelegramContactService telegramContactService;

    public TelegramContactController(TelegramContactService telegramContactService) {
        this.telegramContactService = telegramContactService;
    }
    // CREATE - No need to create contacts manually, they should be created by bot

    // READ
    @GetMapping
    public ResponseEntity<Collection<TelegramContact>> getContacts() {
        Collection<TelegramContact> contacts = telegramContactService.getAll();
        if (contacts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(contacts));
    }
    @GetMapping("unsolved")
    public ResponseEntity<Collection<TelegramContact>> getUnsolvedContacts() {
        Collection<TelegramContact> contacts = telegramContactService.getUnsolved();
        if (contacts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(contacts));
    }
    @GetMapping("phone")
    public ResponseEntity<TelegramContact> findByphoneNumber(@RequestParam("phone") String phoneNumber) {
        TelegramContact contact = telegramContactService.findById(phoneNumber);
        if (contact == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contact);
    }

    //    UPDATE
    @PutMapping("solve")
    public ResponseEntity<TelegramContact> solve(@RequestBody TelegramContact contact) {
        TelegramContact telegramContact = telegramContactService.solve(contact);
        if (telegramContact == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(telegramContact);
    }

    //    DELETE - No need to delete contacts (for now)
}

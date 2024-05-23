package pro.sky.javacourse.AnimalShelterBot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
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
    @Operation(summary = "Получить список всех контактов.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection of TelegramContact objects.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = TelegramContact.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contacts not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Collection<TelegramContact>> getContacts() {
        Collection<TelegramContact> contacts = telegramContactService.getAll();
        if (contacts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(contacts));
    }
    @Operation(summary = "Получить список неотвеченных контактов.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection of TelegramContact objects.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = TelegramContact.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contacts not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("unsolved")
    public ResponseEntity<Collection<TelegramContact>> getUnsolvedContacts() {
        Collection<TelegramContact> contacts = telegramContactService.getUnsolved();
        if (contacts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(contacts));
    }

    @Operation(summary = "Найти контакт по телефонному номеру.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "TelegramContact object.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TelegramContact.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contact not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("phone")
    public ResponseEntity<TelegramContact> findByphoneNumber(@RequestParam("phone") String phoneNumber) {
        TelegramContact contact = telegramContactService.findById(phoneNumber);
        if (contact == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contact);
    }

    //    UPDATE
    @Operation(summary = "Отметить контакт как отвеченный. Установить статус контакта - solved.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Контакт пользователя в Телеграмм",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TelegramContact.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "TelegramContact with status solved.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TelegramContact.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "TelegramContact not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
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

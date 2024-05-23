package pro.sky.javacourse.AnimalShelterBot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("caretakers")
public class CaretakerController {
    private final CaretakerService caretakerService;

    public CaretakerController(CaretakerService caretakerService) {
        this.caretakerService = caretakerService;
    }

    //    CREATE
    @Operation(summary = "Добавить опекуна.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый опекун с заполненными данными.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Caretaker.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Caretaker object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Caretaker.class)
                            )
                    )
            }
    )
    @PostMapping
    public Caretaker add(@RequestBody Caretaker caretaker) {
        return caretakerService.add(caretaker);
    }

    //    READ
    @Operation(summary = "Получить список всех опекунов.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection of Caretaker objects.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Caretaker.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Caretakers not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Collection<Caretaker>> getCaretakers() {
        Collection<Caretaker> caretakers = caretakerService.getAll();
        if (caretakers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(caretakers));
    }

    @Operation(summary = "Найти опекуна по id.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Caretaker object.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Caretaker.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Caretaker not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/id")
    public ResponseEntity<Caretaker> find(@RequestParam Long id) {
        Caretaker caretaker = caretakerService.find(id);
        if (caretaker == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(caretaker);
    }

    @Operation(summary = "Найти опекуна по chatId.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Caretaker object.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Caretaker.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Caretaker not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/chatId")
    public ResponseEntity<Caretaker> findByChatId(@RequestParam Long chatId) {
        Caretaker caretaker = caretakerService.findByChatId(chatId);
        if (caretaker == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(caretaker);
    }

    //    UPDATE
    @Operation(summary = "Редактировать данные опекуна.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактируемый опекун с измененными данными.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Caretaker.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Caretaker object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Caretaker.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Caretaker not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping
    public ResponseEntity<Caretaker> edit(@RequestBody Caretaker caretaker) {
        Caretaker updatedCaretaker = caretakerService.edit(caretaker);
        if (caretaker == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCaretaker);
    }

    //    DELETE
    @Operation(summary = "Удалить опекуна из базы данных.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Опекун которого нужно удалить из базы.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Caretaker.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Method always returns void."
                    )
            }
    )
    @DeleteMapping
    public void delete(@RequestBody Caretaker caretaker) {
        caretakerService.delete(caretaker);
    }
}

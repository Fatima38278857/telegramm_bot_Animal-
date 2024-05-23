package pro.sky.javacourse.AnimalShelterBot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;
import pro.sky.javacourse.AnimalShelterBot.service.VolunteerService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("volunteers")
public class VolunteerController {
    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService, ShelterService shelterService) {
        this.volunteerService = volunteerService;
    }

    //    CREATE
    @Operation(summary = "Добавить волонтера.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый волонтер с заполненными данными.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Volunteer.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Volunteer object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Volunteer.class)
                            )
                    )
            }
    )
    @PostMapping
    public Volunteer add(@RequestBody Volunteer volunteer) {
        return volunteerService.add(volunteer);
    }

    // READ
    @Operation(summary = "Получить список всех волонтеров.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection of Volunteer objects.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Volunteers not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Collection<Volunteer>> getVolunteers() {
        Collection<Volunteer> volunteers = volunteerService.getAll();
        if (volunteers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(volunteers));
    }

    @Operation(summary = "Найти волонтера по id.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Volunteer object.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Volunteer.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Volunteer not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/id")
    public ResponseEntity<Volunteer> find(@RequestParam Long id) {
        Volunteer volunteer = volunteerService.find(id);
        if (volunteer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(volunteer);
    }

    @Operation(summary = "Найти волонтера по chatId.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Volunteer object.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Volunteer.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Volunteer not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/chatId")
    public ResponseEntity<Volunteer> findByChatId(@RequestParam Long chatId) {
        Volunteer volunteer = volunteerService.findByChatId(chatId);
        if (volunteer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(volunteer);
    }

    @Operation(summary = "Получить список приютов в которых числится волонтер по id волонтера.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection of Shelter objects.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Shelter.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shelters not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/shelters")
    public ResponseEntity<Collection<Shelter>> getShelters(@RequestParam Long id) {
        Collection<Shelter> shelters;
        try {
            shelters = volunteerService.getShelters(id);
        } catch (NullPointerException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(shelters));
    }

    // UPDATE
    @Operation(summary = "Добавить волонтера в приют.",
            parameters = {
                    @Parameter(name = "id", description = "id волонтера", in = ParameterIn.PATH),
                    @Parameter(name = "shelterId", description = "id приюта", in = ParameterIn.QUERY),
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Method always return void"
                    )
            }
    )
    @PutMapping(value = "{id}/shelter")
    public ResponseEntity<String> addToShelter(@PathVariable("id") Long id, Long shelterId) {
        try {
            volunteerService.addToShelter(id, shelterId);
        } catch (NullPointerException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить волонтера из указанного приюта.",
            parameters = {
                    @Parameter(name = "id", description = "id волонтера", in = ParameterIn.PATH),
                    @Parameter(name = "shelterId", description = "id приюта", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Method always returns void."
                    )
            }
    )
    @PutMapping(value = "{id}/removeShelter")
    public ResponseEntity<String> removeFromShelter(@PathVariable("id") Long id, Long shelterId) {
        volunteerService.removeFromShelter(id, shelterId);
        return ResponseEntity.ok().build();
    }

    // DELETE
    @Operation(summary = "Удалить волонтера из базы данных. Предлагается использовать только для ошибочно добавленных волонтеров.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Волонтер которого нужно удалить из базы.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Volunteer.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Method always returns void."
                    )
            }
    )
    @DeleteMapping
    public void delete(@RequestBody Volunteer volunteer) {
        volunteerService.delete(volunteer);
    }
}

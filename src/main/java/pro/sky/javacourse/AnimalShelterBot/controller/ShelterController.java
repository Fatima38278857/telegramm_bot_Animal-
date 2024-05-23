package pro.sky.javacourse.AnimalShelterBot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;
import pro.sky.javacourse.AnimalShelterBot.service.VolunteerService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("shelters")
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService, VolunteerService volunteerService) {
        this.shelterService = shelterService;
    }

    @Operation(summary = "Добавить приют.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый приют с заполненными данными.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shelter object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    )
            }
    )
    @PostMapping
    public Shelter add(@RequestBody Shelter shelter) {
        return shelterService.add(shelter);
    }

    @Operation(summary = "Получить список всех приютов.",
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
    @GetMapping
    public ResponseEntity<Collection<Shelter>> getShelters() {
        Collection<Shelter> shelters = shelterService.getAll();
        if (shelters.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(shelters));
    }

    @Operation(summary = "Найти приют по id.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shelter object.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shelter not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/id")
    public ResponseEntity<Shelter> find(@RequestParam Long id) {
        Shelter shelter = shelterService.find(id);
        if (shelter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shelter);
    }

    @Operation(summary = "Найти приют по имени. Имя приюта обязано быть уникальным.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shelter object.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shelter not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/name")
    public ResponseEntity<Shelter> find(@RequestParam String name) {
        Shelter shelter = shelterService.findShelterByName(name);
        if (shelter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shelter);
    }

    @Operation(summary = "Найти главного волонтера приюта по id приюта.",
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
                            description = "Main volunteer not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/mainVolunteer")
    public ResponseEntity<Volunteer> getMainVolunteer(@RequestParam Long shelterId) {
        Volunteer mainVolunteer = shelterService.getMainVolunteer(shelterId);
        if (mainVolunteer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mainVolunteer);
    }

    @Operation(summary = "Получить список волонтеров конкретного приюта по id приюта.",
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
                            description = "Shelter not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/volunteers")
    public ResponseEntity<Collection<Volunteer>> getVolunteers(@RequestParam Long shelterId) {
        Collection<Volunteer> volunteers = shelterService.getVolunteers(shelterId);
        if (volunteers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(volunteers));
    }

    @Operation(summary = "Найти схему проезда приюта по id приюта.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "InputStreamResource.",
                            content = @Content(
                                    mediaType = MediaType.IMAGE_JPEG_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shelter or location map not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/locationMap")
    public ResponseEntity<InputStreamResource> showLocationMap(@RequestParam("id") Long id) {
        Shelter shelter = shelterService.find(id);
        if (shelter == null) {
            return ResponseEntity.notFound().build();
        }
        String filePath = shelter.getLocationMapFilePath();
        File file = new File(filePath);
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType(shelter.getLocationMapMediaType()))
                .body(new InputStreamResource(fileInputStream));
    }

    @Operation(summary = "Редактировать данные приюта.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Приют с измененными данными.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Shelter object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shelter not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping
    public ResponseEntity<Shelter> edit(@RequestBody Shelter shelter) {
        Shelter updatedshelter = shelterService.edit(shelter);
        if (updatedshelter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedshelter);
    }

    @Operation(summary = "Добавить карту проезда к приюту.",
            description = "Добавляет карту проезда для приюта с указанным id.",
            parameters = {
                    @Parameter(name = "id", description = "id приюта", in = ParameterIn.PATH),
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Изображение с картой проезда к приюту в формате jpg. Размеры изображения не должны быть больше 1000x1000 пикселей.",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated shelter with locationMap file path.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "File must not be larger then 1000x1000 pixels.",
                            content = @Content(schema = @Schema())
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shelter not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping(value = "{id}/location", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Shelter> uploadLocationMap(@PathVariable("id") Long shelterId, @RequestParam MultipartFile locationMapFile) throws IOException {
        if (locationMapFile.getSize() > 1000 * 1000) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(shelterService.uploadLocationMap(shelterId, locationMapFile));
        } catch (NullPointerException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Установить главного волонтера в приют с указанным id.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Приют в который назначается главный волонтер.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class))
            ),
            parameters = {
                    @Parameter(name = "mainVolunteerId", description = "id волонтера", in = ParameterIn.QUERY),
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Shelter object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shelter or volunteer not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping(value = "/mainVolunteer")
    public ResponseEntity<Shelter> setMainVolunteer(@RequestBody Shelter shelter,
                                                    @RequestParam("mainVolunteerId") Long mainVolunteerId) {
        Shelter shelterFromDb;
        try {
            shelterFromDb = shelterService.setMainVolunteer(shelter, mainVolunteerId);
        } catch (NullPointerException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shelterFromDb);
    }

    @Operation(summary = "Удалить приют из базы данных.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Приют которого нужно удалить из базы.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Method always returns void."
                    )
            }
    )
    @DeleteMapping
    public void delete(@RequestBody Shelter shelter) {
        shelterService.delete(shelter);
    }

}

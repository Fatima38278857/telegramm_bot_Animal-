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
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.PetStatus;
import pro.sky.javacourse.AnimalShelterBot.service.PetService;

import java.io.*;
import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("pets")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    // CREATE
    @Operation(summary = "Добавить питомца.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый питомец с заполненными данными.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pet object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Wrong shelter id.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PostMapping
    public Pet add(@RequestBody Pet pet) {
        return petService.add(pet);
    }

    // UPDATE
    @Operation(summary = "Добавить аватар питомцу.",
            description = "Добавляет аватар для питомца с указанным id.",
            parameters = {
                    @Parameter(name = "id", description = "id питомца", in = ParameterIn.PATH),
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Изображение питомца в формате jpg. Размеры изображения не должны быть больше 1000x1000 пикселей.",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated pet with avatar file path.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "File must not be larger then 1000x1000 pixels.",
                            content = @Content(schema = @Schema())
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping(value = "{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Pet> uploadAvatar(@PathVariable("id") Long id,
                                               @RequestParam MultipartFile avatarFile) throws IOException {
        if (avatarFile.getSize() > 1000 * 1000) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(petService.uploadAvatar(id, avatarFile));
        } catch (NullPointerException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Редактировать данные питомца.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактируемый питомец с измененными данными.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Pet object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping
    public ResponseEntity<Pet> edit(@RequestBody Pet pet) {
        Pet updatedPet = petService.edit(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }
    @Operation(summary = "Выдать питомца опекуну, назначив испытательный срок.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Питомец из базы данных.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            parameters = {
                    @Parameter(name = "caretakerId", description = "id опекуна", in = ParameterIn.QUERY),
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Pet object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet or caretaker not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping("trial/start")
    public ResponseEntity<Pet> startTrial(@RequestBody Pet pet, @RequestParam Long caretakerId) {
        Pet updatedPet = petService.startTrial(pet, caretakerId);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }
    @Operation(summary = "Продлить испытательный срок опекуну данного питомца на 15 дней.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Питомец из базы данных.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Pet object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet is not on adoption or not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping("trial/plus15")
    public ResponseEntity<Pet> trialAdd15(@RequestBody Pet pet) {
        Pet updatedPet = petService.trialAdd15(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }
    @Operation(summary = "Продлить испытательный срок опекуну данного питомца на 30 дней.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Питомец из базы данных.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Pet object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet is not on adoption or not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping("trial/plus30")
    public ResponseEntity<Pet> trialAdd30(@RequestBody Pet pet) {
        Pet updatedPet = petService.trialAdd30(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }
    @Operation(summary = "Выписать усыновленного питомца из приюта. Установить статус питомца - УСЫНОВЛЕН.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Питомец из базы данных.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Pet object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet is not on adoption or not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping("adopt")
    public ResponseEntity<Pet> adopt(@RequestBody Pet pet) {
        Pet updatedPet = petService.adopt(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }
    @Operation(summary = "Забрать питомца у опекуна. Установить статус питомца - ОФОРМЛЯЕТСЯ.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Питомец из базы данных.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Pet object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet is not on adoption or not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping("returned")
    public ResponseEntity<Pet> returned(@RequestBody Pet pet) {
        Pet updatedPet = petService.returned(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }
    @Operation(summary = "Поместить питомца под процедуру возврата от опекуна. Установить статус питомца - ВОЗВРАТ.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Питомец из базы данных.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Pet object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet is not on adoption or not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping("suspended")
    public ResponseEntity<Pet> suspended(@RequestBody Pet pet) {
        Pet updatedPet = petService.suspended(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }
    @Operation(summary = "Сделать питомца доступным для просмотра в боте и усыновления. Установить статус питомца - ДОСТУПЕН.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Питомец из базы данных.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Pet object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet is not in right status or not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping("available")
    public ResponseEntity<Pet> available(@RequestBody Pet pet) {
        Pet updatedPet = petService.available(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }
    @Operation(summary = "Отметить заболевшего питомца. Установить статус питомца - БОЛЕН.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Питомец из базы данных.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated Pet object from database.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet is not in right status or not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @PutMapping("ill")
    public ResponseEntity<Pet> ill(@RequestBody Pet pet) {
        Pet updatedPet = petService.ill(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }


    // READ
    @Operation(summary = "Получить список всех питомцев.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection of Pet objects.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pets not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Collection<Pet>> getPets() {
        Collection<Pet> pets = petService.getAll();
        if (pets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(pets));
    }

    @Operation(summary = "Найти питомца по id.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pet object.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pet not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/id")
    public ResponseEntity<Pet> find(@RequestParam Long id) {
        Pet pet = petService.find(id);
        if (pet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pet);
    }

    @Operation(summary = "Получить список питомцев по имени.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection of Pet objects.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pets not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/name")
    public ResponseEntity<Collection<Pet>> find(@RequestParam String name) {
        Collection<Pet> pets = petService.find(name);
        if (pets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(pets));
    }

    @Operation(summary = "Получить список питомцев по id приюта.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection of Pet objects.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pets not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/shelter")
    public ResponseEntity<Collection<Pet>> findByShelterId(@RequestParam Long shelterId) {
        Collection<Pet> pets = petService.findByShelterId(shelterId);
        if (pets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(pets));
    }

    @Operation(summary = "Получить список питомцев по статусу питомца.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection of Pet objects.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pets not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("{status}")
    public ResponseEntity<Collection<Pet>> findByStatus(@RequestParam PetStatus status) {
        Collection<Pet> pets = petService.findByStatus(status);
        if (pets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(pets));
    }

    @Operation(summary = "Получить список доступных питомцев.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection of Pet objects.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pets not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/available")
    public ResponseEntity<Collection<Pet>> findByStatus(@RequestParam Long shelterId) {
        Collection<Pet> pets = petService.findAvailableByShelterId(shelterId);
        if (pets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(pets));
    }

    @Operation(summary = "Найти аватар питомца по id питомца.",
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
                            description = "Pet or avatar not found.",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/avatar")
    public ResponseEntity<InputStreamResource> showAvatar(@RequestParam("id") Long id) {
        Pet pet = petService.find(id);
        if (pet == null) {
            return ResponseEntity.notFound().build();
        }
        String filePath = pet.getAvatarFilePath();
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
                .contentType(MediaType.parseMediaType(pet.getAvatarMediaType()))
                .body(new InputStreamResource(fileInputStream));
    }

    // DELETE - может быть использован для удаления некорректно введенного питомца
    @Operation(summary = "Удалить питомца из базы данных. Предлагается использовать только для ошибочно добавленных питомцев.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Питомец которого нужно удалить из базы.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Method always returns void."
                    )
            }
    )
    @DeleteMapping
    public void delete(@RequestBody Pet pet) {
        petService.delete(pet);
    }

}

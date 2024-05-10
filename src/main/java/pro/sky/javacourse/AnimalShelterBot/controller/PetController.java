package pro.sky.javacourse.AnimalShelterBot.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.PetStatus;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.service.PetService;

import java.io.IOException;
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
    @PostMapping
    public Pet add(@RequestBody Pet pet) {
        return petService.add(pet);
    }

    // UPDATE
    @PutMapping(value = "{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable("id") Long petId,
                                               @RequestBody @RequestParam MultipartFile avatarFile) throws IOException {
        if (avatarFile.getSize() > 1000 * 1000) {
            return ResponseEntity.badRequest().body("File must not be larger then 1000x1080 pixels.");
        }
        petService.uploadAvatar(petId, avatarFile);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Pet> edit(@PathVariable("id") Long id, @RequestBody Pet pet) {
        Pet updatedPet = petService.edit(id, pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }

    @PutMapping("trial/start")
    public ResponseEntity<Pet> startTrial(@RequestBody Pet pet, @RequestBody Caretaker caretaker) {
        Pet updatedPet = petService.startTrial(pet, caretaker);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }

    @PutMapping("trial/plus15")
    public ResponseEntity<Pet> trialAdd15(@RequestBody Pet pet) {
        Pet updatedPet = petService.trialAdd15(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }
    @PutMapping("trial/plus30")
    public ResponseEntity<Pet> trialAdd30(@RequestBody Pet pet) {
        Pet updatedPet = petService.trialAdd30(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }

    @PutMapping("adopt")
    public ResponseEntity<Pet> adopt(@RequestBody Pet pet) {
        Pet updatedPet = petService.adopt(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }

    @PutMapping("returned")
    public ResponseEntity<Pet> returned(@RequestBody Pet pet) {
        Pet updatedPet = petService.returned(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }

    @PutMapping("suspended")
    public ResponseEntity<Pet> suspended(@RequestBody Pet pet) {
        Pet updatedPet = petService.suspended(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }

    @PutMapping("available")
    public ResponseEntity<Pet> available(@RequestBody Pet pet) {
        Pet updatedPet = petService.available(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }

    @PutMapping("ill")
    public ResponseEntity<Pet> ill(@RequestBody Pet pet) {
        Pet updatedPet = petService.ill(pet);
        if (updatedPet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPet);
    }


    // READ
    @GetMapping
    public ResponseEntity<Collection<Pet>> getPets() {
        Collection<Pet> pets = petService.getAll();
        if (pets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(pets));
    }

    @GetMapping("/id")
    public ResponseEntity<Pet> find(@RequestParam Long id) {
        Pet pet = petService.find(id);
        if (pet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pet);
    }

    @GetMapping("/name")
    public ResponseEntity<Pet> find(@RequestParam String name) {
        Pet pet = petService.find(name);
        if (pet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pet);
    }

    @GetMapping("{status}")
    public ResponseEntity<Collection<Pet>> findByStatus(@RequestParam PetStatus status) {
        Collection<Pet> pets = petService.findByStatus(status);
        if (pets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(pets));
    }

    // DELETE - может быть сипользован


}

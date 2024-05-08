package pro.sky.javacourse.AnimalShelterBot.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.dto.CaretakerAvatarDto;
import pro.sky.javacourse.AnimalShelterBot.mapper.CaretakerAvatarMapper;
import pro.sky.javacourse.AnimalShelterBot.model.CaretakerAvatar;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerAvatarService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("caretakerAvatar")
public class CaretakerAvatarController {
    private final CaretakerAvatarService avatarService;

    private final CaretakerAvatarMapper avatarMapper;


    public CaretakerAvatarController(CaretakerAvatarService caretakerService, CaretakerAvatarMapper avatarMapper) {
        this.avatarService = caretakerService;
        this.avatarMapper = avatarMapper;
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("File is too big");
        }

        avatarService.uploadAvatar(id, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long id) {
        CaretakerAvatar avatar = avatarService.findAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/{id}/avatar")
    public void downloadAvatar(@PathVariable Long id, HttpServletResponse response) throws IOException {
        CaretakerAvatar avatar = avatarService.findAvatar(id);

        Path path = Path.of(avatar.getFilePath());

        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream();) {
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        }
    }

    @GetMapping
    public List<CaretakerAvatarDto> getPaginateAvatar(
            @RequestParam int pageNumber,
            @RequestParam int pageSize
    ) {
        return avatarService.getPaginatedAvatar(pageNumber, pageSize)
                .stream()
                .map(avatarMapper::mapToDTO)
                .collect(Collectors.toList());
    }

}

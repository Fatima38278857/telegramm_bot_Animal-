package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.model.ReportMessage;
import pro.sky.javacourse.AnimalShelterBot.model.ReportStatus;
import pro.sky.javacourse.AnimalShelterBot.repository.ReportMessageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ReportMessageServiceImpl implements ReportMessageService {
    private final ReportMessageRepository messageRepository;
    private final Logger logger = LoggerFactory.getLogger(ReportMessageServiceImpl.class);

    public ReportMessageServiceImpl(ReportMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    @Override
    public String getExtensions(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public ReportMessage save(ReportMessage message) {
        if (message.getFilePath() != null) {
            String filePath = message.getFilePath();
            String updatedFilePath = filePath.replace(ReportStatus.INCOMPLETE.name(), "");
            try {
                Files.move(Path.of(filePath), Path.of(updatedFilePath));
            } catch (IOException e) {
                logger .info("Error renaming photo: " + filePath);
                throw new RuntimeException(e);
            }
        }
        return messageRepository.save(message);
    }
}

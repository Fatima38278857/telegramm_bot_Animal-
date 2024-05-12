package com.example.ShelterBot.bot.service;

import com.example.ShelterBot.bot.implementation.ImplementationBot;
import com.example.ShelterBot.bot.model.Report;
import com.example.ShelterBot.bot.repository.ReportRepository;
import com.example.ShelterBot.bot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
;import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


@Component
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(ImplementationBot.class);

    public Report readReport(long id) {
        Report report = new Report();
        report.getId();
        String text = report.getText();
        report.getPhoto();
        report.getDateTime();
        report.getUser();
        report.getStatusReport();
        reportRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Такого факультета нет"));
        return report;
    }
}

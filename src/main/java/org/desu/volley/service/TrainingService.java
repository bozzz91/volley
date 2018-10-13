package org.desu.volley.service;

import org.desu.volley.domain.Training;
import org.desu.volley.domain.TrainingUser;
import org.desu.volley.domain.enumeration.TrainingState;
import org.desu.volley.repository.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingService {
    private final Logger log = LoggerFactory.getLogger(TrainingService.class);

    @Inject
    private TrainingRepository trainingRepository;

    @Inject
    private UserService userService;

    @Scheduled(cron = "0 5/15 * * * ?")
    public void updateTrainingState() {
        List<Training> trainings = trainingRepository.findByStateNot(TrainingState.DONE);
        log.info("Found {} not finished trainings", trainings.size());
        ZonedDateTime now = ZonedDateTime.now();

        List<Training> saved = trainingRepository.save(trainings.stream()
            .filter(training -> training.getStartAt().isBefore(now))
            .peek(training -> training.setState(TrainingState.PROCESS))
            .collect(Collectors.toList()));
        if (!saved.isEmpty())
            log.info("Updated {} trainings to {}", saved.size(), TrainingState.PROCESS);

        saved = trainingRepository.save(trainings.stream()
            .filter(training -> training.getEndAt().isBefore(now))
            .peek(training -> training.setState(TrainingState.DONE))
            .collect(Collectors.toList()));
        if (!saved.isEmpty())
            log.info("Updated {} trainings to {}", saved.size(), TrainingState.DONE);
    }

    public List<Training> amendTrainingOrganizer(List<Training> trainings) {
        for (Training training : trainings) {
            amendTrainingOrganizer(training);
        }
        return trainings;
    }

    public Training amendTrainingUsers(Training training) {
        for (TrainingUser trainingUser : training.getTrainingUsers()) {
            userService.amendPersonalData(trainingUser.getUser());
        }
        return amendTrainingOrganizer(training);
    }

    private Training amendTrainingOrganizer(Training training) {
        userService.amendPersonalData(training.getOrganizer());
        return training;
    }
}

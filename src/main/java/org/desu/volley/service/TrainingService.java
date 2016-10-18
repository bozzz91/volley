package org.desu.volley.service;

import org.desu.volley.domain.Training;
import org.desu.volley.domain.enumeration.TrainingState;
import org.desu.volley.repository.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class TrainingService {
    private final Logger log = LoggerFactory.getLogger(TrainingService.class);

    @Inject
    private TrainingRepository trainingRepository;

    @Scheduled(cron = "0 5/20/35/50 * * * ?")
    public void updateTrainingState() {
        log.info("updateTrainingState.start; Start training state update");
        List<Training> trainings = trainingRepository.findByState(TrainingState.REGISTRATION);
        log.info("Found {} trainings in state: {}", trainings.size(), TrainingState.REGISTRATION);
        ZonedDateTime now = ZonedDateTime.now();
        Collection<Training> toSave = new ArrayList<>();
        trainings.stream()
            .filter(training -> training.getStartAt().isAfter(now))
            .forEach(training -> {
                training.setState(TrainingState.PROCESS);
                toSave.add(training);
            });
        trainingRepository.save(toSave);
        log.info("Updated {} trainings to state {}", toSave.size(), TrainingState.PROCESS);
        toSave.clear();

        trainings = trainingRepository.findByState(TrainingState.PROCESS);
        log.info("Found {} trainings in state: {}", trainings.size(), TrainingState.PROCESS);
        trainings.stream()
            .filter(training -> training.getEndAt().isAfter(now))
            .forEach(training -> {
                training.setState(TrainingState.DONE);
                toSave.add(training);
            });
        trainingRepository.save(toSave);
        log.info("updateTrainingState.end; Updated {} trainings to state {}", toSave.size(), TrainingState.DONE);
    }
}

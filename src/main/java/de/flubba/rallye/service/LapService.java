package de.flubba.rallye.service;

import de.flubba.rallye.configuration.RallyeProperties;
import de.flubba.rallye.entity.Lap;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.repository.LapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class LapService {
    private final TagService tagService;
    private final LapRepository lapRepository;
    private final RallyeProperties rallyeProperties;
    private final Clock clock;

    public Runner countLap(String tagId) throws LapCountingException {
        var tagAssignment = tagService.getTagAssignment(tagId)
                .orElseThrow(() -> new LapCountingException("Tag %s is not registered.".formatted(tagId)));
        var runner = tagService.findRunner(tagAssignment)
                .orElseThrow(() -> new LapCountingException("No runner with id %s found for counting laps.".formatted(tagAssignment.getRunnerId())));
        long duration = getLapDuration(runner);
        saveNewLap(runner, duration);
        LapBroadcaster.broadcast(runner, duration);
        return runner;
    }

    private void saveNewLap(Runner runner, long duration) {
        Lap lap = new Lap();
        lap.setTime(System.currentTimeMillis());
        lap.setRunner(runner);
        lap.setDuration(duration);
        lapRepository.saveAndFlush(lap);
    }

    private long getLapDuration(Runner runner) throws LapCountingException {
        long duration = 0;
        long currentTime = System.currentTimeMillis();
        Lap lastLap = lapRepository.findLastLap(runner);
        if (lastLap != null) {
            duration = currentTime - lastLap.getTime();
            if (duration < rallyeProperties.getMinLapDuration().toMillis()) {
                throw new LapCountingException("Lap too short! Was only %s. Minimum lap duration is %s seconds."
                        .formatted(duration / 1000, rallyeProperties.getMinLapDuration()));
            }
        }
        return duration;
    }

    public static final class LapCountingException extends Exception {
        private LapCountingException(String message) {
            super(message);
        }
    }
}

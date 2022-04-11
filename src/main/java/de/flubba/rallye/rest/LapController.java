package de.flubba.rallye.rest;

import de.flubba.rallye.rest.dto.RunnerDto;
import de.flubba.rallye.service.LapService;
import de.flubba.rallye.service.LapService.LapCountingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LapController {
    private final LapService lapService;

    @PostMapping(value = "/countLap")
    @SuppressWarnings("squid:S1452") // generic Wildcard is sensible here
    public ResponseEntity<?> countLap(String tagId) {
        try {
            var runner = lapService.countLap(tagId);
            return ResponseEntity.ok(new RunnerDto(runner.getId(), runner.getName()));
        } catch (LapCountingException e) {
            log.warn("Did not count lap: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}

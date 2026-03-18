package de.flubba.rallye.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PingController {

    @GetMapping("/ping")
    public ResponseEntity<Void> ping() {
        log.info("Ping received");
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

package de.flubba.rallye.rest;

import de.flubba.rallye.entity.TagAssignment;
import de.flubba.rallye.service.TagService;
import de.flubba.rallye.service.TagService.AssignmentAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class TagController {

    private static final Logger log = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;

    @RequestMapping(value = "/getTagAssignment")
    public ResponseEntity<TagAssignment> getTagAssignment(String tagId) {
        var tagAssignment = tagService.getTagAssignment(tagId);
        return ResponseEntity.status(tagAssignment.isPresent() ? OK : NOT_FOUND).body(tagAssignment.orElse(null));
    }

    @PostMapping(value = "/setTagAssignment")
    public ResponseEntity<String> setTagAssignment(@RequestParam(defaultValue = "false") boolean overwrite,
                                                   String tagId,
                                                   Long runnerId) {
        try {
            var tagAssignment = tagService.assignTag(overwrite, TagAssignment.builder().tagId(tagId).runnerId(runnerId).build());
            return ResponseEntity.ok("Tag %s assigned to runner %s.".formatted(tagAssignment.getTagId(), tagAssignment.getRunnerId()));
        } catch (AssignmentAlreadyExistsException e) {
            log.info("Did not create tag assignment: {}", e.getMessage());
            return ResponseEntity.status(CONFLICT).body(e.getMessage());
        }
    }

}

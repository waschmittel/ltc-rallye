package de.flubba.rallye.service;

import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.TagAssignment;
import de.flubba.rallye.entity.repository.RunnerRepository;
import de.flubba.rallye.entity.repository.TagAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final TagAssignmentRepository tagAssignmentRepository;
    private final RunnerRepository runnerRepository;

    public Optional<TagAssignment> getTagAssignment(String tagId) {
        return tagAssignmentRepository.findOneByTagId(tagId);
    }

    public TagAssignment assignTag(boolean overwrite, TagAssignment tagAssignment) throws AssignmentAlreadyExistsException {
        if (overwrite) {
            deleteExistingAssignment(tagAssignment);
        }
        checkIfTagAssignmentExists(tagAssignment);
        tagAssignmentRepository.saveAndFlush(tagAssignment);
        log.debug("Tag {} assigned to runner {}", tagAssignment.getTagId(), tagAssignment.getRunnerId());
        return tagAssignment;
    }

    private void deleteExistingAssignment(TagAssignment tagAssignment) {
        log.info("Deleting existing assignments for {} and runner {}.", tagAssignment.getTagId(), tagAssignment.getRunnerId());
        var existingAssignment = tagAssignmentRepository.findOneByRunnerId(tagAssignment.getRunnerId());
        if (existingAssignment.isPresent()) {
            tagAssignmentRepository.delete(existingAssignment.get());
        }
        existingAssignment = tagAssignmentRepository.findOneByTagId(tagAssignment.getTagId());
        if (existingAssignment.isPresent()) {
            tagAssignmentRepository.delete(existingAssignment.get());
        }
    }

    private void checkIfTagAssignmentExists(TagAssignment tagAssignment) throws AssignmentAlreadyExistsException {
        var existingAssignment = tagAssignmentRepository.findOneByRunnerId(tagAssignment.getRunnerId());
        if (existingAssignment.isPresent()) {
            throw new AssignmentAlreadyExistsException(existingAssignment.get());
        }
        existingAssignment = tagAssignmentRepository.findOneByTagId(tagAssignment.getTagId());
        if (existingAssignment.isPresent()) {
            throw new AssignmentAlreadyExistsException(existingAssignment.get());
        }
    }

    public static final class AssignmentAlreadyExistsException extends Exception {
        private AssignmentAlreadyExistsException(TagAssignment tagAssignment) {
            super(String.format("Assignment of %s to runner %s already exists.", tagAssignment.getTagId(), tagAssignment.getRunnerId()));
        }
    }

    public Optional<Runner> findRunner(TagAssignment tagAssignment) {
        return runnerRepository.findOneById(tagAssignment.getRunnerId());
    }
}

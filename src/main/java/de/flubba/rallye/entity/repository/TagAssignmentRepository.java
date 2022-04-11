package de.flubba.rallye.entity.repository;

import de.flubba.rallye.entity.TagAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagAssignmentRepository extends JpaRepository<TagAssignment, Long> {
    Optional<TagAssignment> findOneByTagId(String tagId);

    Optional<TagAssignment> findOneByRunnerId(Long runnerId);
}

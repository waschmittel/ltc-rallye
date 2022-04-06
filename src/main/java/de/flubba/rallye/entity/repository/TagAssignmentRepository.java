package de.flubba.rallye.entity.repository;

import de.flubba.rallye.entity.TagAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagAssignmentRepository extends JpaRepository<TagAssignment, Long> {
    TagAssignment findOneByTagId(String tagId);

    TagAssignment findOneByRunnerId(Long runnerId);
}

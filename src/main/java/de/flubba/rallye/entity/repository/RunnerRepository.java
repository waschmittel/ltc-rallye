package de.flubba.rallye.entity.repository;

import de.flubba.rallye.entity.Runner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RunnerRepository extends JpaRepository<Runner, Long> {
    Optional<Runner> findOneById(Long runnerId);

    Long countByName(String name);

    @Query("select r from Runner r where lower(r.name) like lower(concat('%', :name,'%'))")
    List<Runner> findByNameIgnoreCaseContaining(String name);

    List<Runner> findByNameIgnoreCaseContainingAndGenderIs(String name, Runner.Gender gender); //TODO: add this filter again for results
}

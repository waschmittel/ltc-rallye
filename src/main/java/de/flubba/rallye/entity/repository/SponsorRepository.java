package de.flubba.rallye.entity.repository;

import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    long countByRunner(Runner runner);

    List<Sponsor> findByRunner(Runner runner);
}

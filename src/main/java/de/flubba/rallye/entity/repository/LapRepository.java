package de.flubba.rallye.entity.repository;

import de.flubba.rallye.entity.Lap;
import de.flubba.rallye.entity.Runner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LapRepository extends JpaRepository<Lap, Long> {
    @Query("select l from Lap l where l.runner = ?1 and l.time in (select max(laps.time) from Lap laps where laps.runner = ?1)")
    Optional<Lap> findLastLap(Runner runner);

    List<Lap> findByRunner(Runner runner);
}

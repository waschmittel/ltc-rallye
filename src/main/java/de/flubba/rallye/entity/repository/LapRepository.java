package de.flubba.rallye.entity.repository;

import de.flubba.rallye.entity.Lap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LapRepository extends JpaRepository<Lap, Long> {
    @Query("select l from Lap l where l.runnerId = ?1 and l.time in (select max(laps.time) from Lap laps where laps.runnerId = ?1)")
    Optional<Lap> findLastLap(Long runnerId);

    List<Lap> findByRunnerId(Long runner);
}

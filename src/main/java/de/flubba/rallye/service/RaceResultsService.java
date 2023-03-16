package de.flubba.rallye.service;

import de.flubba.rallye.entity.Lap;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.Sponsor;
import de.flubba.rallye.entity.repository.LapRepository;
import de.flubba.rallye.entity.repository.RunnerRepository;
import de.flubba.rallye.entity.repository.SponsorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class RaceResultsService {
    private final RunnerRepository runnerRepository;
    private final LapRepository lapRepository;
    private final SponsorRepository sponsorRepository;

    @Autowired
    public RaceResultsService(RunnerRepository runnerRepository, LapRepository lapRepository, SponsorRepository sponsorRepository) {
        this.runnerRepository = runnerRepository;
        this.lapRepository = lapRepository;
        this.sponsorRepository = sponsorRepository;
    }

    public void generateResults() {
        runnerRepository.findAll().forEach(runner -> {
            calculateLaps(runner);
            calculateDonationsAndSponsors(runner);
            calculateAverage(runner);
            runnerRepository.saveAndFlush(runner);
        });
    }

    private void calculateLaps(Runner runner) {
        runner.setNumberOfLapsRun(lapRepository.findByRunnerId(runner.getId()).size());
    }

    private void calculateDonationsAndSponsors(Runner runner) {
        List<Sponsor> sponsors = sponsorRepository.findByRunnerId(runner.getId());
        BigDecimal runnersTotalDonation = sponsors.stream()
                .map(sponsor -> saveSponsorsTotalDonation(runner, sponsor))
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal("0.00"));
        runner.setDonations(runnersTotalDonation);
        runner.setNumberOfSponsors(sponsors.size());
    }

    private BigDecimal saveSponsorsTotalDonation(Runner runner, Sponsor sponsor) {
        BigDecimal oneTimeDonation = Optional.ofNullable(sponsor.getOneTimeDonation()).orElse(BigDecimal.ZERO);
        BigDecimal lapDonation = getLapDonation(sponsor, runner);

        BigDecimal totalDonation = lapDonation.add(oneTimeDonation).setScale(2, RoundingMode.HALF_UP);

        sponsor.setTotalDonation(totalDonation);
        sponsorRepository.saveAndFlush(sponsor);

        return totalDonation;
    }

    private static BigDecimal getLapDonation(Sponsor sponsor, Runner runner) {
        Long bonusLaps = Optional.ofNullable(runner.getBonusLaps()).orElse(0L);
        BigDecimal allLaps = new BigDecimal(runner.getNumberOfLapsRun() + bonusLaps);
        BigDecimal perLapDonation = sponsor.getPerLapDonation();

        return allLaps.multiply(Optional.ofNullable(perLapDonation).orElse(BigDecimal.ZERO));
    }

    private void calculateAverage(Runner runner) {
        runner.setAverage(getAverage(lapRepository.findByRunnerId(runner.getId())));
    }

    private static BigDecimal getAverage(List<Lap> laps) {
        double average = laps.stream()
                .mapToLong(Lap::getDuration)
                .filter(duration -> duration != 0)
                .average()
                .orElse(0);
        return BigDecimal.valueOf(average)
                .divide(new BigDecimal(1000L), RoundingMode.HALF_UP)
                .setScale(3, RoundingMode.HALF_UP);
    }
}

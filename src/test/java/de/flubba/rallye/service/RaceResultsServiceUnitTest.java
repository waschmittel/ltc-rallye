package de.flubba.rallye.service;

import de.flubba.rallye.entity.Lap;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.Sponsor;
import de.flubba.rallye.entity.repository.LapRepository;
import de.flubba.rallye.entity.repository.RunnerRepository;
import de.flubba.rallye.entity.repository.SponsorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RaceResultsServiceUnitTest {
    @Mock
    RunnerRepository runnerRepository;
    @Mock
    SponsorRepository sponsorRepository;
    @Mock
    LapRepository lapRepository;

    @InjectMocks
    RaceResultsService sut;

    @Test
    void calculatesEverythingCorrectly() {
        var runner = Runner.builder()
                .bonusLaps(4L)
                .build();
        var sponsor1 = Sponsor.builder().oneTimeDonation(new BigDecimal("10.00")).build();
        var sponsor2 = Sponsor.builder().perLapDonation(new BigDecimal("0.10")).build();
        var sponsor3 = Sponsor.builder().oneTimeDonation(new BigDecimal("100.00")).perLapDonation(new BigDecimal("1.00")).build();

        when(sponsorRepository.findByRunner(runner)).thenReturn(List.of(
                sponsor1, sponsor2, sponsor3
        ));
        when(runnerRepository.findAll()).thenReturn(List.of(
                runner));
        when(lapRepository.findByRunner(runner)).thenReturn(List.of(
                Lap.builder().duration(100_000L).build(),
                Lap.builder().duration(400_000L).build(),
                Lap.builder().duration(300_000L).build(),
                Lap.builder().duration(100_000L).build(),
                Lap.builder().duration(100_009L).build()
        ));

        sut.generateResults();

        verify(runnerRepository).saveAndFlush(runner);
        verify(sponsorRepository).saveAndFlush(sponsor1);
        verify(sponsorRepository).saveAndFlush(sponsor2);
        verify(sponsorRepository).saveAndFlush(sponsor3);

        assertThat(runner.getNumberOfLapsRun()).isEqualTo(5);
        assertThat(runner.getAverage()).isEqualTo(new BigDecimal("200.002"));
        assertThat(runner.getNumberOfSponsors()).isEqualTo(3);
        assertThat(runner.getDonations()).isEqualTo(new BigDecimal("119.90"));

        assertThat(sponsor1.getTotalDonation()).isEqualTo(new BigDecimal("10.00"));
        assertThat(sponsor2.getTotalDonation()).isEqualTo(new BigDecimal("0.90"));
        assertThat(sponsor3.getTotalDonation()).isEqualTo(new BigDecimal("109.00"));
    }
}

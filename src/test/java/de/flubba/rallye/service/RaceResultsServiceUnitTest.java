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
        var runnerId = 13344L;
        var runner = Runner.builder()
                .id(runnerId)
                .bonusPoints(4L)
                .build();
        var sponsorTotalDonation = Sponsor.builder().oneTimeDonation(new BigDecimal("10.00")).build();
        var sponsorPerLap = Sponsor.builder().perLapDonation(new BigDecimal("0.11")).build();
        var sponsorBoth = Sponsor.builder()
                .oneTimeDonation(new BigDecimal("100.00"))
                .perLapDonation(new BigDecimal("1.00"))
                .build();

        when(sponsorRepository.findByRunnerId(runnerId)).thenReturn(List.of(
                sponsorTotalDonation, sponsorPerLap, sponsorBoth
        ));
        when(runnerRepository.findAll()).thenReturn(List.of(
                runner));
        when(lapRepository.findByRunnerId(runnerId)).thenReturn(List.of(
                Lap.builder().duration(100_000L).build(),
                Lap.builder().duration(400_000L).build(),
                Lap.builder().duration(300_000L).build(),
                Lap.builder().duration(100_000L).build(),
                Lap.builder().duration(100_009L).build()
        ));

        sut.generateResults();

        verify(runnerRepository).saveAndFlush(runner);
        verify(sponsorRepository).saveAndFlush(sponsorTotalDonation);
        verify(sponsorRepository).saveAndFlush(sponsorPerLap);
        verify(sponsorRepository).saveAndFlush(sponsorBoth);

        assertThat(runner.getNumberOfLapsRun()).isEqualTo(5);
        assertThat(runner.getAverage()).isEqualTo(new BigDecimal("200.002"));
        assertThat(runner.getNumberOfSponsors()).isEqualTo(3);

        assertThat(sponsorTotalDonation.getTotalDonation()).isEqualTo(new BigDecimal("10.00"));
        assertThat(sponsorPerLap.getTotalDonation()).isEqualTo(new BigDecimal("0.59"));
        assertThat(sponsorBoth.getTotalDonation()).isEqualTo(new BigDecimal("105.40"));
        assertThat(runner.getDonations()).isEqualTo(new BigDecimal("115.99"));
    }
}

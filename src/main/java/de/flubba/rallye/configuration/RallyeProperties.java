package de.flubba.rallye.configuration;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.Duration;

@Data
@ConfigurationProperties("rallye")
public class RallyeProperties {
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    BigDecimal shekelToEuroRate;

    @NotNull
    Duration minLapDuration;
}

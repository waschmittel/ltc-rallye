package de.flubba.rallye.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
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

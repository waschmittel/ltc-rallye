package de.flubba.rallye.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@HasDonation
@ToString(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@FieldNameConstants
public class Sponsor {
    @Id
    @GeneratedValue
    @ToString.Include
    private Long id;

    @NotBlank(message = "{sponsor.field.required}")
    @Column(length = 100)
    @Size(min = 2, max = 100, message = "{sponsor.field.size}")
    @ToString.Include
    private String name;

    @Column(length = 100)
    @Size(max = 100, message = "{sponsor.field.maxsize}")
    private String country;

    @Column(length = 100)
    @Size(max = 100, message = "{sponsor.field.maxsize}")
    private String city;

    @Column(length = 100)
    @Size(max = 100, message = "{sponsor.field.maxsize}")
    private String street;

    @DecimalMin(value = "0", message = "{sponsor.donation.one.range}")
    @DecimalMax(value = "10000", message = "{sponsor.donation.one.range}")
    @Column(precision = 8, scale = 2)
    private BigDecimal oneTimeDonation;

    @DecimalMin(value = "0", message = "{sponsor.donation.lap.range}")
    @DecimalMax(value = "50", message = "{sponsor.donation.lap.range}")
    @Column(precision = 8, scale = 2)
    private BigDecimal perLapDonation;

    private BigDecimal totalDonation;

    @NotNull
    private Long runnerId;

}

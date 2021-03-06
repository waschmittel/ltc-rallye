package de.flubba.rallye.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @NotNull(message = "{sponsor.field.required}")
    @Column(length = 100)
    @Size(min = 2, max = 100, message = "{sponsor.field.size}")
    @ToString.Include
    private String name;

    @NotNull(message = "{sponsor.field.required}")
    @Column(length = 100)
    @Size(min = 2, max = 100, message = "{sponsor.field.size}")
    private String country;

    @NotNull(message = "{sponsor.field.required}")
    @Column(length = 100)
    @Size(min = 2, max = 100, message = "{sponsor.field.size}")
    private String city;

    @NotNull(message = "{sponsor.field.required}")
    @Column(length = 100)
    @Size(min = 2, max = 100, message = "{sponsor.field.size}")
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

    @ManyToOne
    private Runner runner;

}

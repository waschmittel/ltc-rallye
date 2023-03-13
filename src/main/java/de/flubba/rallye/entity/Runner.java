package de.flubba.rallye.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@ToString(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@FieldNameConstants
public class Runner {
    public enum Gender {
        male, female, child
    }

    public enum Country {
        Israel, Germany, Switzerland, other
    }

    @Id
    @SequenceGenerator(name = "RUNNER_ID", allocationSize = 1)
    @GeneratedValue(generator = "RUNNER_ID", strategy = GenerationType.SEQUENCE)
    @ToString.Include
    private Long id;

    @NotNull(message = "{runner.name.required}")
    @Column(length = 100, unique = true)
    @Size(min = 5, max = 100, message = "{runner.name.length}")
    @ToString.Include
    private String name;

    @NotNull(message = "{runner.room.required}")
    @Column(length = 20)
    @Size(min = 1, max = 20, message = "{runner.room.length}")
    private String roomNumber;
    private BigDecimal average;
    private BigDecimal donations;

    @NotNull(message = "{runner.gender.required}")
    private Gender gender;

    @NotNull(message = "{runner.country.required}")
    private Country country;

    @OneToMany(mappedBy = "runner", orphanRemoval = true)
    private List<Lap> laps;

    @OneToMany(mappedBy = "runner", orphanRemoval = true)
    private List<Sponsor> sponsors;

    private Integer numberOfSponsors;
    private Integer numberOfLapsRun;
    private Long bonusLaps;

}

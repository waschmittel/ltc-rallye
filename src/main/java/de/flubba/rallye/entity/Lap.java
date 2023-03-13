package de.flubba.rallye.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class Lap {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Long time;

    @NotNull
    private Long duration;

    @ManyToOne
    private Runner runner;

}

package de.flubba.rallye.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@Builder
public class TagAssignment {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(length = 40, unique = true)
    private String tagId;

    @NotNull
    @Column(unique = true)
    private Long runnerId;

}

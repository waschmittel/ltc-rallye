package de.flubba.rallye.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@Builder
@FieldNameConstants
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

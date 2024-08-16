package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class Referendumoption {

    @NotNull
    @ToString.Include
    private String id;

    @NotNull
    @ToString.Include
    private String name;

    @ToString.Include
    private Long position;
}

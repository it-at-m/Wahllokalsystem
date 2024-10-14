package de.muenchen.oss.wahllokalsystem.authservice.domain;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class LoginAttempt extends BaseEntity {

    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9_\\.-]*")
    @Size(min = 1)
    @ToString.Include
    private String username;

    @NotNull
    @ToString.Include
    private int attempts;

    @ToString.Include
    private LocalDateTime lastModified;
}

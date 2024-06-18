package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand;

import static java.sql.Types.VARCHAR;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class Wahlvorstand extends BaseEntity {

    @NotNull
    @ToString.Include
    @JdbcTypeCode(VARCHAR)
    private UUID wahlbezirkID;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<Wahlvorstandsmitglied> mitglieder;

}

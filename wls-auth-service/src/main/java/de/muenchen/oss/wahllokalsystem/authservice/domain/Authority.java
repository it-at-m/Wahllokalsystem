package de.muenchen.oss.wahllokalsystem.authservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Authority extends BaseEntity {

    @ToString.Exclude
    @NaturalId
    private String authority;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "secauthorities_secpermissions", joinColumns = @JoinColumn(name = "authority_oid"), inverseJoinColumns = @JoinColumn(name = "permission_oid")
    )
    private Set<Permission> permissions;

    @ManyToMany(mappedBy = "authorities", cascade = CascadeType.ALL)
    private Set<User> users;
}

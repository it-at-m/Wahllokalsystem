package de.muenchen.oss.wahllokalsystem.authservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "SECPERMISSIONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Permission extends BaseEntity {

    @ToString.Include
    private String permission;

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.muenchen.oss.wahllokalsystem.authservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "SECAUTHORITIES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Authority extends BaseEntity {

    @ToString.Exclude
    private String authority;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "secauthorities_secpermissions", joinColumns = @JoinColumn(name = "authority_oid"), inverseJoinColumns = @JoinColumn(name = "permission_oid")
    )
    private Set<Permission> permissions;

    @ManyToMany(mappedBy = "authorities", cascade = CascadeType.ALL)
    private Set<User> users;
}

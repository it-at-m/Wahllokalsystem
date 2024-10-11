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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "SECUSERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class User extends BaseEntity { //TODO Issue: da der Username nur einmal vorhanden sein darf könnten wir ihn auch zur ID machen

    @NotNull
    @Size(min = 1)
    @ToString.Include
    private String username;

    @ToString.Include
    private String password;

    @Email
    @ToString.Include
    private String email;

    @ToString.Include
    private boolean userEnabled;

    @ToString.Include
    private boolean accountNonLocked;

    @ToString.Include
    private String wahltagID;

    @ToString.Include
    private LocalDate wahltag;

    @ToString.Include
    private String wahlbezirkID;

    @ToString.Include
    private String wahlbezirkNummer;

    @ToString.Include
    private Wahlbezirksart wahlbezirksArt;

    @ToString.Include
    private String pin;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "secusers_secauthorities", joinColumns = { @JoinColumn(name = "user_oid") }, inverseJoinColumns = { @JoinColumn(name = "authority_oid") })
    private Set<Authority> authorities;

    @ToString.Include
    private String wbid_wahlnummer;
}

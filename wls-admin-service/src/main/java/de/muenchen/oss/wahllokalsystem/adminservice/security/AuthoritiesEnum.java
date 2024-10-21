/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.adminservice.security;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Each possible authority in this project is represented by an enum. The enums are used within the
 * {@link PagingAndSortingRepository} in the annotation e.g.
 * {@link PreAuthorize}.
 */
public enum AuthoritiesEnum {
    WLS_ADMIN_SERVICE_READ_THEENTITY, WLS_ADMIN_SERVICE_WRITE_THEENTITY, WLS_ADMIN_SERVICE_DELETE_THEENTITY,
    // add your authorities here and also add these new authorities to sso-authorisation.json.

}

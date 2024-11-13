package de.muenchen.oss.wahllokalsystem.authservice.service;

import java.util.Collection;

public record UsersOfWahltagModel(
        String wahltagID,
        Collection<WahllokalUserInfoModel> users
) {
}

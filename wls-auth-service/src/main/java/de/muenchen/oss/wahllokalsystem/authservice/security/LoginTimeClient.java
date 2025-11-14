package de.muenchen.oss.wahllokalsystem.authservice.security;

import de.muenchen.oss.wahllokalsystem.authservice.service.LegalLoginIntervalModel;

public interface LoginTimeClient {

    LegalLoginIntervalModel getLegalLoginInterval();
}

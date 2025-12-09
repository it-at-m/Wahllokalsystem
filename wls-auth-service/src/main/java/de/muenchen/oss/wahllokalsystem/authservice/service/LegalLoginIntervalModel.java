package de.muenchen.oss.wahllokalsystem.authservice.service;

import java.time.LocalDateTime;

public record LegalLoginIntervalModel(LocalDateTime earliestLogin,LocalDateTime latestLogin){}

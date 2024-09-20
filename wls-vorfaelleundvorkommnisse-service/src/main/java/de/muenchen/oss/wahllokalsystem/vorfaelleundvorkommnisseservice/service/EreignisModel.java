package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import java.time.LocalDateTime;

public record EreignisModel(String beschreibung,
                            LocalDateTime uhrzeit,
                            EreignisartModel ereignisart) {
}

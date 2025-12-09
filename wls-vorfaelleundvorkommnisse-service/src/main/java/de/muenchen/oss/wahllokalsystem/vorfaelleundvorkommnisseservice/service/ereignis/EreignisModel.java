package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EreignisModel(String beschreibung,LocalDateTime uhrzeit,@NotNull EreignisartModel ereignisart){}

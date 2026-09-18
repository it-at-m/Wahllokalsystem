package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisModel;
import java.util.List;

public record MBWErgebnisseModel(
    List<ErgebnisModel> stapelA,
    List<ErgebnisModel> stapelB,
    List<ErgebnisModel> stapelDUngueltig,
    List<ErgebnisModel> stimmenJeKandidatStapelBC) {}

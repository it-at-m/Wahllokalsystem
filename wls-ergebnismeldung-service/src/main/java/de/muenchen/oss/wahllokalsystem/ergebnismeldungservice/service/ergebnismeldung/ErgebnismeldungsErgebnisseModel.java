package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import java.util.Collection;

public record ErgebnismeldungsErgebnisseModel(
    Collection<ErgebnisseModel> gueltigeErgebnisse,
    Collection<ErgebnisseModel> ungueltigeErgebnisse) {}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class WahlartPredicateHolder {

    private final Map<WahlartModel, InvalidErgebnissePredicate> predicatesForWahlarten = Map.ofEntries(
            Map.entry(WahlartModel.BAW, new InvalidErgebnissePredicate(List.of(Stapelart.SRW_BAW_D_UNGUELTIG))),
            Map.entry(WahlartModel.BEB, new InvalidErgebnissePredicate(List.of(Stapelart.VE_BE_D_UNGUELTIG, Stapelart.VE_BE_CBA_UNGUELTIG))),
            Map.entry(WahlartModel.BTW,
                    new InvalidErgebnissePredicate(
                            List.of(Stapelart.BTW_B_II_UNGUELTIG, Stapelart.BTW_B_I_UNGUELTIG, Stapelart.BTW_C_UNGEKENNZEICHNET,
                                    Stapelart.BTW_C_LEER,
                                    Stapelart.BTW_D_I_UNGUELTIG, Stapelart.BTW_D_II_UNGUELTIG))),
            Map.entry(WahlartModel.BZW,
                    new InvalidErgebnissePredicate(
                            List.of(Stapelart.LTW_BZW_C_UNGUELTIG, Stapelart.LTW_BZW_F_UNGUELTIG, Stapelart.LTW_BZW_B, Stapelart.LTW_BZW_E,
                                    Stapelart.LTW_BZW_G_BEIDE, Stapelart.LTW_BZW_G_GROSS, Stapelart.LTW_BZW_G_KLEIN))),
            Map.entry(WahlartModel.EUW,
                    new InvalidErgebnissePredicate(List.of(Stapelart.EUW_C_UNGUELTIG, Stapelart.EUW_B_LEER, Stapelart.EUW_B_UNGEKENNZEICHNET))),
            Map.entry(WahlartModel.LTW,
                    new InvalidErgebnissePredicate(
                            List.of(Stapelart.LTW_BZW_C_UNGUELTIG, Stapelart.LTW_BZW_F_UNGUELTIG, Stapelart.LTW_BZW_B, Stapelart.LTW_BZW_E,
                                    Stapelart.LTW_BZW_G_BEIDE, Stapelart.LTW_BZW_G_GROSS, Stapelart.LTW_BZW_G_KLEIN))),
            Map.entry(WahlartModel.MBW, new InvalidErgebnissePredicate(List.of(Stapelart.MBW_D_UNGUELTIG))),
            Map.entry(WahlartModel.OBW,
                    new InvalidErgebnissePredicate(List.of(Stapelart.OBW_C_UNGUELTIG, Stapelart.OBW_B_LEER, Stapelart.OBW_B_UNGEKENNZEICHNET))),
            Map.entry(WahlartModel.SRW, new InvalidErgebnissePredicate(List.of(Stapelart.SRW_BAW_D_UNGUELTIG))),
            Map.entry(WahlartModel.VE, new InvalidErgebnissePredicate(List.of(Stapelart.VE_BE_D_UNGUELTIG))));

    public Predicate<Stapelart> getPredicateForStapelWithInvalidErgebnisse(final WahlartModel wahlart) {
        if (!predicatesForWahlarten.containsKey(wahlart)) {
            throw new IllegalArgumentException("Wahlart" + wahlart + " wird nicht unterstützt");
        }

        return predicatesForWahlarten.get(wahlart);
    }

    private record InvalidErgebnissePredicate(Collection<Stapelart> stapelRepresentingInvalid) implements Predicate<Stapelart> {

    @Override
    public boolean test(final Stapelart stapelart) {
        return stapelRepresentingInvalid.contains(stapelart);
    }
}}

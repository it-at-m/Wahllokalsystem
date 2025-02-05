package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.electionType;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.DefaultElectionTypeValidator;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.ElectionTypeValidation;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BtwValidationImpl implements ElectionTypeValidation {

    private final DefaultElectionTypeValidator validator;

    @Override
    public boolean supports(final WahlartModel wahlart) {
        return WahlartModel.BTW == wahlart;
    }

    @Override
    public boolean isValidUwb(final String wahlbezirkID, final String wahlID, final Long waehlerverzeichnisNummer, final MeldungsartModel meldungsart)
            throws WlsException {
        val necessaryStacks = buildNecessaryStack();
        return validator.checkValidation(WahlbezirkArtModel.UWB, wahlbezirkID, wahlID, waehlerverzeichnisNummer, necessaryStacks);
    }

    @Override
    public boolean isValidBwb(final String wahlbezirkID, final String wahlID, final Long waehlerverzeichnisNummer, final MeldungsartModel meldungsart)
            throws WlsException {
        val necessaryStacks = buildNecessaryStack();
        necessaryStacks.add(Stapelart.BTW_C_LEER);
        return validator.checkValidation(WahlbezirkArtModel.BWB, wahlbezirkID, wahlID, waehlerverzeichnisNummer, necessaryStacks);
    }

    private List<Stapelart> buildNecessaryStack() {
        List<Stapelart> necessaryStacks = new ArrayList<>();
        necessaryStacks.add(Stapelart.BTW_A);
        necessaryStacks.add(Stapelart.BTW_B_I_GUELTIG);
        necessaryStacks.add(Stapelart.BTW_B_I_UNGUELTIG);
        necessaryStacks.add(Stapelart.BTW_B_II_GUELTIG);
        necessaryStacks.add(Stapelart.BTW_B_II_UNGUELTIG);
        necessaryStacks.add(Stapelart.BTW_C_UNGEKENNZEICHNET);
        necessaryStacks.add(Stapelart.BTW_D_I_GUELTIG);
        necessaryStacks.add(Stapelart.BTW_D_II_GUELTIG);
        necessaryStacks.add(Stapelart.BTW_D_I_UNGUELTIG);
        necessaryStacks.add(Stapelart.BTW_D_II_UNGUELTIG);
        return necessaryStacks;
    }
}
